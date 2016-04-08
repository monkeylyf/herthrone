package com.herthrone.card.hero;

import com.herthrone.base.Hero;
import com.herthrone.card.heropower.LesserHeal;

/**
 * Created by yifeng on 4/2/16.
 */
public class AnduinWrynn extends Hero {

  public final String name = "Anduin Wrynn";
  public final String className = "Priest";

  public AnduinWrynn() {
    //super(new LesserHeal(2));
    super(null);
  }
}
