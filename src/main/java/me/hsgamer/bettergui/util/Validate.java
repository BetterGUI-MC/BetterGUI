package me.hsgamer.bettergui.util;

import java.util.Collection;

public class Validate {

  private Validate() {

  }

  public static boolean isValidPositiveInteger(String input) {
    try {
      return Integer.parseInt(input) > 0;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isValidInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  public static boolean isClassLoaded(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  public static boolean isNullOrEmpty(Collection<?> list) {
    return list == null || list.isEmpty();
  }
}