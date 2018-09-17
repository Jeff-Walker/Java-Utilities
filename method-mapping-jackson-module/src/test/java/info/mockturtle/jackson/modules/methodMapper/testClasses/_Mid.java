package info.mockturtle.jackson.modules.methodMapper.testClasses;


import java.util.ArrayList;
import java.util.List;

public class _Mid {
  String string;
  int integer;

  _Top top;

  // follows hibernate's one-to-one conventions
  _EmbeddedSingle embeddedSingle;

  List<_Bottom> bottoms = new ArrayList<_Bottom>();

  public List<_Bottom> getBottoms() {
    return bottoms;
  }

  public void addBottom(_Bottom bottom) {
    bottoms.add(bottom);
    bottom.setMid(this);
  }

  public void removeBottom(_Bottom bottom) {
    bottoms.remove(bottom);
    bottom.setMid(null);
  }

  public void setTop(_Top top) {
    this.top = top;
  }

  public _Top getTop() {
    return top;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public int getInteger() {
    return integer;
  }

  public void setInteger(int integer) {
    this.integer = integer;
  }

  public _EmbeddedSingle getEmbeddedSingle() {
    return embeddedSingle;
  }

  public void setEmbeddedSingle(_EmbeddedSingle embeddedSingle) {
    embeddedSingle.setMid(this);
    this.embeddedSingle = embeddedSingle;
  }
}