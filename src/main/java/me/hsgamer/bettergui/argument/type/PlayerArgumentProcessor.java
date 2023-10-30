package me.hsgamer.bettergui.argument.type;

import me.hsgamer.bettergui.builder.ArgumentProcessorBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
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

    this.onlineOnly = Optional.ofNullable(MapUtils.getIfFound(options, "online-only", "online"))
      .map(String::valueOf)
      .map(Boolean::parseBoolean)
      .orElse(false);
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

  @Override
  protected String getValue(String query, UUID uuid, OfflinePlayer object) {
    if (query.startsWith("papi_")) {
      String papiQuery = query.substring("papi_".length());
      return StringReplacerApplier.replace("%" + papiQuery + "%", object.getUniqueId(), this);
    } else {
      return StringReplacerApplier.replace("{" + query + "}", object.getUniqueId(), this);
    }
  }
}
