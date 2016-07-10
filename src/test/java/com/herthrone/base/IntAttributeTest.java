package com.herthrone.base;

import com.herthrone.object.ValueAttribute;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class IntAttributeTest {

  private static final int FOUR = 4;
  private static final int BUFF = 1;
  private static final int DEBUFF = -2;
  private ValueAttribute attr;

  @Before
  public void setUp() throws Exception {
    attr = new ValueAttribute(IntAttributeTest.FOUR);
  }

  @Test
  public void testGetVal() throws Exception {
    assertThat(attr.value()).isEqualTo(IntAttributeTest.FOUR);
  }

  @Test
  public void testSetBuff() throws Exception {
    attr.getPermanentBuff().increase(IntAttributeTest.BUFF);
    assertThat(attr.value()).isEqualTo(IntAttributeTest.BUFF + IntAttributeTest.FOUR);
  }

  @Test
  public void testResetBuff() throws Exception {
    attr.getPermanentBuff().increase(IntAttributeTest.DEBUFF);
    assertThat(attr.value()).isEqualTo(IntAttributeTest.DEBUFF + IntAttributeTest.FOUR);

    attr.resetBuff();
    assertThat(attr.value()).isEqualTo(IntAttributeTest.FOUR);
  }

  @Test
  public void testReset() throws Exception {
    int decreaseVal = 1;
    attr.decrease(decreaseVal);
    assertThat(attr.value()).isEqualTo(IntAttributeTest.FOUR - decreaseVal);
    attr.getPermanentBuff().increase(IntAttributeTest.BUFF);
    assertThat(attr.value()).isEqualTo(IntAttributeTest.FOUR - decreaseVal + IntAttributeTest.BUFF);

    attr.reset();

    assertThat(attr.value()).isEqualTo(IntAttributeTest.FOUR);
  }

  @Test
  public void testIncrease() throws Exception {
    final int val = 2;
    attr.increase(val);
    assertThat(attr.value()).isEqualTo(IntAttributeTest.FOUR + val);
  }

  @Test
  public void testDecrease() throws Exception {
    final int val = 2;
    attr.decrease(val);
    assertThat(attr.value()).isEqualTo(IntAttributeTest.FOUR - val);
  }
}