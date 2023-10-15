package me.hsgamer.bettergui.util;

import me.hsgamer.hscore.config.PathString;

import java.util.Objects;

public class CaseInsensitivePathString {
  private final PathString pathString;

  public CaseInsensitivePathString(PathString pathString) {
    this.pathString = pathString;
  }

  public PathString getPathString() {
    return pathString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CaseInsensitivePathString that = (CaseInsensitivePathString) o;
    String[] thatPath = that.pathString.getPath();
    String[] thisPath = this.pathString.getPath();
    if (thatPath.length != thisPath.length) return false;
    for (int i = 0; i < thatPath.length; i++) {
      if (!thatPath[i].equalsIgnoreCase(thisPath[i])) return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    String[] thisPath = this.pathString.getPath();
    String[] lowerPath = new String[thisPath.length];
    for (int i = 0; i < thisPath.length; i++) {
      lowerPath[i] = thisPath[i].toLowerCase();
    }
    return Objects.hash((Object) lowerPath);
  }
}
