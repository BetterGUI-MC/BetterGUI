package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class WrappedAirButton extends ActionButton<Button> {
  public WrappedAirButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<Consumer<InventoryClickEvent>, Button> getButtonFunction(Map<String, Object> section) {
    return consumer -> (uuid, actionItem) -> {
      actionItem.setItem(new ItemStack(Material.AIR));
      actionItem.setAction(InventoryClickEvent.class, consumer);
      return true;
    };
  }
}
