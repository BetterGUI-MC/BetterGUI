package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.AirButton;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class WrappedAirButton extends ActionOnlyButton {
  public WrappedAirButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<BiConsumer<UUID, InventoryClickEvent>, Button> getButtonFunction(Map<String, Object> section) {
    return AirButton::new;
  }
}
