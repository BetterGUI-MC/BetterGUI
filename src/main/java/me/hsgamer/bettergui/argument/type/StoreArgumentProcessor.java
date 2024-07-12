package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.task.BatchRunnable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class StoreArgumentProcessor extends BaseActionArgumentProcessor {
  private final Map<UUID, String> map = new HashMap<>();
  private final int length;
  private final boolean takeRemaining;
  private final List<String> suggestions;
  private final boolean checkSuggestion;

  public StoreArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    super(input);

    this.length = Optional.ofNullable(MapUtils.getIfFound(options, "length", "size"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::intValue)
      .orElse(1);

    this.takeRemaining = Optional.ofNullable(MapUtils.getIfFound(options, "take-remaining", "take-remain", "remaining", "remain"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(false);

    this.suggestions = Optional.ofNullable(MapUtils.getIfFound(options, "suggestion", "suggest"))
      .map(CollectionUtils::createStringListFromObject)
      .orElse(Collections.emptyList());

    this.checkSuggestion = Optional.ofNullable(MapUtils.getIfFound(options, "check-suggestion", "check-suggest"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(false);
  }

  @Override
  public Optional<String[]> process(UUID uuid, String[] args) {
    if (!takeRemaining && length <= 0) {
      return Optional.empty();
    }

    if (length > 0 && args.length < length) {
      BatchRunnable batchRunnable = new BatchRunnable();
      batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> onRequiredActionApplier.accept(uuid, process));
      SchedulerUtil.async().run(batchRunnable);
      return Optional.empty();
    }

    String current;
    String[] remaining;
    if (takeRemaining) {
      current = String.join(" ", args);
      remaining = new String[0];
    } else {
      String[] store = Arrays.copyOfRange(args, 0, length);
      current = String.join(" ", store);
      remaining = Arrays.copyOfRange(args, length, args.length);
    }

    if (checkSuggestion && !suggestions.isEmpty() && !suggestions.contains(current)) {
      BatchRunnable batchRunnable = new BatchRunnable();
      batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> onInvalidActionApplier.accept(uuid, process));
      SchedulerUtil.async().run(batchRunnable);
      return Optional.empty();
    }

    map.put(uuid, current);
    return Optional.of(remaining);
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
}
