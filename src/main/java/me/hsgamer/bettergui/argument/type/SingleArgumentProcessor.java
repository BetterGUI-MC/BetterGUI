package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.action.ActionApplier;
import me.hsgamer.bettergui.api.argument.ArgumentProcessor;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SingleArgumentProcessor<T> implements ArgumentProcessor {
  protected final Map<String, Object> options;
  private final ArgumentProcessorBuilder.Input input;
  private final Map<UUID, T> map = new HashMap<>();
  private final ActionApplier onRequiredActionApplier;
  private final ActionApplier onInvalidActionApplier;

  public SingleArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    this.input = input;
    options = new CaseInsensitiveStringMap<>(input.options);
    this.onRequiredActionApplier = new ActionApplier(input.menu, MapUtils.getIfFoundOrDefault(options, Collections.emptyList(), "required-command", "required-action", "action", "command"));
    this.onInvalidActionApplier = new ActionApplier(input.menu, MapUtils.getIfFoundOrDefault(options, Collections.emptyList(), "invalid-command", "invalid-action"));
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
      BetterGUI.runBatchRunnable(batchRunnable ->
        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE)
          .addLast(process ->
            onRequiredActionApplier.accept(uuid, process)
          )
      );
      return Optional.empty();
    }

    Optional<T> object = getObject(args[0]);
    if (!object.isPresent()) {
      BetterGUI.runBatchRunnable(batchRunnable ->
        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE)
          .addLast(process ->
            onInvalidActionApplier.accept(uuid, process)
          )
      );
      return Optional.empty();
    }

    map.put(uuid, object.get());
    return Optional.of(Arrays.copyOfRange(args, 1, args.length));
  }

  @Override
  public String getValue(String query, UUID uuid) {
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

    List<String> list;
    if (args.length == 1) {
      String query = args[0];
      list = getObjectStream()
        .map(this::getArgumentValue)
        .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(query.toLowerCase(Locale.ROOT)))
        .collect(Collectors.toList());
    } else {
      list = Collections.emptyList();
    }

    return Pair.of(Optional.of(list), Arrays.copyOfRange(args, 1, args.length));
  }

  @Override
  public Menu getMenu() {
    return input.menu;
  }
}
