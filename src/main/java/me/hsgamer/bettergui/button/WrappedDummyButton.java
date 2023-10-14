package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.gui.object.BukkitItem;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.minecraft.gui.button.impl.DummyButton;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WrappedDummyButton extends BaseWrappedButton<DummyButton> {
  public WrappedDummyButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected DummyButton createButton(Map<String, Object> section) {
    ItemBuilder<ItemStack> itemBuilder = StringReplacerApplier.apply(new BukkitItemBuilder(), this);
    ItemModifierBuilder.INSTANCE.build(section).forEach(itemBuilder::addItemModifier);
    return new DummyButton(uuid -> new BukkitItem(itemBuilder.build(uuid)));
  }
}
