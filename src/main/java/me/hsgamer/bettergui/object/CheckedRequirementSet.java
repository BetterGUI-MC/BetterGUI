package me.hsgamer.bettergui.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

public class CheckedRequirementSet {

  private final Map<UUID, RequirementSet> map = new HashMap<>();

  public void put(Player player, RequirementSet requirementSet) {
    map.put(player.getUniqueId(), requirementSet);
  }

  public Optional<RequirementSet> get(Player player) {
    return Optional.ofNullable(map.remove(player.getUniqueId()));
  }
}
