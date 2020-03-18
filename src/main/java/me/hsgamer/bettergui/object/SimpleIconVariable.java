package me.hsgamer.bettergui.object;

import java.util.Optional;

/**
 * A simple extension of IconVariable
 */
public abstract class SimpleIconVariable implements IconVariable {

  private final Icon icon;

  public SimpleIconVariable(Icon icon) {
    this.icon = icon;
  }

  @Override
  public Optional<Icon> getIconInvolved() {
    return Optional.ofNullable(icon);
  }
}
