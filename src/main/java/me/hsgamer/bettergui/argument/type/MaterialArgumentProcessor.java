package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class MaterialArgumentProcessor extends SingleArgumentProcessor<Material> {
  public MaterialArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    super(input);
  }

  @Override
  protected Optional<Material> getObject(String name) {
    return Optional.ofNullable(Material.matchMaterial(name));
  }

  @Override
  protected Stream<Material> getObjectStream() {
    return Arrays.stream(Material.values());
  }

  @Override
  protected String getArgumentValue(Material object) {
    return object.name();
  }

  @Override
  protected String getValue(String query, UUID uuid, Material object) {
    return "";
  }
}
