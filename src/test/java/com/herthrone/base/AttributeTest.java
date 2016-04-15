package com.herthrone.base;

import junit.framework.TestCase;

public class AttributeTest extends TestCase {

  private static final int FOUR = 4;
  private static final int BUFF = 1;
  private static final int DEBUFF = -2;
  private Attribute attr;

  public void setUp() throws Exception {
    this.attr = new Attribute(AttributeTest.FOUR);
  }

  public void testGetVal() throws Exception {
    assertEquals(AttributeTest.FOUR, this.attr.getVal());
  }

  public void testSetBuff() throws Exception {
    this.attr.buff(AttributeTest.BUFF);
    assertEquals(AttributeTest.BUFF + AttributeTest.FOUR, this.attr.getVal());
  }

  public void testResetBuff() throws Exception {
    this.attr.buff(AttributeTest.DEBUFF);
    assertEquals(AttributeTest.DEBUFF + AttributeTest.FOUR, this.attr.getVal());

    this.attr.resetBuff();
    assertEquals(AttributeTest.FOUR, this.attr.getVal());
  }

  public void testReset() throws Exception {
    this.attr.decrease(1);
    this.attr.buff(AttributeTest.BUFF);

    this.attr.reset();

    assertEquals(AttributeTest.FOUR, this.attr.getVal());
  }

  public void testIncrease() throws Exception {
    final int val = 2;
    this.attr.increase(val);
    assertEquals(AttributeTest.FOUR + val, this.attr.getVal());
  }

  public void testDecrease() throws Exception {
    final int val = 2;
    this.attr.decrease(val);
    assertEquals(AttributeTest.FOUR - val, this.attr.getVal());
  }
}