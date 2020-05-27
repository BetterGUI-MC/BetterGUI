package me.hsgamer.bettergui.object.menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.config.impl.MessageConfig;
import me.hsgamer.bettergui.object.LocalVariable;
import me.hsgamer.bettergui.object.LocalVariableManager;
import me.hsgamer.bettergui.object.property.menu.MenuAction;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ArgsMenu extends SimpleMenu {

  private final Map<UUID, List<String>> argsPerPlayer = new HashMap<>();
  private MenuAction minArgsAction;
  private int minArgs = 0;
  private int registeredArgs = 0;
  private boolean clearOnClose = false;
  private String[] defaultArgs;

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
          registeredArgs = args.size();
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
              public String getReplacement(OfflinePlayer executor, String identifier) {
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

        if (settings.containsKey(Settings.CLEAR_ARGS_ON_CLOSE)) {
          clearOnClose = Boolean
              .parseBoolean(String.valueOf(settings.get(Settings.CLEAR_ARGS_ON_CLOSE)));
        }

        if (settings.containsKey(Settings.MIN_ARGS_ACTION)) {
          minArgsAction = new MenuAction(this);
          minArgsAction.setValue(settings.get(Settings.MIN_ARGS_ACTION));
        }

        if (settings.containsKey(Settings.DEFAULT_ARGS)) {
          defaultArgs = String.valueOf(settings.get(Settings.DEFAULT_ARGS)).split(" ");
        }
      }
    }
  }

  @Override
  public boolean createInventory(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();
    if (argsPerPlayer.containsKey(uuid)) {
      if (args.length >= minArgs) {
        argsPerPlayer.put(uuid, Arrays.asList(fillEmptyArgs(args.clone())));
      }
    } else {
      if (args.length < minArgs) {
        if (defaultArgs != null) {
          args = defaultArgs.clone();
        } else {
          if (minArgsAction != null) {
            minArgsAction.getParsed(player).execute();
          }
          return false;
        }
      }
      argsPerPlayer.put(player.getUniqueId(), Arrays.asList(fillEmptyArgs(args.clone())));
    }
    return super.createInventory(player, args, bypass);
  }

  private String[] fillEmptyArgs(String[] args) {
    if (args.length < registeredArgs) {
      args = Arrays.copyOf(args, registeredArgs);
      Arrays.fill(args, MessageConfig.EMPTY_ARG_VALUE.getValue());
    }
    return args;
  }

  @Override
  protected SimpleInventory initInventory(Player player) {
    SimpleInventory inventory = super.initInventory(player);

    if (clearOnClose) {
      inventory.addCloseHandler(event -> argsPerPlayer.remove(event.getPlayer().getUniqueId()));
    }

    return inventory;
  }

  private static final class Settings {

    static final String MIN_ARGS = "min-args";
    static final String ARGS = "args";
    static final String CLEAR_ARGS_ON_CLOSE = "clear-args-on-close";
    static final String MIN_ARGS_ACTION = "min-args-action";
    static final String DEFAULT_ARGS = "default-args";
  }
}
