package me.hsgamer.bettergui.menu;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.util.SlotUtil;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.minecraft.gui.simple.SimpleButtonMap;

import java.util.Map;
import java.util.UUID;

public class SimpleMenu extends BaseInventoryMenu<SimpleButtonMap> {
  public SimpleMenu(Config config) {
    super(config);
  }

  @Override
  protected SimpleButtonMap createButtonMap() {
    SimpleButtonMap buttonMap = new SimpleButtonMap();
    for (Map.Entry<String, Object> entry : configSettings.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!(value instanceof Map)) {
        continue;
      }
      //noinspection unchecked
      Map<String, Object> values = new CaseInsensitiveStringMap<>((Map<String, Object>) value);
      if (key.equalsIgnoreCase("default-icon") || key.equalsIgnoreCase("default-button")) {
        ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(this, "button_" + key, values)).ifPresent(button -> {
          button.init();
          buttonMap.setDefaultButton(button);
        });
      } else {
        ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(this, "button_" + key, values)).ifPresent(button -> {
          button.init();
          SlotUtil.getSlots(values).forEach(slot -> buttonMap.setButton(slot, button));
        });
      }
    }
    return buttonMap;
  }

  @Override
  protected void refreshButtonMapOnCreate(SimpleButtonMap buttonMap, UUID uuid) {
    buttonMap.getButtonSlotMap().values()
      .stream()
      .filter(WrappedButton.class::isInstance)
      .map(WrappedButton.class::cast)
      .forEach(button -> button.refresh(uuid));
  }
}
