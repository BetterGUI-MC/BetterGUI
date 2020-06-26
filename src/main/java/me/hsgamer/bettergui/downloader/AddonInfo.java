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

  void addAuthor(String author) {
    this.authors.add(author);
  }

  void setSourceLink(String sourceLink) {
    this.sourceLink = sourceLink;
  }

  void setDescription(String description) {
    this.description = description;
  }

  static class Info {

    static final String VERSION = "version";
    static final String DESCRIPTION = "description";
    static final String AUTHORS = "authors";
    static final String SOURCE_LINK = "source-code";
    static final String DIRECT_LINK = "direct-link";

    private Info() {

    }
  }
}
