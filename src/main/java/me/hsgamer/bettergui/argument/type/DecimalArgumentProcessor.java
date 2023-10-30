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

public class DecimalArgumentProcessor extends SingleArgumentProcessor<Double> {
  private final List<Double> suggestions;

  public DecimalArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    super(input);

    this.suggestions = Optional.ofNullable(MapUtils.getIfFound(options, "suggestion", "suggest"))
      .map(CollectionUtils::createStringListFromObject)
      .orElseGet(() -> Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
      .stream()
      .flatMap(s -> Validate.getNumber(s).map(Stream::of).orElseGet(Stream::empty))
      .map(Number::doubleValue)
      .collect(Collectors.toList());
  }

  @Override
  protected Optional<Double> getObject(String name) {
    try {
      return Optional.of(Double.parseDouble(name));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  @Override
  protected Stream<Double> getObjectStream() {
    return suggestions.stream();
  }

  @Override
  protected String getArgumentValue(Double object) {
    return Double.toString(object);
  }

  @Override
  protected String getValue(String query, UUID uuid, Double object) {
    return "";
  }
}
