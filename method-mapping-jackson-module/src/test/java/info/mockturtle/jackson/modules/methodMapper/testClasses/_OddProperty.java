package info.mockturtle.jackson.modules.methodMapper.testClasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class _OddProperty {
  @JsonProperty("@looksLikeMethod")
  String looksLikeMethod;

  public String getLooksLikeMethod() {
    return looksLikeMethod;
  }

  public void setLooksLikeMethod(String looksLikeMethod) {
    this.looksLikeMethod = looksLikeMethod;
  }
}
