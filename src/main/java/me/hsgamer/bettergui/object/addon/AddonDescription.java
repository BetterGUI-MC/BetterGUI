package me.hsgamer.bettergui.object.addon;

import java.util.ArrayList;
import java.util.List;

public class AddonDescription {

  private String name;
  private String version;
  private String mainClass;
  private String description = "";
  private List<String> authors = new ArrayList<>();

  public AddonDescription(String name, String version, String mainClass) {
    this.name = name;
    this.version = version;
    this.mainClass = mainClass;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getMainClass() {
    return mainClass;
  }

  public List<String> getAuthors() {
    return authors;
  }

  public void setAuthors(List<String> authors) {
    this.authors = authors;
  }

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
