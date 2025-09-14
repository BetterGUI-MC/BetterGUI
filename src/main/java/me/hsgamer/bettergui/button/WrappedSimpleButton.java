package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.item.BukkitItemBuilder;
import me.hsgamer.hscore.minecraft.item.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class WrappedSimpleButton extends ActionButton<Button> {
  public WrappedSimpleButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<Consumer<InventoryClickEvent>, Button> getButtonFunction(Map<String, Object> section) {
    ItemBuilder<ItemStack> itemBuilder = StringReplacerApplier.apply(new BukkitItemBuilder(), this);
    ItemModifierBuilder.INSTANCE.build(section).forEach(itemBuilder::addItemModifier);
    return buttonConsumer -> (uuid, actionItem) -> {
      actionItem.setItem(itemBuilder.build(uuid));
      actionItem.setAction(InventoryClickEvent.class, buttonConsumer);
      return true;
    };
  }
}
