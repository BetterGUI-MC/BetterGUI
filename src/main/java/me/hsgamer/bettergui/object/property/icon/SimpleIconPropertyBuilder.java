package me.hsgamer.bettergui.object.property.icon;

import java.util.function.Consumer;
import me.hsgamer.bettergui.builder.PropertyBuilder;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.property.icon.impl.ClickCommand;
import me.hsgamer.bettergui.object.property.icon.impl.ClickRequirement;
import me.hsgamer.bettergui.object.property.icon.impl.CloseOnClick;
import me.hsgamer.bettergui.object.property.icon.impl.ViewRequirement;
import me.hsgamer.bettergui.util.MenuClickType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SimpleIconPropertyBuilder {

  private final Icon icon;
  private ClickCommand command;
  private ClickRequirement clickRequirement;
  private boolean closeOnClick = false;
  private ViewRequirement viewRequirement;

  public SimpleIconPropertyBuilder(Icon icon) {
    this.icon = icon;
    this.command = new ClickCommand(icon);
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
      } else if (iconProperty instanceof ClickRequirement) {
        this.clickRequirement = (ClickRequirement) iconProperty;
      } else if (iconProperty instanceof ViewRequirement) {
        this.viewRequirement = (ViewRequirement) iconProperty;
      } else if (iconProperty instanceof CloseOnClick) {
        this.closeOnClick = ((CloseOnClick) iconProperty).getValue();
      }
    }));
  }

  public Consumer<InventoryClickEvent> createClickEvent(Player player) {
    return event -> {
      MenuClickType clickType = MenuClickType.fromEvent(event);
      if (clickRequirement != null) {
        if (!clickRequirement.check(player, clickType)) {
          clickRequirement.sendFailCommand(player, clickType);
          return;
        }
        clickRequirement.getCheckedRequirement(player, clickType).ifPresent(iconRequirementSet -> {
          iconRequirementSet.take(player);
          iconRequirementSet.sendSuccessCommands(player);
        });
      }
      if (closeOnClick) {
        icon.getMenu().closeInventory(player);
      }
      command.getTaskChain(player, clickType).execute();
    };
  }
}
