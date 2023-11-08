package me.hsgamer.bettergui.api.addon;

/**
 * An extra interface for {@link me.hsgamer.hscore.expansion.common.Expansion} to be called after all addons are enabled
 */
public interface PostEnable {
  /**
   * Called after all addons are enabled
   */
  void onPostEnable();
}
