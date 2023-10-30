package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NumberArgumentProcessor extends SingleArgumentProcessor<Long> {
  private final List<Long> suggestions;

  public NumberArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    super(input);

    this.suggestions = Optional.ofNullable(MapUtils.getIfFound(options, "suggestion", "suggest"))
      .map(CollectionUtils::createStringListFromObject)
      .orElseGet(() -> Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
      .stream()
      .flatMap(s -> Validate.getNumber(s).map(Stream::of).orElseGet(Stream::empty))
      .map(Number::longValue)
      .collect(Collectors.toList());
  }

  @Override
  protected Optional<Long> getObject(String name) {
    try {
      return Optional.of(Long.parseLong(name));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  @Override
  protected Stream<Long> getObjectStream() {
    return suggestions.stream();
  }

  @Override
  protected String getArgumentValue(Long object) {
    return Long.toString(object);
  }

  @Override
  protected String getValue(String query, UUID uuid, Long object) {
    return "";
  }
}
