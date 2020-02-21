package me.hsgamer.bettergui.object.addon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.NoSuchFileException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import me.hsgamer.bettergui.object.addon.AddonDescription.Settings;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class AddonClassLoader extends URLClassLoader {

  private Addon addon;

  public AddonClassLoader(File file, ClassLoader parent)
      throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException, InvalidConfigurationException {
    super(new URL[]{file.toURI().toURL()}, parent);
    JarFile jar = new JarFile(file);
    AddonDescription addonDescription = getAddonDescription(jar);
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

  private AddonDescription getAddonDescription(JarFile jar)
      throws IOException, InvalidConfigurationException {
    // Load addon.yml file
    JarEntry entry = jar.getJarEntry("addon.yml");
    if (entry == null) {
      throw new NoSuchFileException(
          "Addon '" + jar.getName() + "' doesn't contain addon.yml file");
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)));
    YamlConfiguration data = new YamlConfiguration();
    data.load(reader);

    // Load required descriptions
    String name = data.getString(Settings.NAME);
    String version = data.getString(Settings.VERSION);
    String mainClass = data.getString(Settings.CLASSPATH);
    if (name == null) {
      throw new InvalidConfigurationException(
          "Addon '" + jar.getName() + "' doesn't have a name on addon.yml");
    }
    if (version == null) {
      throw new InvalidConfigurationException(
          "Addon '" + jar.getName() + "' doesn't have a version on addon.yml");
    }
    if (mainClass == null) {
      throw new InvalidConfigurationException(
          "Addon '" + jar.getName() + "' doesn't have a main class on addon.yml");
    }
    AddonDescription addonDescription = new AddonDescription(name, version, mainClass);

    // Set optional descriptions
    if (data.isSet(Settings.AUTHORS)) {
      addonDescription.setAuthors(data.getStringList(Settings.AUTHORS));
    }
    if (data.isSet(Settings.DESCRIPTION)) {
      addonDescription.setDescription(data.getString(Settings.DESCRIPTION));
    }
    if (data.isSet(Settings.DEPEND)) {
      addonDescription.setDepends(data.getStringList(Settings.DEPEND));
    }
    if (data.isSet(Settings.SOFT_DEPEND)) {
      addonDescription.setSoftDepends(data.getStringList(Settings.SOFT_DEPEND));
    }

    return addonDescription;
  }

  public Addon getAddon() {
    return addon;
  }
}
