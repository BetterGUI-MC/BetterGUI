package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.object.BukkitItem;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.minecraft.gui.button.impl.SimpleButton;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class WrappedSimpleButton extends ActionButton<SimpleButton> {
  public WrappedSimpleButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<Consumer<ClickEvent>, SimpleButton> getButtonFunction(Map<String, Object> section) {
    ItemBuilder<ItemStack> itemBuilder = StringReplacerApplier.apply(new BukkitItemBuilder(), this);
    ItemModifierBuilder.INSTANCE.build(section).forEach(itemBuilder::addItemModifier);
    return buttonConsumer -> new SimpleButton(uuid -> new BukkitItem(itemBuilder.build(uuid)), buttonConsumer);
  }
}
