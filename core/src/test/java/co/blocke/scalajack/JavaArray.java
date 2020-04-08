package co.blocke.scalajack;

import co.blocke.dottyjack.SJCaptureJava;

import java.math.*;

public class JavaArray {

  private BigDecimal[] bigDecs;
  private BigInteger[] bigInts;
  private Boolean[] booleans;
  private Byte[] bytes;
  private Character[] characters;
  private Double[] doubles;
  private Float[] floats;
  private Integer[] integers;
  private Long[] longs;
  private Short[] shorts;
  private BigInteger[][] multi;

  public BigDecimal[] getBigDecs() { return bigDecs; }
  public void setBigDecs(BigDecimal[] n) { bigDecs = n; }

  public BigInteger[] getBigInts() { return bigInts; }
  public void setBigInts(BigInteger[] n) { bigInts = n; }

  public Boolean[] getBooleans() { return booleans; }
  public void setBooleans(Boolean[] n) { booleans = n; }

  public Byte[] getBytes() { return bytes; }
  public void setBytes(Byte[] n) { bytes = n; }

  public Character[] getCharacters() { return characters; }
  public void setCharacters(Character[] n) { characters = n; }

  public Double[] getDoubles() { return doubles; }
  public void setDoubles(Double[]n) { doubles = n; }

  public Float[] getFloats() { return floats; }
  public void setFloats(Float[] n) { floats = n; }

  public Integer[] getIntegers() { return integers; }
  public void setIntegers(Integer[] n) { integers = n; }

  public Long[] getLongs() { return longs; }
  public void setLongs(Long[] n) { longs = n; }

  public Short[] getShorts() { return shorts; }
  public void setShorts(Short[] n) { shorts = n; }

  public BigInteger[][] getMulti() { return multi; }
  public void setMulti(BigInteger[][] n) { multi = n; }
}