package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.task.BatchRunnable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class StoreArgumentProcessor implements ArgumentProcessor {
  private final ArgumentProcessorBuilder.Input input;
  private final Map<UUID, String> map = new HashMap<>();
  private final int length;
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

    this.takeRemaining = Optional.ofNullable(options.get("take-remaining"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(false);

    this.actionApplier = new ActionApplier(input.menu, MapUtils.getIfFoundOrDefault(options, Collections.emptyList(), "required-command", "required-action", "action", "command"));

    this.suggestions = Optional.ofNullable(MapUtils.getIfFound(options, "suggestion", "suggest"))
      .map(CollectionUtils::createStringListFromObject)
      .orElse(Collections.emptyList());
  }

  @Override
  public Optional<String[]> process(UUID uuid, String[] args) {
    if (!takeRemaining && length <= 0) {
      return Optional.empty();
    }

    if (length > 0 && args.length < length) {
      BatchRunnable batchRunnable = new BatchRunnable();
      batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> actionApplier.accept(uuid, process));
      Scheduler.current().async().runTask(batchRunnable);
      return Optional.empty();
    }

    if (takeRemaining) {
      map.put(uuid, String.join(" ", args));
      return Optional.of(new String[0]);
    } else {
      String[] store = Arrays.copyOfRange(args, 0, length);
      map.put(uuid, String.join(" ", store));
      return Optional.of(Arrays.copyOfRange(args, length, args.length));
    }
  }

  @Override
  public String getValue(String query, UUID uuid) {
    return map.getOrDefault(uuid, "");
  }

  @Override
  public Pair<Optional<List<String>>, String[]> tabComplete(UUID uuid, String[] args) {
    if (!takeRemaining) {
      if (length <= 0) {
        return Pair.of(Optional.empty(), args);
      } else if (args.length > length) {
        return Pair.of(Optional.empty(), Arrays.copyOfRange(args, length, args.length));
      }
    }

    String current = String.join(" ", args);
    List<String> list = suggestions.stream()
      .filter(string -> string.length() > current.length())
      .filter(string -> string.toLowerCase(Locale.ROOT).startsWith(current.toLowerCase(Locale.ROOT)))
      .map(string -> {
        String[] split = string.split(" ");
        return split[args.length - 1];
      })
      .collect(Collectors.toList());

    return Pair.of(Optional.of(list), new String[0]);
  }

  @Override
  public Menu getMenu() {
    return input.menu;
  }
}
