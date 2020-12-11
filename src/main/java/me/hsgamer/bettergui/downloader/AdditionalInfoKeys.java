package me.hsgamer.bettergui.downloader;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.downloader.object.InfoKey;

import java.util.List;

public class AdditionalInfoKeys {
  public static final InfoKey<String> DESCRIPTION = new InfoKey<String>("description", false) {
    @Override
    public String convertType(Object object) {
      return String.valueOf(object);
    }
  };
  public static final InfoKey<String> SOURCE_CODE = new InfoKey<String>("source-code", false) {
    @Override
    public String convertType(Object object) {
      return String.valueOf(object);
    }
  };
  public static final InfoKey<String> WIKI = new InfoKey<String>("wiki", false) {
    @Override
    public String convertType(Object object) {
      return String.valueOf(object);
    }
  };
  public static final InfoKey<List<String>> AUTHORS = new InfoKey<List<String>>("authors", false) {
    @Override
    public List<String> convertType(Object object) {
      return CollectionUtils.createStringListFromObject(object, false);
    }
  };

  private AdditionalInfoKeys() {
    // EMPTY
  }
}
