package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoreArgumentProcessor implements ArgumentProcessor {
  private final ArgumentProcessorBuilder.Input input;
  private final Map<UUID, String> map = new HashMap<>();
  private final int length;
  private final String defaultValue;
  private final boolean clearOnClose;
  private final boolean takeRemaining;
  private final ActionApplier actionApplier;
  private final List<String> suggestions;

  public StoreArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    this.input = input;

    Map<String, Object> options = new CaseInsensitiveStringMap<>(input.options);

    this.length = Optional.ofNullable(options.get("length"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::intValue)
      .orElse(1);

    this.defaultValue = Optional.ofNullable(options.get("default"))
      .map(Objects::toString)
      .orElse("");

    this.clearOnClose = Optional.ofNullable(options.get("clear-on-close"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(false);

    this.takeRemaining = Optional.ofNullable(options.get("take-remaining"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(false);

    this.actionApplier = Optional.ofNullable(options.get("action"))
      .map(MapUtils::castOptionalStringObjectMap)
      .map(map -> new ActionApplier(input.menu, map))
      .orElseGet(() -> new ActionApplier(Collections.emptyList()));

    this.suggestions = Optional.ofNullable(MapUtils.getIfFound(options, "suggestion", "suggest"))
      .map(CollectionUtils::createStringListFromObject)
      .orElse(Collections.emptyList());
  }

  @Override
  public Optional<String[]> process(UUID uuid, String[] args) {
    if (args.length < length) {
      BetterGUI.runBatchRunnable(batchRunnable ->
        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE)
          .addLast(process ->
            actionApplier.accept(uuid, process)
          )
      );
      return Optional.empty();
    }

    if (takeRemaining) {
      map.put(uuid, String.join(" ", args));
      return Optional.of(new String[0]);
    } else if (length > 0) {
      String[] store = Arrays.copyOfRange(args, 0, length);
      map.put(uuid, String.join(" ", store));
      return Optional.of(Arrays.copyOfRange(args, length, args.length));
    } else {
      map.put(uuid, "");
      return Optional.of(args);
    }
  }

  @Override
  public String getValue(String query, UUID uuid) {
    return map.getOrDefault(uuid, defaultValue);
  }

  @Override
  public Pair<Optional<List<String>>, String[]> tabComplete(UUID uuid, String[] args) {
    if (!takeRemaining && length <= 0) {
      return Pair.of(Optional.empty(), args);
    }

    String current;
    String[] remaining;
    if (takeRemaining) {
      current = String.join(" ", args);
      remaining = new String[0];
    } else {
      current = String.join(" ", Arrays.copyOfRange(args, 0, length));
      remaining = Arrays.copyOfRange(args, length, args.length);
    }

    List<String> list = suggestions.stream()
      .flatMap(string -> {
        if (string.toLowerCase(Locale.ROOT).startsWith(current.toLowerCase(Locale.ROOT))) {
          String suggestRemaining = string.substring(current.length());
          return Stream.of(suggestRemaining);
        } else {
          return Stream.empty();
        }
      })
      .collect(Collectors.toList());

    return Pair.of(Optional.of(list), remaining);
  }

  @Override
  public void onClear(UUID uuid) {
    if (clearOnClose) {
      map.remove(uuid);
    }
  }

  @Override
  public void onClearAll() {
    map.clear();
  }

  @Override
  public Menu getMenu() {
    return input.menu;
  }
}
