package me.hsgamer.bettergui.object.icon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.ParentIcon;
import me.hsgamer.bettergui.object.property.icon.impl.ViewRequirement;
import me.hsgamer.bettergui.util.ExpressionUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class AnimatedIcon extends Icon implements ParentIcon {

  private final Set<UUID> failToCreate = new HashSet<>();
  private final List<Icon> icons = new ArrayList<>();
  private final Map<UUID, Integer> currentIndexMap = new HashMap<>();
  private final Map<UUID, Integer> currentTimeMap = new HashMap<>();
  private final Map<UUID, Icon> currentIconMap = new HashMap<>();
  private final Map<UUID, Integer> updateMap = new HashMap<>();
  private ViewRequirement viewRequirement;
  private String updatePattern = "0";
  private boolean checkOnlyOnCreation = false;

  public AnimatedIcon(String name, Menu<?> menu) {
    super(name, menu);
  }

  public AnimatedIcon(Icon original) {
    super(original);
    if (original instanceof AnimatedIcon) {
      this.icons.addAll(((AnimatedIcon) original).icons);
      this.updatePattern = ((AnimatedIcon) original).updatePattern;
      this.checkOnlyOnCreation = ((AnimatedIcon) original).checkOnlyOnCreation;
      this.viewRequirement = ((AnimatedIcon) original).viewRequirement;
    }
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    setChildFromSection(getMenu(), section);
    section.getKeys(false).forEach(key -> {
      if (key.equalsIgnoreCase("update")) {
        updatePattern = section.getString(key);
      } else if (key.equalsIgnoreCase("view-requirement")) {
        viewRequirement = new ViewRequirement(this);
        viewRequirement.setValue(section.get(key));
      } else if (key.equalsIgnoreCase("check-only-on-creation")) {
        checkOnlyOnCreation = section.getBoolean(key);
      }
    });
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
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
      failToCreate.remove(player.getUniqueId());
    }
    return createFirstItem(player);
  }

  private Optional<ClickableItem> createFirstItem(Player player) {
    Icon currentIcon = icons.get(0);
    int update = 0;

    String parsed =
        hasVariables(player, updatePattern) ? setVariables(updatePattern, player) : updatePattern;
    if (ExpressionUtils.isValidExpression(parsed)) {
      update = ExpressionUtils.getResult(parsed).intValue();
    }

    currentIndexMap.put(player.getUniqueId(), 0);
    currentIconMap.put(player.getUniqueId(), currentIcon);
    currentTimeMap.put(player.getUniqueId(), update);
    updateMap.put(player.getUniqueId(), update);
    return currentIcon.createClickableItem(player);
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    if (checkOnlyOnCreation) {
      if (failToCreate.contains(player.getUniqueId())) {
        return Optional.empty();
      }
    } else if (viewRequirement != null) {
      if (!viewRequirement.check(player)) {
        failToCreate.add(player.getUniqueId());
        return Optional.empty();
      } else if (failToCreate.contains(player.getUniqueId())) {
        viewRequirement.getCheckedRequirement(player).ifPresent(iconRequirementSet -> {
          iconRequirementSet.take(player);
          iconRequirementSet.sendSuccessCommands(player);
        });
        failToCreate.remove(player.getUniqueId());
        return createFirstItem(player);
      }
    }

    Icon currentIcon = currentIconMap.get(player.getUniqueId());

    if (currentTimeMap.get(player.getUniqueId()) > 0) {
      currentTimeMap.merge(player.getUniqueId(), -1, Integer::sum);
    } else {
      currentIcon = icons.get(getFrame(player));
      currentIconMap.put(player.getUniqueId(), currentIcon);
      currentTimeMap.put(player.getUniqueId(), updateMap.get(player.getUniqueId()));
    }

    return currentIcon.updateClickableItem(player);
  }

  private int getFrame(Player player) {
    currentIndexMap.computeIfPresent(player.getUniqueId(), (uuid, index) -> {
      if (index < icons.size() - 1) {
        ++index;
      } else {
        index = 0;
      }
      return index;
    });
    return currentIndexMap.get(player.getUniqueId());
  }

  @Override
  public void addChild(Icon icon) {
    icons.add(icon);
  }

  @Override
  public List<Icon> getChild() {
    return icons;
  }
}
