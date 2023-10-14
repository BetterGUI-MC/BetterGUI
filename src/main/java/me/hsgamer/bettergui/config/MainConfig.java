package me.hsgamer.bettergui.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

import java.io.File;
import java.net.URI;

/**
 * The main class of the plugin
 */
public interface MainConfig {
  @ConfigPath("use-modern-click-type")
  default boolean isModernClickType() {
    return false;
  }

  @ConfigPath("use-legacy-button")
  default boolean isUseLegacyButton() {
    return true;
  }

  @ConfigPath("relative-menu-name")
  default boolean isRelativeMenuName() {
    return false;
  }

  @ConfigPath("trim-menu-file-extension")
  default boolean isTrimMenuFileExtension() {
    return false;
  }

  @ConfigPath("include-menu-in-template")
  default boolean isIncludeMenuInTemplate() {
    return false;
  }

  void reloadConfig();

  /**
   * Get the file name
   *
   * @param rootFolder the root folder
   * @param file       the file
   *
   * @return the file name
   */
  default String getFileName(File rootFolder, File file) {
    String name;
    if (isRelativeMenuName()) {
      URI menusFolderURI = rootFolder.toURI();
      URI fileURI = file.toURI();
      name = menusFolderURI.relativize(fileURI).getPath();
    } else {
      name = file.getName();
    }
    if (isTrimMenuFileExtension()) {
      int index = name.lastIndexOf('.');
      if (index > 0) {
        name = name.substring(0, index);
      }
    }
    return name;
  }
}
