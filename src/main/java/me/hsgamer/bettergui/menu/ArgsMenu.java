package me.hsgamer.bettergui.menu;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.entity.Player;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.util.*;

public class ArgsMenu extends SimpleMenu {
  private final Map<UUID, String[]> argsPerPlayer = new HashMap<>();
  private final Map<String, Integer> argToIndexMap = new CaseInsensitiveStringHashMap<>();
  private final List<Action> minArgsAction = new LinkedList<>();
  private int minArgs = 0;
  private int registeredArgs = 0;
  private String[] defaultArgs;

  /**
   * Create a new menu
   *
   * @param name the name of the menu
   */
  public ArgsMenu(String name) {
    super(name);

    VariableManager.register(name + "_merged_args", (original, uuid) -> Optional.ofNullable(argsPerPlayer.get(uuid)).map(args -> String.join(" ", args)).orElse(""));
    VariableManager.register(name + "arg_", (original, uuid) -> {
      int index = argToIndexMap.getOrDefault(original, -1);
      if (argsPerPlayer.containsKey(uuid)) {
        String[] playerArgs = argsPerPlayer.get(uuid);
        if (index >= 0 && index < playerArgs.length) {
          return playerArgs[index];
        }
      }
      return "";
    });
  }

  @Override
  public void setFromFile(FileConfiguration file) {
    super.setFromFile(file);
    for (String key : file.getKeys(false)) {
      if (!key.equalsIgnoreCase("menu-settings")) {
        return;
      }

      Map<String, Object> settings = new CaseInsensitiveStringHashMap<>(file.getConfigurationSection(key).getValues(false));

      this.minArgs = Optional.ofNullable(settings.get(Settings.MIN_ARGS)).map(String::valueOf).flatMap(Validate::getNumber).map(BigDecimal::intValue).orElse(this.minArgs);
      this.defaultArgs = Optional.ofNullable(settings.get(Settings.DEFAULT_ARGS)).map(String::valueOf).map(s -> s.split(" ")).orElse(this.defaultArgs);

      Optional.ofNullable(settings.get(Settings.ARGS)).map(o -> CollectionUtils.createStringListFromObject(o, true)).ifPresent(list -> {
        this.registeredArgs = list.size();
        for (int i = 0; i < list.size(); i++) {
          this.argToIndexMap.put(list.get(i), i);
        }
      });
      Optional.ofNullable(settings.get(Settings.MIN_ARGS_ACTION)).ifPresent(o -> this.minArgsAction.addAll(ActionBuilder.INSTANCE.getActions(this, o)));
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
          TaskChain<?> taskChain = BetterGUI.newChain();
          minArgsAction.forEach(action -> action.addToTaskChain(uuid, taskChain));
          taskChain.execute();
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

  private static final class Settings {
    static final String MIN_ARGS = "min-args";
    static final String ARGS = "args";
    static final String MIN_ARGS_ACTION = "min-args-action";
    static final String DEFAULT_ARGS = "default-args";
  }
}
