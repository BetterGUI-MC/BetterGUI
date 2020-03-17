package me.hsgamer.bettergui.util.expression.string;

import me.hsgamer.bettergui.util.expression.StringComparator;

public class EndsWith extends StringComparator {

  public EndsWith() {
    super("STREDW");
  }

  @Override
  public boolean compare(String s1, String s2) {
    return s1.endsWith(s2);
  }
}
