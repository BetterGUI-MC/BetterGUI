package me.hsgamer.bettergui.util;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.entity.EntityScheduler;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The utility class for the scheduler
 */
public class SchedulerUtil {
  private static Plugin getProvidingPlugin() {
    return JavaPlugin.getProvidingPlugin(SchedulerUtil.class);
  }

  /**
   * Get the global scheduler
   *
   * @return the global scheduler
   */
  public static GlobalScheduler global() {
    return GlobalScheduler.get(getProvidingPlugin());
  }

  /**
   * Get the async scheduler
   *
   * @return the async scheduler
   */
  public static AsyncScheduler async() {
    return AsyncScheduler.get(getProvidingPlugin());
  }

  /**
   * Get the entity scheduler
   *
   * @param entity the entity
   *
   * @return the entity scheduler
   */
  public static EntityScheduler entity(Entity entity) {
    return EntityScheduler.get(getProvidingPlugin(), entity);
  }
}
