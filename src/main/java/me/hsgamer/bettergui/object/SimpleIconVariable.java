package me.hsgamer.bettergui.object;

public abstract class SimpleIconVariable implements IconVariable {

  private Icon icon;

  public SimpleIconVariable(Icon icon) {
    this.icon = icon;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }
}
