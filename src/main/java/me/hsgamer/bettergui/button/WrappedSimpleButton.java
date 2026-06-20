package me.hsgamer.bettergui.button;

import io.github.projectunified.craftitem.core.ItemModifier;
import io.github.projectunified.craftitem.spigot.core.SpigotItem;
import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.StringReplacer;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class WrappedSimpleButton extends ActionButton {
  private final BetterGUI plugin;

  public WrappedSimpleButton(BetterGUI plugin, ButtonBuilder.Input input) {
    super(input);
    this.plugin = plugin;
  }

  @Override
  protected Function<Consumer<InventoryClickEvent>, Button> getButtonFunction(Map<String, Object> section) {
    List<ItemModifier> itemModifiers = plugin.get(ItemModifierBuilder.class).build(section);
    return buttonConsumer -> {
      Button button = (uuid, actionItem) -> {
        UnaryOperator<String> replacer = StringReplacerApplier.getReplaceOperator(uuid, this);
        SpigotItem item = new SpigotItem(uuid);
        for (ItemModifier itemModifier : itemModifiers) {
          itemModifier.modify(item, replacer);
        }
        actionItem.setItem(item.getItemStack());
        actionItem.setAction(InventoryClickEvent.class, buttonConsumer);
        return true;
      };
      WrappedPredicateButton.PredicateClickButtonContext predicateClickButtonContext = new WrappedPredicateButton.PredicateClickButtonContext(section, this);
      if (predicateClickButtonContext.exists()) {
        WrappedPredicateButton.PredicateClickButton predicateClickButton = new WrappedPredicateButton.PredicateClickButton(predicateClickButtonContext);
        predicateClickButton.setButton(button);
        return predicateClickButton;
      } else {
        return button;
      }
    };
  }

  @Override
  public void refresh(UUID uuid) {
    if (!(this.button instanceof WrappedPredicateButton.PredicateClickButton)) return;
    ((WrappedPredicateButton.PredicateClickButton) this.button).refresh(uuid);
  }

  @Override
  public StringReplacer getStringReplacer() {
    if (!(this.button instanceof WrappedPredicateButton.PredicateClickButton)) return StringReplacer.DUMMY;
    return ((WrappedPredicateButton.PredicateClickButton) this.button).getStringReplacer();
  }
}
