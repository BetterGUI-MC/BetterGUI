package me.hsgamer.bettergui.util.expression.string;

import me.hsgamer.bettergui.util.expression.StringComparator;

public class Equals extends StringComparator {

  public Equals() {
    super("STREQ");
  }

  @Override
  public boolean compare(String s1, String s2) {
    return s1.equals(s2);
  }
}
