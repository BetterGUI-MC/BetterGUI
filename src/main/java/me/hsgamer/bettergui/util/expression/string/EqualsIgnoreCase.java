package me.hsgamer.bettergui.util.expression.string;

import me.hsgamer.bettergui.util.expression.StringComparator;

public class EqualsIgnoreCase extends StringComparator {

  public EqualsIgnoreCase() {
    super("STREQIC");
  }

  @Override
  public boolean compare(String s1, String s2) {
    return s1.equalsIgnoreCase(s2);
  }
}
