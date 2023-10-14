package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.minecraft.gui.button.impl.ListButton;

import java.util.*;

public class WrappedListButton extends BaseWrappedButton<ListButton> {
  public WrappedListButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected ListButton createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
    boolean keepCurrentIndex = Optional.ofNullable(keys.get("keep-current-index")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    List<WrappedButton> childButtons = Optional.ofNullable(keys.get("child"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(o -> ButtonBuilder.INSTANCE.getChildButtons(this, o))
      .orElseGet(Collections::emptyList);
    return new ListButton().addButton(childButtons).setKeepCurrentIndex(keepCurrentIndex);
  }

  @Override
  public void refresh(UUID uuid) {
    if (this.button != null) {
      if (!this.button.isKeepCurrentIndex()) {
        this.button.removeCurrentIndex(uuid);
      }
      this.button.getButtons().stream().filter(WrappedButton.class::isInstance).forEach(button -> ((WrappedButton) button).refresh(uuid));
    }
  }
}
