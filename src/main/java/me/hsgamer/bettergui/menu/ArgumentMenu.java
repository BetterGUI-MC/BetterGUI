package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.argument.type.StoreArgumentProcessor;
import me.hsgamer.hscore.config.Config;

@Deprecated
public class ArgumentMenu extends SimpleMenu {
  public ArgumentMenu(Config config) {
    super(config);

    BetterGUI.getInstance().getLogger().warning("Argument Menu is deprecated and will be removed in 8.0! Please use Simple Menu with Argument Processor instead.");

    boolean hasArgument = false;
    for (ArgumentProcessor argumentProcessor : getArgumentHandler().getProcessors()) {
      if (argumentProcessor instanceof StoreArgumentProcessor) {
        hasArgument = true;
        break;
      }
    }
    if (!hasArgument) {
      getArgumentHandler().addProcessor(new StoreArgumentProcessor(this));
    }
  }
}
