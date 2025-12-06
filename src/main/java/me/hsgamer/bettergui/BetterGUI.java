package me.hsgamer.bettergui;

import io.github.projectunified.craftux.spigot.SpigotInventoryUIListener;
import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import io.github.projectunified.minelib.plugin.postenable.PostEnable;
import io.github.projectunified.minelib.plugin.postenable.PostEnableComponent;
import me.hsgamer.bettergui.builder.*;
import me.hsgamer.bettergui.command.*;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.config.TemplateConfig;
import me.hsgamer.bettergui.downloader.AddonDownloader;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.bettergui.manager.MenuCommandManager;
import me.hsgamer.bettergui.manager.MenuManager;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.papi.PlaceholderAPIHook;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main class of the plugin
 */
public final class BetterGUI extends BasePlugin implements PostEnable {
  /**
   * Get the instance of the plugin
   *
   * @return the instance
   */
  public static BetterGUI getInstance() {
    return JavaPlugin.getPlugin(BetterGUI.class);
  }

  @Override
  protected List<Object> getComponents() {
    return Arrays.asList(
      new VariableManager(this),

      new PostEnableComponent(this),

      new ActionBuilder(this),
      new ArgumentProcessorBuilder(),
      new ButtonBuilder(),
      new ConfigBuilder(),
      new InventoryBuilder(),
      new ItemModifierBuilder(),
      new MenuBuilder(),
      new RequirementBuilder(),

      new Permissions(this),
      new CommandComponent(this,
        new OpenCommand(this),
        new MainCommand(this),
        new GetAddonsCommand(this),
        new ReloadCommand(this),
        new GetVariablesCommand(this),
        new GetTemplateButtonsCommand(this)
      ),

      ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this, "config.yml")),
      ConfigGenerator.newInstance(MessageConfig.class, new BukkitConfig(this, "messages.yml")),
      new TemplateConfig(this),

      new AddonManager(this),
      new MenuManager(this),
      new MenuCommandManager(this),
      new AddonDownloader(this),

      new SpigotInventoryUIListener(this),
      new PlaceholderAPIHook(this)
    );
  }

  @Override
  public void load() {
    MessageUtils.setPrefix(() -> get(MessageConfig.class).getPrefix());
  }

  @Override
  public void enable() {
    get(SpigotInventoryUIListener.class).register();
  }

  @Override
  public void postEnable() {
    get(AddonManager.class).call(me.hsgamer.bettergui.api.addon.PostEnable.class, me.hsgamer.bettergui.api.addon.PostEnable::onPostEnable);

    Metrics metrics = new Metrics(this, 6609);
    metrics.addCustomChart(new DrilldownPie("addon", () -> {
      Map<String, Map<String, Integer>> map = new HashMap<>();
      Map<String, Integer> addons = get(AddonManager.class).getExpansionCount();
      map.put(String.valueOf(addons.size()), addons);
      return map;
    }));
    metrics.addCustomChart(new AdvancedPie("addon_count", get(AddonManager.class)::getExpansionCount));

    if (getDescription().getVersion().contains("SNAPSHOT")) {
      getLogger().warning("You are using the development version");
      getLogger().warning("This is not ready for production");
      getLogger().warning("Use in your own risk");
    } else {
      new SpigotVersionChecker(75620).getVersion().thenAccept(output -> {
        if (output.startsWith("Error when getting version:")) {
          getLogger().warning(output);
        } else if (this.getDescription().getVersion().equalsIgnoreCase(output)) {
          getLogger().info("You are using the latest version");
        } else {
          getLogger().warning("There is an available update");
          getLogger().warning("New Version: " + output);
        }
      });
    }
  }

  @Override
  public void disable() {
    get(SpigotInventoryUIListener.class).unregister();
  }
}
