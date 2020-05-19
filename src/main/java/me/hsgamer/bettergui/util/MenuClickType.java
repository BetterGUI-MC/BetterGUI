package me.hsgamer.bettergui.util;

import me.hsgamer.bettergui.config.impl.MainConfig;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public enum MenuClickType {

  LEFT(ClickType.LEFT),
  SHIFT_LEFT(ClickType.SHIFT_LEFT),
  RIGHT(ClickType.RIGHT),
  SHIFT_RIGHT(ClickType.SHIFT_RIGHT),
  WINDOW_BORDER_LEFT(ClickType.WINDOW_BORDER_LEFT),
  WINDOW_BORDER_RIGHT(ClickType.WINDOW_BORDER_RIGHT),
  MIDDLE(ClickType.MIDDLE),
  NUMBER_KEY(ClickType.NUMBER_KEY),
  DOUBLE_CLICK(ClickType.DOUBLE_CLICK),
  DROP(ClickType.DROP),
  CONTROL_DROP(ClickType.CONTROL_DROP),
  CREATIVE(ClickType.CREATIVE),
  UNKNOWN(ClickType.UNKNOWN),

  // Numbered number key
  NUMBER_KEY_1(ClickType.NUMBER_KEY),
  NUMBER_KEY_2(ClickType.NUMBER_KEY),
  NUMBER_KEY_3(ClickType.NUMBER_KEY),
  NUMBER_KEY_4(ClickType.NUMBER_KEY),
  NUMBER_KEY_5(ClickType.NUMBER_KEY),
  NUMBER_KEY_6(ClickType.NUMBER_KEY),
  NUMBER_KEY_7(ClickType.NUMBER_KEY),
  NUMBER_KEY_8(ClickType.NUMBER_KEY),
  NUMBER_KEY_9(ClickType.NUMBER_KEY);
  private final ClickType clickType;

  MenuClickType(ClickType clickType) {
    this.clickType = clickType;
  }

  public static MenuClickType fromEvent(InventoryClickEvent event) {
    ClickType clickType = event.getClick();
    if (MainConfig.MODERN_CLICK_TYPE.getValue().equals(Boolean.FALSE)
        || !clickType.equals(ClickType.NUMBER_KEY)) {
      return valueOf(clickType.name());
    }
    return valueOf("NUMBER_KEY_" + (event.getHotbarButton() + 1));
  }

  @SuppressWarnings("unused")
  public ClickType getBukkitClickType() {
    return clickType;
  }
}
