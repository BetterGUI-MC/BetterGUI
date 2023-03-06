package me.hsgamer.bettergui;

final class PluginBuild {
  static final String NAME = "${project.name}";
  static final String VERSION = "${project.version}";
  static final String DESCRIPTION = "${project.description}";
  static final String AUTHOR = "HSGamer";
  static final String WEBSITE = "${project.url}";

  private PluginBuild() {
    throw new IllegalStateException("Utility class");
  }
}
