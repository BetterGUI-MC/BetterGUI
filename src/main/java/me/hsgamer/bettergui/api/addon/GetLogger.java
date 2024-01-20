package me.hsgamer.bettergui.api.addon;

import me.hsgamer.hscore.expansion.extra.expansion.GetClassLoader;
import me.hsgamer.hscore.logger.common.Logger;
import me.hsgamer.hscore.logger.provider.LoggerProvider;

/**
 * An extra interface for {@link me.hsgamer.hscore.expansion.common.Expansion} to get the logger for the addon
 */
public interface GetLogger extends GetClassLoader {
  /**
   * Get the logger
   *
   * @return the logger
   */
  default Logger getLogger() {
    return LoggerProvider.getLogger(this.getExpansionClassLoader().getDescription().getName());
  }
}
