package me.hsgamer.bettergui.object.addon;

import java.util.ArrayList;
import java.util.List;

public class AddonDescription {

  private final String name;
  private final String version;
  private final String mainClass;
  private String description = "";
  private List<String> authors = new ArrayList<>();

  public AddonDescription(String name, String version, String mainClass) {
    this.name = name;
    this.version = version;
    this.mainClass = mainClass;
  }

  /**
   * Get the name of the addon
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Get the version of the addon
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Get the main class of the addon
   *
   * @return the path to the main class
   */
  public String getMainClass() {
    return mainClass;
  }

  /**
   * Get the authors of the addon
   *
   * @return the list of the authors
   */
  public List<String> getAuthors() {
    return authors;
  }

  public void setAuthors(List<String> authors) {
    this.authors = authors;
  }

  /**
   * Get the description of the addon
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public static class Settings {

    public static final String NAME = "name";
    public static final String VERSION = "version";
    public static final String CLASSPATH = "main";
    public static final String AUTHORS = "authors";
    public static final String DESCRIPTION = "description";

    private Settings() {

    }
  }
}
