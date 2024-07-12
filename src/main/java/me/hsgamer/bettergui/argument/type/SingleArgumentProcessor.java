package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.task.BatchRunnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SingleArgumentProcessor<T> extends BaseActionArgumentProcessor {
  private final Map<UUID, T> map = new HashMap<>();
  private final Map<UUID, String> rawMap = new HashMap<>();

  public SingleArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    super(input);
  }

  protected abstract Optional<T> getObject(String name);

  protected abstract Stream<T> getObjectStream();

  protected abstract String getArgumentValue(T object);

  protected abstract String getValue(String query, UUID uuid, T object);

  protected Optional<T> getObject(UUID uuid) {
    return Optional.ofNullable(map.get(uuid));
  }

  @Override
  public Optional<String[]> process(UUID uuid, String[] args) {
    if (args.length == 0) {
      BatchRunnable batchRunnable = new BatchRunnable();
      batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> onRequiredActionApplier.accept(uuid, process));
      SchedulerUtil.async().run(batchRunnable);
      return Optional.empty();
    }

    String raw = args[0];
    rawMap.put(uuid, raw);

    Optional<T> object = getObject(raw);
    if (!object.isPresent()) {
      BatchRunnable batchRunnable = new BatchRunnable();
      batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> onInvalidActionApplier.accept(uuid, process));
      SchedulerUtil.async().run(batchRunnable);
      return Optional.empty();
    }

    map.put(uuid, object.get());
    return Optional.of(Arrays.copyOfRange(args, 1, args.length));
  }

  @Override
  public String getValue(String query, UUID uuid) {
    if (query.equalsIgnoreCase("raw")) {
      return rawMap.getOrDefault(uuid, "");
    }

    Optional<T> object = getObject(uuid);
    if (!object.isPresent()) {
      return "";
    }

    T obj = object.get();
    if (query.isEmpty()) {
      return getArgumentValue(obj);
    } else {
      return getValue(query, uuid, obj);
    }
  }

  @Override
  public Pair<Optional<List<String>>, String[]> tabComplete(UUID uuid, String[] args) {
    if (args.length == 0) {
      return Pair.of(Optional.empty(), args);
    }

    Optional<List<String>> optionalList;
    if (args.length == 1) {
      String query = args[0];
      List<String> list = getObjectStream()
        .map(this::getArgumentValue)
        .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(query.toLowerCase(Locale.ROOT)))
        .collect(Collectors.toList());
      optionalList = Optional.of(list);
    } else {
      optionalList = Optional.empty();
    }

    return Pair.of(optionalList, Arrays.copyOfRange(args, 1, args.length));
  }
}
