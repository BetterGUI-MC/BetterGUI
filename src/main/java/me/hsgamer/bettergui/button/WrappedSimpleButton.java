package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.SimpleButton;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class WrappedSimpleButton extends ActionButton {
  public WrappedSimpleButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<BiConsumer<UUID, InventoryClickEvent>, Button> getButtonFunction(Map<String, Object> section) {
    ItemBuilder itemBuilder = StringReplacerApplier.apply(new ItemBuilder(), this);
    ItemModifierBuilder.INSTANCE.build(section).forEach(itemBuilder::addItemModifier);
    return buttonConsumer -> new SimpleButton(itemBuilder::build, buttonConsumer);
  }
}
