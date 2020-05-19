package me.hsgamer.bettergui.util;

import me.hsgamer.bettergui.config.impl.MainConfig;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public enum MenuClickType {

  /**
   * @see ClickType#LEFT
   */
  LEFT(ClickType.LEFT),
  /**
   * @see ClickType#SHIFT_LEFT
   */
  SHIFT_LEFT(ClickType.SHIFT_LEFT),
  /**
   * @see ClickType#RIGHT
   */
  RIGHT(ClickType.RIGHT),
  /**
   * @see ClickType#SHIFT_RIGHT
   */
  SHIFT_RIGHT(ClickType.SHIFT_RIGHT),
  /**
   * @see ClickType#WINDOW_BORDER_LEFT
   */
  WINDOW_BORDER_LEFT(ClickType.WINDOW_BORDER_LEFT),
  /**
   * @see ClickType#WINDOW_BORDER_RIGHT
   */
  WINDOW_BORDER_RIGHT(ClickType.WINDOW_BORDER_RIGHT),
  /**
   * @see ClickType#MIDDLE
   */
  MIDDLE(ClickType.MIDDLE),
  /**
   * @see ClickType#NUMBER_KEY
   */
  NUMBER_KEY(ClickType.NUMBER_KEY),
  /**
   * @see ClickType#DOUBLE_CLICK
   */
  DOUBLE_CLICK(ClickType.DOUBLE_CLICK),
  /**
   * @see ClickType#DROP
   */
  DROP(ClickType.DROP),
  /**
   * @see ClickType#CONTROL_DROP
   */
  CONTROL_DROP(ClickType.CONTROL_DROP),
  /**
   * @see ClickType#CREATIVE
   */
  CREATIVE(ClickType.CREATIVE),
  /**
   * @see ClickType#UNKNOWN
   */
  UNKNOWN(ClickType.UNKNOWN),

  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_1(ClickType.NUMBER_KEY, 0),
  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_2(ClickType.NUMBER_KEY, 1),
  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_3(ClickType.NUMBER_KEY, 2),
  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_4(ClickType.NUMBER_KEY, 3),
  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_5(ClickType.NUMBER_KEY, 4),
  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_6(ClickType.NUMBER_KEY, 5),
  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_7(ClickType.NUMBER_KEY, 6),
  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_8(ClickType.NUMBER_KEY, 7),
  /**
   * Only work if use-modern-click-type is enabled
   */
  NUMBER_KEY_9(ClickType.NUMBER_KEY, 8);
  private final ClickType clickType;
  private final int hotbarSlot;

  MenuClickType(ClickType clickType, int hotbarSlot) {
    this.clickType = clickType;
    this.hotbarSlot = hotbarSlot;
  }

  MenuClickType(ClickType clickType) {
    this(clickType, -1);
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

  @SuppressWarnings("unused")
  public int getHotbarSlot() {
    return hotbarSlot;
  }
}
