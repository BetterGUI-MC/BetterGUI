package me.hsgamer.bettergui.object.icon;

import com.cryptomorin.xseries.XMaterial;
import java.util.Map;
import java.util.Optional;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.property.icon.Variable;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DummyIcon extends Icon {

  private Map<String, ItemProperty<?, ?>> itemProperties;

  public DummyIcon(String name, Menu menu) {
    super(name, menu);
  }

  public DummyIcon(Icon original) {
    super(original);
    if (original instanceof DummyIcon) {
      this.itemProperties = ((DummyIcon) original).itemProperties;
    }
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    itemProperties = PropertyBuilder.loadItemPropertiesFromSection(this, section);
    for (String string : section.getKeys(false)) {
      if (string.equalsIgnoreCase("variable")) {
        Variable variable = new Variable(this);
        variable.setValue(section.getConfigurationSection(string));
        break;
      }
    }
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
    ItemStack itemStack = XMaterial.STONE.parseItem();
    for (ItemProperty<?, ?> itemProperty : itemProperties.values()) {
      itemStack = itemProperty.parse(player, itemStack);
    }
    return Optional.of(new ClickableItem(itemStack, event -> {
    }));
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    return createClickableItem(player);
  }

  public Map<String, ItemProperty<?, ?>> getItemProperties() {
    return itemProperties;
  }
}
