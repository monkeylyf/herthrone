package com.herthrone.base;

import com.herthrone.object.IntAttribute;
import junit.framework.TestCase;

public class IntAttributeTest extends TestCase {

  private static final int FOUR = 4;
  private static final int BUFF = 1;
  private static final int DEBUFF = -2;
  private IntAttribute attr;

  public void setUp() throws Exception {
    attr = new IntAttribute(IntAttributeTest.FOUR);
  }

  public void testGetVal() throws Exception {
    assertEquals(IntAttributeTest.FOUR, attr.value());
  }

  public void testSetBuff() throws Exception {
    attr.buff.permanentBuff.increase(IntAttributeTest.BUFF);
    assertEquals(IntAttributeTest.BUFF + IntAttributeTest.FOUR, attr.value());
  }

  public void testResetBuff() throws Exception {
    attr.buff.permanentBuff.increase(IntAttributeTest.DEBUFF);
    assertEquals(IntAttributeTest.DEBUFF + IntAttributeTest.FOUR, attr.value());

    attr.buff.reset();
    assertEquals(IntAttributeTest.FOUR, attr.value());
  }

  public void testReset() throws Exception {
    int decreaseVal = 1;
    attr.decrease(decreaseVal);
    assertEquals(IntAttributeTest.FOUR - decreaseVal, attr.value());
    attr.buff.permanentBuff.increase(IntAttributeTest.BUFF);
    assertEquals(IntAttributeTest.FOUR - decreaseVal + IntAttributeTest.BUFF, attr.value());

    attr.reset();

    assertEquals(IntAttributeTest.FOUR, attr.value());
  }

  public void testIncrease() throws Exception {
    final int val = 2;
    attr.increase(val);
    assertEquals(IntAttributeTest.FOUR + val, attr.value());
  }

  public void testDecrease() throws Exception {
    final int val = 2;
    attr.decrease(val);
    assertEquals(IntAttributeTest.FOUR - val, attr.value());
  }
}