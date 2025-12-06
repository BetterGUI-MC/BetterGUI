package me.hsgamer.bettergui.button;

import io.github.projectunified.craftitem.core.ItemModifier;
import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class WrappedSimpleButton extends ActionButton<Button> {
  public WrappedSimpleButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Function<Consumer<InventoryClickEvent>, Button> getButtonFunction(Map<String, Object> section) {
    List<ItemModifier> itemModifiers = BetterGUI.getInstance().get(ItemModifierBuilder.class).build(section);
    return buttonConsumer -> (uuid, actionItem) -> {
      UnaryOperator<String> replacer = StringReplacerApplier.getReplaceOperator(uuid, this);
      SpigotItem item = new SpigotItem(uuid);
      for (ItemModifier itemModifier : itemModifiers) {
        itemModifier.modify(item, replacer);
      }
      actionItem.setItem(item.getItemStack());
      actionItem.setAction(InventoryClickEvent.class, buttonConsumer);
      return true;
    };
  }
}
