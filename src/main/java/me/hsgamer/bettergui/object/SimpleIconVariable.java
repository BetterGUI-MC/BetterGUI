package me.hsgamer.bettergui.object;

/**
 * A simple extension of IconVariable
 */
public abstract class SimpleIconVariable implements IconVariable {

  private final Icon icon;

  public SimpleIconVariable(Icon icon) {
    this.icon = icon;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }
}
