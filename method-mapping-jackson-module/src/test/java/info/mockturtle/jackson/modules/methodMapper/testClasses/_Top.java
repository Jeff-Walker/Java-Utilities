package info.mockturtle.jackson.modules.methodMapper.testClasses;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// structure that follows the hibernate many-to-one conventions
public class _Top {
  BigDecimal bigDec;
  int integer;
  float flt;
  String string;

  List<_Mid> mids = new ArrayList<_Mid>();

  public List<_Mid> getMids() {
    return mids;
  }

  public void addMid(_Mid mid) {
    mids.add(mid);
    mid.setTop(this);
  }

  public void removeMid(_Mid mid) {
    mids.remove(mid);
    mid.setTop(null);
  }

  public BigDecimal getBigDec() {
    return bigDec;
  }

  public void setBigDec(BigDecimal bigDec) {
    this.bigDec = bigDec;
  }

  public int getInteger() {
    return integer;
  }

  public void setInteger(int integer) {
    this.integer = integer;
  }

  public float getFlt() {
    return flt;
  }

  public void setFlt(float flt) {
    this.flt = flt;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

}