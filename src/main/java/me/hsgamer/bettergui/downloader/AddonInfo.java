package me.hsgamer.bettergui.downloader;

import java.util.ArrayList;
import java.util.List;

public class AddonInfo {

  private final String name;
  private final String version;
  private final List<String> authors = new ArrayList<>();
  private final String directLink;
  private String description = "";
  private String sourceLink = "";

  AddonInfo(String name, String version, String directLink) {
    this.name = name;
    this.version = version;
    this.directLink = directLink;
  }

  public void addAuthor(String author) {
    this.authors.add(author);
  }

  public void setSourceLink(String sourceLink) {
    this.sourceLink = sourceLink;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public static class Info {

    public static final String VERSION = "version";
    public static final String DESCRIPTION = "description";
    public static final String AUTHORS = "authors";
    public static final String SOURCE_LINK = "source-code";
    public static final String DIRECT_LINK = "direct-link";

    private Info() {

    }
  }
}
