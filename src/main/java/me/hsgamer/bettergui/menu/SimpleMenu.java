package me.hsgamer.bettergui.menu;

import io.github.projectunified.craftux.simple.SimpleButtonMask;
import io.github.projectunified.craftux.spigot.SpigotInventoryUtil;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.util.SlotUtil;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.config.Config;

import java.util.Map;
import java.util.UUID;

public class SimpleMenu extends BaseInventoryMenu<SimpleButtonMask> {
  public SimpleMenu(Config config) {
    super(config);
  }

  @Override
  protected SimpleButtonMask createMask(Map<String, Object> sectionMap) {
    SimpleButtonMask mask = new SimpleButtonMask();
    for (Map.Entry<String, Object> entry : sectionMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!(value instanceof Map)) {
        continue;
      }
      //noinspection unchecked
      Map<String, Object> values = new CaseInsensitiveStringMap<>((Map<String, Object>) value);
      ButtonBuilder.INSTANCE
        .build(new ButtonBuilder.Input(this, "button_" + key, values))
        .ifPresent(button -> SlotUtil.getSlots(values).forEach(slot -> mask.setButton(SpigotInventoryUtil.toPosition(slot, getInventoryType()), button)));
    }
    return mask;
  }

  @Override
  protected void refreshMaskOnCreate(SimpleButtonMask mask, UUID uuid) {
    mask.getButtonSlotMap().keySet()
      .stream()
      .filter(WrappedButton.class::isInstance)
      .map(WrappedButton.class::cast)
      .forEach(button -> button.refresh(uuid));
  }
}
