package me.hsgamer.bettergui.object.addon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.PluginConfig;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The main class of the addon
 */
public abstract class Addon {

  private File dataFolder;
  private PluginConfig config;
  private AddonDescription description;
  private AddonClassLoader addonClassLoader;

  public Addon() {
    this.addonClassLoader = (AddonClassLoader) this.getClass().getClassLoader();
  }

  protected AddonClassLoader getClassLoader() {
    return addonClassLoader;
  }

  /**
   * Called when loading the addon
   * <p>
   * WARNING: Don't use this to check if the required plugins are enabled Use "plugin-depend" option
   * from addon.yml
   *
   * @return whether the addon loaded properly
   */
  public boolean onLoad() {
    return true;
  }

  /**
   * Called when enabling the addon
   */
  public void onEnable() {
  }

  /**
   * Called after all addons enabled
   */
  public void onPostEnable() {
  }

  /**
   * Called when disabling the addon
   */
  public void onDisable() {
  }

  /**
   * Get the parent plugin
   *
   * @return the plugin
   */
  protected BetterGUI getPlugin() {
    return BetterGUI.getInstance();
  }

  /**
   * Get the addon's description
   *
   * @return the description
   */
  public AddonDescription getDescription() {
    return description;
  }

  public final void setDescription(AddonDescription description) {
    this.description = description;
  }

  /**
   * Register the command
   *
   * @param command the Command object
   */
  public void registerCommand(BukkitCommand command) {
    getPlugin().getCommandManager().register(command);
  }

  /**
   * Unregister the command
   *
   * @param command the Command object
   */
  public void unregisterCommand(BukkitCommand command) {
    getPlugin().getCommandManager().unregister(command);
  }

  /**
   * Unregister the command
   *
   * @param command the Command label
   */
  public void unregisterCommand(String command) {
    getPlugin().getCommandManager().unregister(command);
  }

  /**
   * Create the config
   */
  public void setupConfig() {
    config = new PluginConfig(getPlugin(), new File(getDataFolder(), "config.yml"));
  }

  /**
   * Get the config
   *
   * @return the config
   */
  public FileConfiguration getConfig() {
    if (config == null) {
      setupConfig();
    }
    return config.getConfig();
  }

  /**
   * Reload the config
   */
  public void reloadConfig() {
    if (config == null) {
      setupConfig();
    } else {
      config.reloadConfig();
    }
  }

  /**
   * Save the config
   */
  public void saveConfig() {
    if (config == null) {
      setupConfig();
    } else {
      config.saveConfig();
    }
  }

  /**
   * Get the addon's folder
   *
   * @return the directory for the addon
   */
  public File getDataFolder() {
    if (dataFolder == null) {
      dataFolder = new File(getPlugin().getDataFolder(),
          "addon" + File.separator + description.getName());
    }
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    return dataFolder;
  }

  /**
   * Copy resource from the addon's jar
   *
   * @param path path to resource
   * @param replace whether it replaces the existed one
   */
  public void saveResource(String path, boolean replace) {
    if (Validate.isNullOrEmpty(path)) {
      throw new IllegalArgumentException("Path cannot be null or empty");
    }

    path = path.replace('\\', '/');
    InputStream in = getResource(path);
    if (in == null) {
      throw new IllegalArgumentException("The embedded resource '" + path + "' cannot be found");
    }

    File outFile = new File(getDataFolder(), path);
    int lastIndex = path.lastIndexOf('/');
    File outDir = new File(getDataFolder(), path.substring(0, Math.max(lastIndex, 0)));

    if (!outDir.exists()) {
      outDir.mkdirs();
    }

    try (OutputStream out = new FileOutputStream(outFile);) {
      if (!outFile.exists() || replace) {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        in.close();
      }
    } catch (IOException ex) {
      getPlugin().getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
    }
  }

  /**
   * Get the resource in the addon's jar
   *
   * @param path path to resource
   * @return The InputStream of the resource, or null if it's not found
   */
  public InputStream getResource(String path) {
    if (path == null) {
      throw new IllegalArgumentException("Filename cannot be null");
    }

    try {
      URL url = getClassLoader().getResource(path);

      if (url == null) {
        return null;
      }

      URLConnection connection = url.openConnection();
      connection.setUseCaches(false);
      return connection.getInputStream();
    } catch (IOException ex) {
      return null;
    }
  }
}
