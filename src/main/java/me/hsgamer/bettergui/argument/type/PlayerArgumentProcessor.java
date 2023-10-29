package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerArgumentProcessor extends SingleArgumentProcessor<OfflinePlayer> {
  private final boolean onlineOnly;

  public PlayerArgumentProcessor(ArgumentProcessorBuilder.Input input) {
    super(input);

    this.onlineOnly = Optional.ofNullable(options.get("online-only"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(false);
  }

  @Override
  public String getValue(String query, UUID uuid) {
    return getObject(uuid).map(this::getArgumentValue).orElse("");
  }

  @Override
  protected Optional<OfflinePlayer> getObject(String name) {
    if (onlineOnly) {
      return Optional.ofNullable(Bukkit.getPlayer(name));
    } else {
      //noinspection deprecation
      return Optional.ofNullable(Bukkit.getOfflinePlayer(name));
    }
  }

  @Override
  protected Stream<OfflinePlayer> getObjectStream() {
    if (onlineOnly) {
      return Arrays.stream(Bukkit.getOnlinePlayers().toArray(new OfflinePlayer[0]));
    } else {
      return Arrays.stream(Bukkit.getOfflinePlayers());
    }
  }

  @Override
  protected String getArgumentValue(OfflinePlayer object) {
    return Optional.ofNullable(object.getName()).orElse("");
  }
}
