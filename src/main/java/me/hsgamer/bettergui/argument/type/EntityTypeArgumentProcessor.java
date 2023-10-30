package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class EntityTypeArgumentProcessor extends SingleArgumentProcessor<EntityType> {
  public EntityTypeArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    super(input);
  }

  @Override
  protected Optional<EntityType> getObject(String name) {
    try {
      return Optional.of(EntityType.valueOf(name.toUpperCase()));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  @Override
  protected Stream<EntityType> getObjectStream() {
    return Arrays.stream(EntityType.values());
  }

  @Override
  protected String getArgumentValue(EntityType object) {
    return object.name();
  }

  @Override
  protected String getValue(String query, UUID uuid, EntityType object) {
    return "";
  }
}
