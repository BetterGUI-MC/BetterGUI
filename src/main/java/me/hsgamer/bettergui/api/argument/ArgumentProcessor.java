package me.hsgamer.bettergui.api.argument;

import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.hscore.common.Pair;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The base class for argument processors
 */
public interface ArgumentProcessor extends MenuElement {
  /**
   * Process the arguments
   *
   * @param uuid the UUID of the player
   * @param args the arguments
   *
   * @return the remaining arguments, or empty if the arguments are invalid
   */
  Optional<String[]> process(UUID uuid, String[] args);

  /**
   * Get the value
   *
   * @param query the query
   * @param uuid  the UUID of the player
   *
   * @return the value
   */
  String getValue(String query, UUID uuid);

  /**
   * Get the tab complete for the arguments
   *
   * @param uuid the UUID of the player
   * @param args the arguments
   *
   * @return A pair of the optional suggestions and the remaining arguments. The optional suggestions can be null if the processor should be skipped, then the remaining arguments will be used for the next processor
   */
  default Pair<Optional<List<String>>, String[]> tabComplete(UUID uuid, String[] args) {
    return Pair.of(Optional.empty(), args);
  }
}
