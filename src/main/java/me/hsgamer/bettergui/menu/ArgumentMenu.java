package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.config.Config;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArgumentMenu extends SimpleMenu {
  private static final String MIN_ARGS = "min-args";
  private static final String ARGS = "args";
  private static final String MIN_ARGS_ACTION = "min-args-action";
  private static final String DEFAULT_ARGS = "default-args";

  private final Map<UUID, String[]> argsPerPlayer = new ConcurrentHashMap<>();
  private final Map<String, Integer> argToIndexMap = new CaseInsensitiveStringHashMap<>();
  private final ActionApplier minArgActionApplier;
  private int minArgs = 0;
  private int registeredArgs = 0;
  private String[] defaultArgs;

  public ArgumentMenu(Config config) {
    super(config);

    ActionApplier tempMinArgActionApplier = new ActionApplier(Collections.emptyList());
    for (Map.Entry<String, Object> entry : config.getNormalizedValues(false).entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!key.equalsIgnoreCase("menu-settings")) {
        continue;
      }

      if (!(value instanceof Map)) {
        continue;
      }

      //noinspection unchecked
      Map<String, Object> settings = new CaseInsensitiveStringMap<>((Map<String, Object>) value);

      this.minArgs = Optional.ofNullable(settings.get(MIN_ARGS)).map(String::valueOf).flatMap(Validate::getNumber).map(BigDecimal::intValue).orElse(this.minArgs);
      this.defaultArgs = Optional.ofNullable(settings.get(DEFAULT_ARGS)).map(String::valueOf).map(s -> s.split(" ")).orElse(this.defaultArgs);

      Optional.ofNullable(settings.get(ARGS)).map(o -> CollectionUtils.createStringListFromObject(o, true)).ifPresent(list -> {
        this.registeredArgs = list.size();
        for (int i = 0; i < list.size(); i++) {
          this.argToIndexMap.put(list.get(i), i);
        }
      });
      tempMinArgActionApplier = Optional.ofNullable(settings.get(MIN_ARGS_ACTION)).map(o -> new ActionApplier(this, o)).orElse(tempMinArgActionApplier);
    }
    this.minArgActionApplier = tempMinArgActionApplier;

    variableManager.register("merged_args", (original, uuid) -> Optional.ofNullable(argsPerPlayer.get(uuid)).map(args -> String.join(" ", args)).orElse(""));
    variableManager.register("arg_", (original, uuid) -> {
      int index = argToIndexMap.getOrDefault(original, -1);
      if (argsPerPlayer.containsKey(uuid)) {
        String[] playerArgs = argsPerPlayer.get(uuid);
        if (index >= 0 && index < playerArgs.length) {
          return playerArgs[index];
        } else {
          return null;
        }
      }
      return "";
    });
  }

  @Override
  public boolean create(Player player, String[] args, boolean bypass) {
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
          BetterGUI.runBatchRunnable(batchRunnable ->
            batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE)
              .addLast(process ->
                minArgActionApplier.accept(uuid, process)
              )
          );
          return false;
        }
      }
      argsPerPlayer.put(player.getUniqueId(), fillEmptyArgs(args));
    }
    return super.create(player, args, bypass);
  }

  @Override
  public void closeAll() {
    argsPerPlayer.clear();
    argToIndexMap.clear();
  }

  private String[] fillEmptyArgs(String[] args) {
    if (args.length < registeredArgs) {
      String[] clone = Arrays.copyOf(args, registeredArgs);
      Arrays.fill(clone, args.length, clone.length, BetterGUI.getInstance().getMessageConfig().emptyArgValue);
      return clone;
    }
    return args;
  }
}
