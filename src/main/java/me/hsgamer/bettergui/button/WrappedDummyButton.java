package me.hsgamer.bettergui.button;

import io.github.projectunified.craftitem.core.ItemModifier;
import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.MenuButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class WrappedDummyButton extends MenuButton {
  private final BetterGUI plugin;

  public WrappedDummyButton(BetterGUI plugin, ButtonBuilder.Input input) {
    super(input);
    this.plugin = plugin;
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    List<ItemModifier> itemModifiers = plugin.get(ItemModifierBuilder.class).build(section);
    return (uuid, actionItem) -> {
      UnaryOperator<String> replacer = StringReplacerApplier.getReplaceOperator(uuid, this);
      SpigotItem spigotItem = new SpigotItem(uuid);
      for (ItemModifier itemModifier : itemModifiers) {
        itemModifier.modify(spigotItem, replacer);
      }
      actionItem.setItem(spigotItem.getItemStack());
      return true;
    };
  }
}
