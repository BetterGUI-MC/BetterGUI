package me.hsgamer.bettergui.requirement;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Checked Requirement Set
 */
public class CheckedRequirementSet {
  private final Map<UUID, RequirementSet> map = new HashMap<>();

  /**
   * Add requirement set
   *
   * @param uuid           the unique id
   * @param requirementSet the requirement set
   */
  public void put(UUID uuid, RequirementSet requirementSet) {
    map.put(uuid, requirementSet);
  }

  /**
   * Get the requirement set
   *
   * @param uuid the unique id
   *
   * @return the requirement set
   */
  public Optional<RequirementSet> get(UUID uuid) {
    return Optional.ofNullable(map.remove(uuid));
  }
}
