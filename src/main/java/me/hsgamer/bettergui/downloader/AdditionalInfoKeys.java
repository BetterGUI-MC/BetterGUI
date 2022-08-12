package me.hsgamer.bettergui.downloader;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.downloader.core.object.InfoKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class AdditionalInfoKeys {
  public static final InfoKey<String> DESCRIPTION = new InfoKey<String>("description", "") {
    @Override
    public String convertType(@NotNull Object object) {
      return String.valueOf(object);
    }
  };
  public static final InfoKey<String> SOURCE_CODE = new InfoKey<String>("source-code", "") {
    @Override
    public String convertType(@NotNull Object object) {
      return String.valueOf(object);
    }
  };
  public static final InfoKey<String> WIKI = new InfoKey<String>("wiki", "") {
    @Override
    public String convertType(@NotNull Object object) {
      return String.valueOf(object);
    }
  };
  public static final InfoKey<List<String>> AUTHORS = new InfoKey<List<String>>("authors", Collections.emptyList()) {
    @Override
    public List<String> convertType(@NotNull Object object) {
      return CollectionUtils.createStringListFromObject(object, false);
    }
  };

  private AdditionalInfoKeys() {
    // EMPTY
  }
}
