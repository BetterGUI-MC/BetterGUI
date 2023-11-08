package me.hsgamer.bettergui.api.addon;

/**
 * An extra interface for {@link me.hsgamer.hscore.expansion.common.Expansion} to be called when the plugin is reloaded
 */
public interface Reloadable {
  /**
   * Called when the plugin is reloaded
   */
  void onReload();
}
