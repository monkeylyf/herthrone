package com.herthrone.base;

import com.herthrone.stats.IntAttribute;
import junit.framework.TestCase;

public class IntAttributeTest extends TestCase {

  private static final int FOUR = 4;
  private static final int BUFF = 1;
  private static final int DEBUFF = -2;
  private IntAttribute attr;

  public void setUp() throws Exception {
    this.attr = new IntAttribute(IntAttributeTest.FOUR);
  }

  public void testGetVal() throws Exception {
    assertEquals(IntAttributeTest.FOUR, this.attr.getVal());
  }

  public void testSetBuff() throws Exception {
    this.attr.permIncrease(IntAttributeTest.BUFF);
    assertEquals(IntAttributeTest.BUFF + IntAttributeTest.FOUR, this.attr.getVal());
  }

  public void testResetBuff() throws Exception {
    this.attr.permIncrease(IntAttributeTest.DEBUFF);
    assertEquals(IntAttributeTest.DEBUFF + IntAttributeTest.FOUR, this.attr.getVal());

    this.attr.resetBuff();
    assertEquals(IntAttributeTest.FOUR, this.attr.getVal());
  }

  public void testReset() throws Exception {
    this.attr.decrease(1);
    this.attr.permIncrease(IntAttributeTest.BUFF);

    this.attr.reset();

    assertEquals(IntAttributeTest.FOUR, this.attr.getVal());
  }

  public void testIncrease() throws Exception {
    final int val = 2;
    this.attr.increase(val);
    assertEquals(IntAttributeTest.FOUR + val, this.attr.getVal());
  }

  public void testDecrease() throws Exception {
    final int val = 2;
    this.attr.decrease(val);
    assertEquals(IntAttributeTest.FOUR - val, this.attr.getVal());
  }
}