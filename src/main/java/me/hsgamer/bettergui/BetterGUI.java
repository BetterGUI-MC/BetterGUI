package me.hsgamer.bettergui;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.checker.spigotmc.SimpleVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterGUI extends JavaPlugin {

    private static BetterGUI instance;
    private static TaskChainFactory taskChainFactory;

    private final MainConfig mainConfig = new MainConfig(this);
    private final MessageConfig messageConfig = new MessageConfig(this);

    /**
     * Create a new task chain
     *
     * @param <T> the type of the input
     * @return the task chain
     */
    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    /**
     * Create a new shared task chain
     *
     * @param <T>  the type of the input
     * @param name the name of the task chain
     * @return the task chain
     */
    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    /**
     * Get the instance of the plugin
     *
     * @return the instance
     */
    public static BetterGUI getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        MessageUtils.setPrefix(MessageConfig.PREFIX::getValue);

        if (getDescription().getVersion().contains("SNAPSHOT")) {
            getLogger().warning("You are using the development version");
            getLogger().warning("This is not ready for production");
            getLogger().warning("Use in your own risk");
        } else {
            new SimpleVersionChecker(75620).getVersion().thenAccept(output -> {
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
    public void onEnable() {
        taskChainFactory = BukkitTaskChainFactory.create(this);

        if (Boolean.TRUE.equals(MainConfig.ENABLE_ALTERNATIVE_COMMAND_MANAGER.getValue())) {
            getLogger().info("Enabled alternative command manager");
//      getServer().getPluginManager().registerEvents(new CommandListener(), this);
        }
    }

    /**
     * Enable metrics (BStats)
     */
    private void enableMetrics() {
        Metrics metrics = new Metrics(this, 6609);
//    metrics.addCustomChart(new Metrics.DrilldownPie("addon", () -> {
//      Map<String, Map<String, Integer>> map = new HashMap<>();
//      Map<String, Integer> addons = addonManager.getAddonCount();
//      map.put(String.valueOf(addons.containsKey("Empty") ? 0 : addons.size()), addons);
//      return map;
//    }));
//    metrics.addCustomChart(new Metrics.AdvancedPie("addon_count", addonManager::getAddonCount));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Get the main config
     *
     * @return the main config
     */
    public MainConfig getMainConfig() {
        return mainConfig;
    }

    /**
     * Get the message config
     *
     * @return the message config
     */
    public MessageConfig getMessageConfig() {
        return messageConfig;
    }
}
