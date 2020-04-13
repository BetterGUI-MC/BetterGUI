package me.hsgamer.bettergui.object.menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.LocalVariable;
import me.hsgamer.bettergui.object.LocalVariableManager;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ArgsMenu extends SimpleMenu {

  private final Map<UUID, List<String>> argsPerPlayer = new HashMap<>();
  private int minArgs = 0;

  public ArgsMenu(String name) {
    super(name);
  }

  @Override
  public void setFromFile(FileConfiguration file) {
    super.setFromFile(file);
    for (String key : file.getKeys(false)) {
      if (key.equalsIgnoreCase("menu-settings")) {
        Map<String, Object> settings = new CaseInsensitiveStringMap<>(
            file.getConfigurationSection(key).getValues(false));

        if (settings.containsKey(Settings.MIN_ARGS)) {
          minArgs = (int) settings.get(Settings.MIN_ARGS);
        }

        if (settings.containsKey(Settings.ARGS)) {
          List<String> args = CommonUtils
              .createStringListFromObject(settings.get(Settings.ARGS), true);
          for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            int finalI = i;
            registerVariable("arg_" + arg, new LocalVariable() {
              private final int index = finalI;

              @Override
              public String getIdentifier() {
                return arg;
              }

              @Override
              public LocalVariableManager<?> getInvolved() {
                return getParent();
              }

              @Override
              public String getReplacement(Player executor, String identifier) {
                UUID uuid = executor.getUniqueId();
                if (argsPerPlayer.containsKey(uuid)) {
                  List<String> playerArgs = argsPerPlayer.get(uuid);
                  if (index < playerArgs.size()) {
                    return playerArgs.get(index);
                  }
                }
                return "";
              }
            });
          }
        }
      }
    }
  }

  @Override
  public void createInventory(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();
    if (argsPerPlayer.containsKey(uuid)) {
      if (args.length >= minArgs) {
        argsPerPlayer.put(uuid, Arrays.asList(args));
      }
    } else {
      if (args.length < minArgs) {
        CommonUtils.sendMessage(player,
            BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.NOT_ENOUGH_ARGS));
        return;
      }
      argsPerPlayer.put(player.getUniqueId(), Arrays.asList(args));
    }
    super.createInventory(player, args, bypass);
  }

  private static final class Settings {

    static final String MIN_ARGS = "min-args";
    static final String ARGS = "args";
  }
}
