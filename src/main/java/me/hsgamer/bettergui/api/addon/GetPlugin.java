package me.hsgamer.bettergui.api.addon;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.manager.AddonManager;
import me.hsgamer.hscore.expansion.common.ExpansionManager;
import me.hsgamer.hscore.expansion.extra.expansion.GetClassLoader;

public interface GetPlugin extends GetClassLoader {
  default BetterGUI getPlugin() {
    ExpansionManager expansionManager = getExpansionClassLoader().getManager();
    if (expansionManager instanceof AddonManager) {
      return ((AddonManager) expansionManager).getPlugin();
    } else {
      throw new IllegalStateException("The expansion manager is not an instance of AddonManager");
    }
  }
}
