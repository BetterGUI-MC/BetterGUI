package me.hsgamer.bettergui.object.icon;

import java.util.Map;
import javax.annotation.Nonnull;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.util.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DummyIcon extends Icon {

  private Map<String, ItemProperty<?, ?>> itemProperties;

  public DummyIcon(String name, Menu menu) {
    super(name, menu);
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    itemProperties = PropertyBuilder.loadItemPropertiesFromSection(this, section);
  }

  @Nonnull
  @Override
  public ClickableItem createClickableItem(Player player) {
    ItemStack itemStack = XMaterial.STONE.parseItem();
    for (ItemProperty<?, ?> itemProperty : itemProperties.values()) {
      itemStack = itemProperty.parse(player, itemStack);
    }
    return new ClickableItem(itemStack, event -> {
    });
  }

  @Nonnull
  @Override
  public ClickableItem updateClickableItem(Player player) {
    return createClickableItem(player);
  }

  public Map<String, ItemProperty<?, ?>> getItemProperties() {
    return itemProperties;
  }
}
