package me.hsgamer.bettergui.object.icon;

import com.cryptomorin.xseries.XMaterial;
import java.util.Map;
import java.util.Optional;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.Property;
import me.hsgamer.bettergui.object.property.icon.ClickCommand;
import me.hsgamer.bettergui.object.property.icon.ClickRequirement;
import me.hsgamer.bettergui.object.property.icon.CloseOnClick;
import me.hsgamer.bettergui.object.property.icon.Cooldown;
import me.hsgamer.bettergui.object.property.icon.ViewRequirement;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SimpleIcon extends Icon {

  private Map<String, ItemProperty<?, ?>> itemProperties;
  private Map<String, Property<?>> otherProperties;

  private ClickCommand command = new ClickCommand(this);
  private ClickRequirement clickRequirement = new ClickRequirement(this);
  private Cooldown cooldown = new Cooldown(this);
  private boolean closeOnClick = false;
  private ViewRequirement viewRequirement = new ViewRequirement(this);

  public SimpleIcon(String name, Menu menu) {
    super(name, menu);
  }

  public SimpleIcon(Icon original) {
    super(original);
    if (original instanceof SimpleIcon) {
      this.itemProperties = ((SimpleIcon) original).itemProperties;
      this.otherProperties = ((SimpleIcon) original).otherProperties;
      this.command = ((SimpleIcon) original).command;
      this.clickRequirement = ((SimpleIcon) original).clickRequirement;
      this.viewRequirement = ((SimpleIcon) original).viewRequirement;
      this.cooldown = ((SimpleIcon) original).cooldown;
      this.closeOnClick = ((SimpleIcon) original).closeOnClick;
    }
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    itemProperties = PropertyBuilder.loadItemPropertiesFromSection(this, section);
    PropertyBuilder.loadIconPropertiesFromSection(this, section).values().forEach((iconProperty -> {
      if (iconProperty instanceof ClickCommand) {
        this.command = (ClickCommand) iconProperty;
      }
      if (iconProperty instanceof ClickRequirement) {
        this.clickRequirement = (ClickRequirement) iconProperty;
      }
      if (iconProperty instanceof Cooldown) {
        this.cooldown = (Cooldown) iconProperty;
      }
      if (iconProperty instanceof ViewRequirement) {
        this.viewRequirement = (ViewRequirement) iconProperty;
      }
      if (iconProperty instanceof CloseOnClick) {
        this.closeOnClick = ((CloseOnClick) iconProperty).getValue();
      }
    }));
    otherProperties = PropertyBuilder.loadOtherPropertiesFromSection(section);
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
    if (!viewRequirement.check(player)) {
      return Optional.empty();
    }
    viewRequirement.take(player);
    return Optional.of(getClickableItem(player));
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    if (!viewRequirement.check(player)) {
      return Optional.empty();
    }
    return Optional.of(getClickableItem(player));
  }

  private ClickableItem getClickableItem(Player player) {
    ItemStack itemStack = XMaterial.STONE.parseItem();
    for (ItemProperty<?, ?> itemProperty : itemProperties.values()) {
      itemStack = itemProperty.parse(player, itemStack);
    }
    return new ClickableItem(itemStack, event -> {
      ClickType clickType = event.getClick();
      if (cooldown.isCooldown(player, clickType)) {
        return;
      }
      if (!clickRequirement.check(player, clickType)) {
        return;
      }
      clickRequirement.take(player, clickType);
      cooldown.startCooldown(player, clickType);
      if (closeOnClick) {
        player.closeInventory();
      }
      command.getTaskChain(player, clickType).execute();
    });
  }

  public Map<String, ItemProperty<?, ?>> getItemProperties() {
    return itemProperties;
  }

  public Map<String, Property<?>> getOtherProperties() {
    return otherProperties;
  }
}
