package me.hsgamer.bettergui.object.property.icon;

import java.util.function.Consumer;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.icon.impl.ClickCommand;
import me.hsgamer.bettergui.object.property.icon.impl.ClickRequirement;
import me.hsgamer.bettergui.object.property.icon.impl.CloseOnClick;
import me.hsgamer.bettergui.object.property.icon.impl.Cooldown;
import me.hsgamer.bettergui.object.property.icon.impl.ViewRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SimpleIconPropertyBuilder {

  private final Icon icon;
  private ClickCommand command;
  private ClickRequirement clickRequirement;
  private Cooldown cooldown;
  private boolean closeOnClick = false;
  private ViewRequirement viewRequirement;

  public SimpleIconPropertyBuilder(Icon icon) {
    this.icon = icon;
    this.command = new ClickCommand(icon);
    this.clickRequirement = new ClickRequirement(icon);
    this.cooldown = new Cooldown(icon);
    this.viewRequirement = new ViewRequirement(icon);
  }

  public Icon getIcon() {
    return icon;
  }

  public ClickCommand getCommand() {
    return command;
  }

  public ClickRequirement getClickRequirement() {
    return clickRequirement;
  }

  public Cooldown getCooldown() {
    return cooldown;
  }

  public boolean isCloseOnClick() {
    return closeOnClick;
  }

  public ViewRequirement getViewRequirement() {
    return viewRequirement;
  }

  public void init(ConfigurationSection section) {
    PropertyBuilder.loadIconPropertiesFromSection(icon, section).values().forEach((iconProperty -> {
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
  }

  public Consumer<InventoryClickEvent> createClickEvent(Player player) {
    return event -> {
      ClickType clickType = event.getClick();
      if (cooldown.isCooldown(player, clickType)) {
        cooldown.sendFailCommand(player, clickType);
        return;
      }
      if (!clickRequirement.check(player, clickType)) {
        clickRequirement.sendFailCommand(player, clickType);
        return;
      }
      clickRequirement.getCheckedRequirement(player, clickType).ifPresent(iconRequirementSet -> {
        iconRequirementSet.take(player);
        iconRequirementSet.sendCommand(player);
      });
      cooldown.startCooldown(player, clickType);
      if (closeOnClick) {
        player.closeInventory();
      }
      command.getTaskChain(player, clickType).execute();
    };
  }
}
