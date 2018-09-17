package info.mockturtle.jackson.modules.methodMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import info.mockturtle.jackson.modules.methodMapper.testClasses._Bottom;
import info.mockturtle.jackson.modules.methodMapper.testClasses._EmbeddedSingle;
import info.mockturtle.jackson.modules.methodMapper.testClasses._Mid;
import info.mockturtle.jackson.modules.methodMapper.testClasses._OddProperty;
import info.mockturtle.jackson.modules.methodMapper.testClasses._Top;

public class Tests {

  private static ObjectMapper buildObjectMapperForTest() {
    ObjectMapper om = new ObjectMapper().registerModule(new MethodMappingModule());
    return om;
  }

  private InputStream getTestResource(String simpleName) {
    return getClass().getResourceAsStream("/methodMapper/" + simpleName);
  }

  /*
   * simple deserialization of a single object from a json object
   */
  @Test
  public void testReadFrom_topx1() throws Exception {
    InputStream is = getTestResource("topx1.json");

    _Top t = buildObjectMapperForTest().readValue(is, _Top.class);

    assertThat(t, notNullValue());
    assertThat(t.getBigDec(), is(new BigDecimal("1.1")));
    assertThat(t.getFlt(), is(1.11F));
    assertThat(t.getInteger(), is(1));
    assertThat(t.getString(), is("str1"));
  }

  /*
   * simple deserialization of a list of object from a json array
   */
  @Test
  public void testReadFrom_topx2() throws Exception {
    InputStream is = getTestResource("topx2.json");

    List<_Top> tl = buildObjectMapperForTest().readValue(is, new TypeReference<List<_Top>>() {});

    assertThat(tl, Matchers.hasSize(2));
    _Top t = tl.get(0);
    assertThat(t.getBigDec(), is(new BigDecimal("1.1")));
    assertThat(t.getFlt(), is(1.11F));
    assertThat(t.getInteger(), is(1));
    assertThat(t.getString(), is("str1"));

    t = tl.get(1);
    assertThat(t.getBigDec(), is(new BigDecimal("2.2")));
    assertThat(t.getFlt(), is(2.22F));
    assertThat(t.getInteger(), is(2));
    assertThat(t.getString(), is("str2"));
  }

  /*
   * Three level add methods each passed json arrays
   */
  @Test
  public void testReadFrom_top1_midx1_bottomx1() throws Exception {
    InputStream is = getTestResource("top_mid_bottom.json");

    List<_Top> tl = buildObjectMapperForTest().readValue(is, new TypeReference<List<_Top>>() {});

    assertThat(tl, hasSize(1));
    _Top t = tl.get(0);
    assertThat(t.getString(), is("str1"));

    assertThat(t.getMids(), hasSize(1));
    _Mid m = t.getMids().get(0);
    assertThat(m.getInteger(), is(11));
    assertThat(m.getString(), is("mid text 11"));
    assertThat(m.getTop(), is(t));

    assertThat(m.getBottoms(), hasSize(1));
    _Bottom b = m.getBottoms().get(0);
    assertThat(b.getInteger(), is(111));
    assertThat(b.getMid(), is(m));
  }

  /*
   * a single object passed to a method.
   */
  @Test
  public void testReadFrom_topx1_midx1_bottomx1_embedded() throws Exception {
    InputStream is = getTestResource("top_mid_embedded.json");

    List<_Top> tl = buildObjectMapperForTest().readValue(is, new TypeReference<List<_Top>>() {});

    assertThat(tl, hasSize(1));
    _Top t = tl.get(0);

    assertThat(t.getMids(), hasSize(1));
    _Mid m = t.getMids().get(0);

    assertThat(m.getEmbeddedSingle(), notNullValue());
    _EmbeddedSingle e = m.getEmbeddedSingle();
    assertThat(e.getName(), is("embedded 11.1"));
    assertThat(e.getMid(), is(m));
  }

  /*
   * Three level add methods each passed json arrays with more than 1 member
   */
  @Test
  public void testReadFrom_top1_midx2_bottomx3() throws Exception {
    InputStream is = getTestResource("top_midx2_bottomx3.json");

    List<_Top> tl = buildObjectMapperForTest().readValue(is, new TypeReference<List<_Top>>() {});

    assertThat(tl, hasSize(1));
    _Top t = tl.get(0);
    assertThat(t.getString(), is("str1"));

    // first mid
    assertThat(t.getMids(), hasSize(2));
    _Mid m = t.getMids().get(0);
    assertThat(m.getInteger(), is(11));
    assertThat(m.getString(), is("mid text 11"));
    assertThat(m.getTop(), is(t));

    assertThat(m.getBottoms(), hasSize(3));
    _Bottom b = m.getBottoms().get(0);
    assertThat(b.getInteger(), is(111));
    assertThat(b.getMid(), is(m));
    b = m.getBottoms().get(1);
    assertThat(b.getInteger(), is(122));
    assertThat(b.getMid(), is(m));
    b = m.getBottoms().get(2);
    assertThat(b.getInteger(), is(133));
    assertThat(b.getMid(), is(m));

    // second mid
    m = t.getMids().get(1);
    assertThat(m.getInteger(), is(22));
    assertThat(m.getString(), is("mid text 22"));
    assertThat(m.getTop(), is(t));

    assertThat(m.getBottoms(), hasSize(3));
    b = m.getBottoms().get(0);
    assertThat(b.getInteger(), is(211));
    assertThat(b.getMid(), is(m));
    b = m.getBottoms().get(1);
    assertThat(b.getInteger(), is(222));
    assertThat(b.getMid(), is(m));
    b = m.getBottoms().get(2);
    assertThat(b.getInteger(), is(233));
    assertThat(b.getMid(), is(m));
  }
  /*
   * Make sure that direct assignment of a json array a property that takes a list works.
   */
  @Test
  public void testReadFrom_topx1_midx1_directAssignment() throws Exception {
    InputStream is = getTestResource("top_midsarray.json");

    List<_Top> tl = buildObjectMapperForTest().readValue(is, new TypeReference<List<_Top>>() {});

    assertThat(tl, hasSize(1));
    _Top t = tl.get(0);

    assertThat(t.getMids(), hasSize(2));
    _Mid m = t.getMids().get(0);
    assertThat(m.getString(), is("mid text 11"));
    assertThat(m.getInteger(), is(11));
    assertThat(m.getTop(), nullValue());

    m = t.getMids().get(1);
    assertThat(m.getString(), is("mid text 12"));
    assertThat(m.getTop(), nullValue());
  }

  /*
   *  if there's a property that looks like one of ours, it still should be handled by the object mapper
   */
  @Test
  public void testRead_propertyLooksLikeMethod() throws Exception {
    InputStream resource = getTestResource("odd_property.json");

    _OddProperty value = buildObjectMapperForTest().readValue(resource, _OddProperty.class);

    assertThat(value, notNullValue());
    assertThat(value.getLooksLikeMethod(), is("but why?"));
  }


  /*
   * if a property looks like a method and our module sees it, we pass it on to the
   * object mapper to handle. In this case, we're not ignoring missing properties.
   */
  @Rule
  public ExpectedException exceptionExpector = ExpectedException.none();
  @Test
  public void testRead_methodNotFound() throws Exception {
    InputStream resource = getTestResource("missing_method.json");

    exceptionExpector.expect(UnrecognizedPropertyException.class);
    exceptionExpector.expectMessage(Matchers.startsWith("Unrecognized field \"@missingMethod\" (class info.mockturtle.jackson.modules.methodMapper.testClasses._Top), not marked as ignorable"));

    buildObjectMapperForTest().readValue(resource, _Top.class);
  }

}
