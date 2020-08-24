package me.hsgamer.bettergui.object.menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.hsgamer.bettergui.config.impl.MessageConfig;
import me.hsgamer.bettergui.object.property.menu.MenuAction;
import me.hsgamer.bettergui.object.variable.LocalVariable;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import me.hsgamer.bettergui.util.CommonUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ArgsMenu extends SimpleMenu {

  private final Map<UUID, String[]> argsPerPlayer = new HashMap<>();
  private MenuAction minArgsAction;
  private int minArgs = 0;
  private int registeredArgs = 0;
  private boolean clearOnClose = false;
  private String[] defaultArgs;

  public ArgsMenu(String name) {
    super(name);

    registerVariable("merged_args", new LocalVariable() {
      @Override
      public String getIdentifier() {
        return "merged_args";
      }

      @Override
      public LocalVariableManager<?> getInvolved() {
        return getParent();
      }

      @Override
      public String getReplacement(OfflinePlayer executor, String identifier) {
        UUID uuid = executor.getUniqueId();
        if (argsPerPlayer.containsKey(uuid)) {
          return String.join(" ", argsPerPlayer.get(uuid));
        }
        return "";
      }
    });
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
            registerVariable("arg_" + arg, new ArgVariable(arg, i));
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
        argsPerPlayer.put(uuid, fillEmptyArgs(args));
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
      argsPerPlayer.put(player.getUniqueId(), fillEmptyArgs(args));
    }
    return super.createInventory(player, args, bypass);
  }

  private String[] fillEmptyArgs(String[] args) {
    if (args.length < registeredArgs) {
      String[] clone = Arrays.copyOf(args, registeredArgs);
      Arrays.fill(clone, args.length, clone.length, MessageConfig.EMPTY_ARG_VALUE.getValue());
      return clone;
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

  private class ArgVariable implements LocalVariable {

    private final int index;
    private final String arg;

    ArgVariable(String arg, int index) {
      this.index = index;
      this.arg = arg;
    }

    @Override
    public String getIdentifier() {
      return "arg_" + arg;
    }

    @Override
    public LocalVariableManager<?> getInvolved() {
      return getParent();
    }

    @Override
    public String getReplacement(OfflinePlayer executor, String identifier) {
      UUID uuid = executor.getUniqueId();
      if (argsPerPlayer.containsKey(uuid)) {
        String[] playerArgs = argsPerPlayer.get(uuid);
        if (index < playerArgs.length) {
          return playerArgs[index];
        }
      }
      return "";
    }
  }
}
