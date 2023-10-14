package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

/**
 * The config for messages
 */
public interface MessageConfig {
  @ConfigPath("prefix")
  default String getPrefix() {
    return "&f[&bBetterGUI&f] ";
  }

  @ConfigPath("no-permission")
  default String getNoPermission() {
    return "&cYou don't have permission to do this";
  }

  @ConfigPath("player-only")
  default String getPlayerOnly() {
    return "&cYou must be a player to do this";
  }

  @ConfigPath("success")
  default String getSuccess() {
    return "&aSuccess";
  }

  @ConfigPath("menu-required")
  default String getMenuRequired() {
    return "&cYou should specify a menu";
  }

  @ConfigPath("invalid-number")
  default String getInvalidNumber() {
    return "&cError converting! {input} is not a valid number";
  }

  @ConfigPath("menu-not-found")
  default String getMenuNotFound() {
    return "&cThat menu does not exist";
  }

  @ConfigPath("player-not-found")
  default String getPlayerNotFound() {
    return "&cThe player is not found. Maybe he is offline or didn't join your server";
  }

  @ConfigPath("empty-arg-value")
  default String getEmptyArgValue() {
    return "/empty/";
  }

  @ConfigPath("have-met-requirement-placeholder")
  default String getHaveMetRequirementPlaceholder() {
    return "Yes";
  }

  void reloadConfig();
}
