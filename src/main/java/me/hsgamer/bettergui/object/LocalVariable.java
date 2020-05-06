package me.hsgamer.bettergui.object;

/**
 * Same as GlobalVariable but this is local
 */
public interface LocalVariable extends GlobalVariable {

  /**
   * @return a string identifying the variable
   */
  String getIdentifier();

  /**
   * @return the variable manager
   */
  LocalVariableManager<?> getInvolved();
}
