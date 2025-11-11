package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class WrappedNullButton extends ActionButton<Button> {
  public WrappedNullButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<Consumer<InventoryClickEvent>, Button> getButtonFunction(Map<String, Object> section) {
    return consumer -> (uuid, actionItem) -> {
      actionItem.setAction(InventoryClickEvent.class, consumer);
      return true;
    };
  }
}
