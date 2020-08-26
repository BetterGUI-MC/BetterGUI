package me.hsgamer.bettergui.object.addon;

import java.util.List;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.hscore.bukkit.addon.object.AddonPath;

public class AdditionalAddonSettings {

  public static final AddonPath<List<String>> AUTHORS = new AddonPath<List<String>>("authors",
      false) {
    @Override
    public List<String> convertType(Object object) {
      return CommonUtils.createStringListFromObject(object, true);
    }
  };
  public static final AddonPath<String> DESCRIPTION = new AddonPath<String>("description", false) {
    @Override
    public String convertType(Object object) {
      return String.valueOf(object);
    }
  };
  public static final AddonPath<List<String>> DEPEND = new AddonPath<List<String>>("depend",
      false) {
    @Override
    public List<String> convertType(Object object) {
      return CommonUtils.createStringListFromObject(object, true);
    }
  };
  public static final AddonPath<List<String>> SOFT_DEPEND = new AddonPath<List<String>>(
      "soft-depend", false) {
    @Override
    public List<String> convertType(Object object) {
      return CommonUtils.createStringListFromObject(object, true);
    }
  };
  public static final AddonPath<List<String>> PLUGIN_DEPEND = new AddonPath<List<String>>(
      "plugin-depend", false) {
    @Override
    public List<String> convertType(Object object) {
      return CommonUtils.createStringListFromObject(object, true);
    }
  };

  private AdditionalAddonSettings() {
    // EMPTY
  }
}
