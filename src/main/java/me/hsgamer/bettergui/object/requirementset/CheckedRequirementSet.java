package me.hsgamer.bettergui.object.requirementset;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * Checked Requirement Set
 */
public class CheckedRequirementSet {

  private final Map<UUID, RequirementSet> map = new HashMap<>();

  /**
   * Add requirement set
   *
   * @param player         the player
   * @param requirementSet the requirement set
   */
  public void put(Player player, RequirementSet requirementSet) {
    map.put(player.getUniqueId(), requirementSet);
  }

  /**
   * Get the requirement set
   *
   * @param player the player
   * @return the requirement set
   */
  public Optional<RequirementSet> get(Player player) {
    return Optional.ofNullable(map.remove(player.getUniqueId()));
  }
}
