package me.hsgamer.bettergui.object.addon;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import me.hsgamer.bettergui.manager.AddonManager;

public class AddonClassLoader extends URLClassLoader {

  private Addon addon;
  private Map<String, Class<?>> classes = new HashMap<>();
  private AddonManager manager;

  public AddonClassLoader(AddonManager manager, File file, AddonDescription addonDescription, ClassLoader parent)
      throws MalformedURLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
    super(new URL[]{file.toURI().toURL()}, parent);
    this.manager = manager;

    Class<?> clazz = Class.forName(addonDescription.getMainClass(), true, this);
    Class<? extends Addon> newClass;
    if (Addon.class.isAssignableFrom(clazz)) {
      newClass = clazz.asSubclass(Addon.class);
    } else {
      throw new ClassCastException("The main class does not extend Addon");
    }
    addon = newClass.getDeclaredConstructor().newInstance();
    addon.setDescription(addonDescription);
  }

  @Override
  protected Class<?> findClass(String name) {
    return findClass(name, true);
  }

  public Class<?> findClass(String name, boolean global) {
    Class<?> clazz = classes.get(name);
    if (clazz == null) {
      if (global) {
        clazz = manager.findClass(name);
      }
      if (clazz == null) {
        try {
          clazz = super.findClass(name);
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
          // IGNORED
        }
        if (clazz != null) {
          manager.putClass(name, clazz);
        }
      }
      if (clazz != null) {
        classes.put(name, clazz);
      }
    }
    return clazz;
  }

  public Addon getAddon() {
    return addon;
  }
}
