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

  public void testGetRawVal() throws Exception {
    assertEquals(AttributeTest.FOUR, this.attr.getRawVal());
  }

  public void testSetBuff() throws Exception {
    this.attr.setBuff(AttributeTest.BUFF);
    assertEquals(AttributeTest.BUFF + AttributeTest.FOUR, this.attr.getVal());
  }

  public void testResetBuff() throws Exception {
    this.attr.setBuff(AttributeTest.DEBUFF);
    assertEquals(AttributeTest.DEBUFF + AttributeTest.FOUR, this.attr.getVal());

    this.attr.resetBuff();
    assertEquals(AttributeTest.FOUR, this.attr.getVal());
  }

  public void testReset() throws Exception {
    this.attr.decrease();
    this.attr.setBuff(AttributeTest.BUFF);
    this.attr.setResetAfterRound();

    this.attr.reset();

    assertEquals(AttributeTest.FOUR, this.attr.getVal());
    assertEquals(AttributeTest.FOUR, this.attr.getRawVal());
  }

  public void testIncreaseToMax() throws Exception {

  }

  public void testIncrease() throws Exception {

  }

  public void testIncrease1() throws Exception {

  }

  public void testDecrease() throws Exception {

  }

  public void testDecrease1() throws Exception {

  }

  public void testSetResetAfterRound() throws Exception {

  }

  public void testResetAfterRound() throws Exception {

  }
}