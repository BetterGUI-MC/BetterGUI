package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.config.Config;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StoreArgumentProcessor implements ArgumentProcessor {
  private static final String MIN_ARGS = "min-args";
  private static final String ARGS = "args";
  private static final String MIN_ARGS_ACTION = "min-args-action";
  private static final String DEFAULT_ARGS = "default-args";
  private static final String CLEAR_ARGS_ON_CLOSE = "clear-args-on-close";

  private final Map<UUID, String[]> argsPerPlayer = new ConcurrentHashMap<>();
  private final Map<String, Integer> argToIndexMap = new CaseInsensitiveStringHashMap<>();
  private final ActionApplier minArgActionApplier;
  private final Menu menu;
  private int minArgs = 0;
  private int registeredArgs = 0;
  private boolean clearArgsOnClose = false;
  private String[] defaultArgs;

  public StoreArgumentProcessor(Menu menu) {
    this.menu = menu;
    Config config = menu.getConfig();

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
      this.clearArgsOnClose = Optional.ofNullable(settings.get(CLEAR_ARGS_ON_CLOSE)).map(String::valueOf).map(Boolean::parseBoolean).orElse(this.clearArgsOnClose);

      Optional.ofNullable(settings.get(ARGS)).map(o -> CollectionUtils.createStringListFromObject(o, true)).ifPresent(list -> {
        this.registeredArgs = list.size();
        for (int i = 0; i < list.size(); i++) {
          this.argToIndexMap.put(list.get(i), i);
        }
      });
      tempMinArgActionApplier = Optional.ofNullable(settings.get(MIN_ARGS_ACTION)).map(o -> new ActionApplier(menu, o)).orElse(tempMinArgActionApplier);
    }
    this.minArgActionApplier = tempMinArgActionApplier;

    menu.getVariableManager().register("merged_args", (original, uuid) -> Optional.ofNullable(argsPerPlayer.get(uuid)).map(args -> String.join(" ", args)).orElse(""));
    menu.getVariableManager().register("arg_", (original, uuid) -> {
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
  public Optional<String[]> process(UUID uuid, String[] args) {
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
          return Optional.empty();
        }
      }
      argsPerPlayer.put(uuid, fillEmptyArgs(args));
    }
    return Optional.of(new String[0]);
  }

  @Override
  public void onClear(UUID uuid) {
    if (clearArgsOnClose) {
      argsPerPlayer.remove(uuid);
    }
  }

  @Override
  public void onClearAll() {
    argsPerPlayer.clear();
    argToIndexMap.clear();
  }

  @Override
  public Menu getMenu() {
    return menu;
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
