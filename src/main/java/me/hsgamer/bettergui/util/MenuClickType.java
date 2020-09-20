package me.hsgamer.bettergui.util;

import me.hsgamer.bettergui.config.MainConfig;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Advanced ClickType
 */
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

  SWAP_OFFHAND("SWAP_OFFHAND"),

  /**
   * NUMBER_KEY with hotbar slot 1
   */
  NUMBER_KEY_1(ClickType.NUMBER_KEY, 0),
  /**
   * NUMBER_KEY with hotbar slot 2
   */
  NUMBER_KEY_2(ClickType.NUMBER_KEY, 1),
  /**
   * NUMBER_KEY with hotbar slot 3
   */
  NUMBER_KEY_3(ClickType.NUMBER_KEY, 2),
  /**
   * NUMBER_KEY with hotbar slot 4
   */
  NUMBER_KEY_4(ClickType.NUMBER_KEY, 3),
  /**
   * NUMBER_KEY with hotbar slot 5
   */
  NUMBER_KEY_5(ClickType.NUMBER_KEY, 4),
  /**
   * NUMBER_KEY with hotbar slot 6
   */
  NUMBER_KEY_6(ClickType.NUMBER_KEY, 5),
  /**
   * NUMBER_KEY with hotbar slot 7
   */
  NUMBER_KEY_7(ClickType.NUMBER_KEY, 6),
  /**
   * NUMBER_KEY with hotbar slot 8
   */
  NUMBER_KEY_8(ClickType.NUMBER_KEY, 7),
  /**
   * UMBER_KEY with hotbar slot 9
   */
  NUMBER_KEY_9(ClickType.NUMBER_KEY, 8);
  private final String clickType;
  private final int hotbarSlot;

  MenuClickType(ClickType clickType, int hotbarSlot) {
    this(clickType.name(), hotbarSlot);
  }

  MenuClickType(ClickType clickType) {
    this(clickType, -1);
  }

  MenuClickType(String clickType, int hotbarSlot) {
    this.clickType = clickType;
    this.hotbarSlot = hotbarSlot;
  }

  MenuClickType(String clickType) {
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

  public String getBukkitClickType() {
    return clickType;
  }

  public int getHotbarSlot() {
    return hotbarSlot;
  }
}
