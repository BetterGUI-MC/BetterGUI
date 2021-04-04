package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.ListButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WrappedListButton extends BaseWrappedButton {

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public WrappedListButton(Menu menu) {
    super(menu);
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section);
    boolean keepCurrentIndex = Optional.ofNullable(keys.get("keep-current-index")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    return Optional.ofNullable(keys.get("child"))
      .filter(o -> o instanceof Map)
      .map(o -> (Map<String, Object>) o)
      .map(o -> new LinkedList<Button>(ButtonBuilder.INSTANCE.getChildButtons(this, o)))
      .map(list -> {
        ListButton button = new ListButton(list);
        button.setKeepCurrentIndex(keepCurrentIndex);
        return button;
      }).orElse(null);
  }

  @Override
  public void refresh(UUID uuid) {
    if (this.button instanceof ListButton) {
      ((ListButton) this.button).getButtons().stream().filter(button -> button instanceof WrappedButton).forEach(button -> ((WrappedButton) button).refresh(uuid));
    }
  }
}
