package me.hsgamer.bettergui.object.icon;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.Property;
import me.hsgamer.bettergui.object.property.icon.SimpleIconPropertyBuilder;
import me.hsgamer.bettergui.object.property.icon.impl.ViewRequirement;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class SimpleIcon extends Icon {

  private Map<String, ItemProperty<?, ?>> itemProperties;
  private Map<String, Property<?>> otherProperties;
  private boolean checkOnlyOnCreation = false;
  private final List<UUID> failToCreate = new ArrayList<>();

  private SimpleIconPropertyBuilder iconPropertyBuilder = new SimpleIconPropertyBuilder(this);

  public SimpleIcon(String name, Menu<?> menu) {
    super(name, menu);
  }

  public SimpleIcon(Icon original) {
    super(original);
    if (original instanceof SimpleIcon) {
      this.itemProperties = ((SimpleIcon) original).itemProperties;
      this.otherProperties = ((SimpleIcon) original).otherProperties;
      this.iconPropertyBuilder = ((SimpleIcon) original).iconPropertyBuilder;
      this.checkOnlyOnCreation = ((SimpleIcon) original).checkOnlyOnCreation;
    }
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    itemProperties = PropertyBuilder.loadItemPropertiesFromSection(this, section);
    iconPropertyBuilder.init(section);
    otherProperties = PropertyBuilder.loadOtherPropertiesFromSection(section);
    section.getKeys(false).forEach(key -> {
      if (key.equalsIgnoreCase("check-only-on-creation")) {
        checkOnlyOnCreation = section.getBoolean(key);
      }
    });
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
    failToCreate.remove(player.getUniqueId());
    ViewRequirement viewRequirement = iconPropertyBuilder.getViewRequirement();
    if (viewRequirement != null) {
      if (!viewRequirement.check(player)) {
        viewRequirement.sendFailCommand(player);
        failToCreate.add(player.getUniqueId());
        return Optional.empty();
      }
      viewRequirement.getCheckedRequirement(player).ifPresent(iconRequirementSet -> {
        iconRequirementSet.take(player);
        iconRequirementSet.sendSuccessCommands(player);
      });
    }
    return Optional.of(getClickableItem(player));
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    if (checkOnlyOnCreation) {
      if (failToCreate.contains(player.getUniqueId())) {
        return Optional.empty();
      }
    } else {
      ViewRequirement viewRequirement = iconPropertyBuilder.getViewRequirement();
      if (viewRequirement != null && !viewRequirement.check(player)) {
        return Optional.empty();
      }
    }
    return Optional.of(getClickableItem(player));
  }

  private ClickableItem getClickableItem(Player player) {
    ItemStack itemStack = XMaterial.STONE.parseItem();
    for (ItemProperty<?, ?> itemProperty : itemProperties.values()) {
      itemStack = itemProperty.parse(player, itemStack);
    }
    return new ClickableItem(itemStack, iconPropertyBuilder.createClickEvent(player));
  }

  public Map<String, ItemProperty<?, ?>> getItemProperties() {
    return itemProperties;
  }

  public Map<String, Property<?>> getOtherProperties() {
    return otherProperties;
  }
}
