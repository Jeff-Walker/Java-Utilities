package info.mockturtle.jackson.modules.methodMapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.LRUMap;


public class MethodMappingModule extends SimpleModule {
  private static final long serialVersionUID = 1L;
  private static final String DefaultMethodLeader = "@";

  protected List<String> ignoredProperties = Collections.emptyList();
  protected String methodLeader = DefaultMethodLeader;

  // since I'm looking through all of a class' methods, keep them around for a bit
  protected LRUMap<String, Method> methodCache = new LRUMap<String, Method>(16, 128);

  @Override
  public void setupModule(SetupContext context) {
    context.addDeserializationProblemHandler(new ProblemHandler());

    super.setupModule(context);
  }

  public MethodMappingModule setIgnoredProperties(String... properties) {
    ignoredProperties = Arrays.asList(properties);
    return this;
  }
  public MethodMappingModule setMethodLeader(String leader) {
    this.methodLeader = leader;
    return this;
  }

  class ProblemHandler extends DeserializationProblemHandler {
    @Override
    public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer,
        Object beanOrClass, String propertyName) throws IOException {
      if (ignoreProperty(propertyName)) {
        return true;
      }
      if (looksLikeMethod(propertyName)) {
        try {
          new MethodMapper(p, beanOrClass, getMethodName(propertyName)).doMap();
          return true;
        } catch (NoSuchMethodError e) {
          return false;
        } catch (Exception e) {
          throw new IOException(e);
        }
      }
      return false;
    }

    private boolean ignoreProperty(String propertyName) {
      return ignoredProperties.contains(propertyName);
    }

    protected String getMethodName(String propertyName) {
      return propertyName.substring(methodLeader.length());
    }
    protected boolean looksLikeMethod(String propertyName) {
      return propertyName.startsWith(methodLeader);
    }

    class MethodMapper {
      private Class<?> beanClass;
      private Object bean;
      private JsonParser jsonParser;
      private String methodName;


      public MethodMapper(JsonParser jsonParser, Object beanOrClass, String methodName) {
        this.jsonParser = jsonParser;
        this.methodName = methodName;

        this.beanClass = getBeanClass(beanOrClass);
        this.bean = getBean(beanOrClass);

      }

      public void doMap() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method bestMatch = getMatchingMethod();
        JsonNode tree = jsonParser.readValueAsTree();
        Class<?> parameterType = bestMatch.getParameterTypes()[0];

        if (tree.isArray()) {
          mapArray(asArrayNode(tree), bestMatch, parameterType);
        } else {
          mapObject(asObjectNode(tree), bestMatch, parameterType);
        }
      }

      private void mapArray(ArrayNode node, Method bestMatch, Class<?> parameterType) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (TreeNode n : node) {
          mapObject(asObjectNode(n), bestMatch, parameterType);
        }
      }

      private void mapObject(ObjectNode node, Method bestMatch, Class<?> parameterType) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object value = jsonParser.getCodec().treeToValue(node, parameterType);
        bestMatch.invoke(bean, value);
      }

      private ObjectNode asObjectNode(TreeNode n) {
        if (n.isObject()) {
          return (ObjectNode)n;
        }
        throw new IllegalArgumentException("expected an object node. got " + n);
      }
      private ArrayNode asArrayNode(TreeNode n) {
        if (n.isArray()) {
          return (ArrayNode)n;
        }
        throw new IllegalArgumentException("expected an array node. got " + n);
      }

      private Method getMatchingMethod() {
        String key = methodKey();
        Method method = methodCache.get(key);
        if (method == null) {
          method = findBestMatch();
        }
        return method;
      }
      private String methodKey() {
        return methodName + "_" + beanClass.getName();
      }
      private Method findBestMatch() {
        for (Method m : beanClass.getMethods()) {
          if (m.getName().equals(methodName)) {
            if (m.getParameterCount() == 1) {
              methodCache.putIfAbsent(methodKey(), m);
              return m;
            }
          }
        }
        throw new NoSuchMethodError("didn't find a method with name '" + methodName + "' on " + beanClass.getCanonicalName() + ", with 1 parameter)");
      }

      private Class<?> getBeanClass(Object beanOrClass) {
        return beanOrClass instanceof Class ? (Class<?>) beanOrClass : beanOrClass.getClass();
      }
      private Object getBean(Object beanOrClass) {
        if (beanOrClass instanceof Class) {
          throw new IllegalStateException("don't have an instance, not sure what to do. got " + beanOrClass);
        }
        return beanOrClass;
      }

    }

  }
}