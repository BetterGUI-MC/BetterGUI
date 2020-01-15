package me.hsgamer.bettergui.object;

public abstract class IconVariable implements Variable {

  private Icon icon;

  public IconVariable(Icon icon) {
    this.icon = icon;
  }

  public Icon getIcon() {
    return icon;
  }
}
