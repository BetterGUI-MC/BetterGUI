package me.hsgamer.bettergui.util;

/**
 * The utility class for command
 */
public final class CommandUtil {
  private CommandUtil() {
    // EMPTY
  }

  /**
   * Normalize the command
   *
   * @param command the command
   *
   * @return the normalized command
   */
  public static String normalizeCommand(String command) {
    return command.startsWith("/") ? command : "/" + command;
  }
}
