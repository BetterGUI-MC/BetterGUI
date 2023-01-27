package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.gui.object.BukkitItem;
import me.hsgamer.hscore.minecraft.gui.button.impl.SimpleButton;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class WrappedAirButton extends ActionButton<SimpleButton> {
  public WrappedAirButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<Consumer<ClickEvent>, SimpleButton> getButtonFunction(Map<String, Object> section) {
    return consumer -> new SimpleButton(new BukkitItem(new ItemStack(Material.AIR)), consumer);
  }
}
