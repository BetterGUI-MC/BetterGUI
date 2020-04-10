package me.hsgamer.bettergui.object.addon;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import me.hsgamer.bettergui.manager.AddonManager;

public class AddonClassLoader extends URLClassLoader {

  private final Addon addon;
  private final File file;
  private final AddonManager manager;

  public AddonClassLoader(AddonManager manager, File file, AddonDescription addonDescription,
      ClassLoader parent)
      throws MalformedURLException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
    super(new URL[]{file.toURI().toURL()}, parent);
    this.manager = manager;
    this.file = file;

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

  public Addon getAddon() {
    return addon;
  }

  @Override
  protected Class<?> findClass(String name) {
    return findClass(name, true);
  }

  public Class<?> findClass(String name, boolean global) {
    Class<?> clazz = null;
    if (global) {
      clazz = manager.findClass(addon, name);
    }
    if (clazz == null) {
      try {
        clazz = super.findClass(name);
      } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        // IGNORED
      }
    }
    return clazz;
  }

  public File getFile() {
    return file;
  }
}
