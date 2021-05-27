package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.requirement.RequirementSetting;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.PredicateButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class LegacyMenuButton extends BaseWrappedButton {
  private final Set<UUID> checked = new ConcurrentSkipListSet<>();
  private boolean checkOnlyOnCreation = false;

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public LegacyMenuButton(Menu menu) {
    super(menu);
  }

  private Map<AdvancedClickType, RequirementSetting> setClickRequirements(Map<String, Object> section) {
    Map<AdvancedClickType, RequirementSetting> clickRequirements = new ConcurrentHashMap<>();

    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section);

    RequirementSetting defaultSetting = new RequirementSetting(getMenu(), getName() + "_click_default");
    Optional.ofNullable(keys.get("default"))
      .filter(Map.class::isInstance)
      .map(o -> (Map<String, Object>) o)
      .ifPresent(defaultSetting::loadFromSection);

    clickTypeMap.forEach((clickTypeName, clickType) ->
      clickRequirements.put(clickType, Optional.ofNullable(keys.get(clickTypeName))
        .filter(Map.class::isInstance)
        .map(o -> (Map<String, Object>) o)
        .map(subsection -> {
          RequirementSetting setting = new RequirementSetting(getMenu(), getName() + "_click_" + clickTypeName.toLowerCase(Locale.ROOT));
          setting.loadFromSection(subsection);
          return setting;
        }).orElse(defaultSetting))
    );

    return clickRequirements;
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section);

    MenuButton menuButton = new MenuButton(getMenu());
    menuButton.setName(getName());
    menuButton.setFromSection(section);

    PredicateButton predicateButton = new PredicateButton(menuButton);

    this.checkOnlyOnCreation = Optional.ofNullable(keys.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(this.checkOnlyOnCreation);

    Optional.ofNullable(keys.get("view-requirement"))
      .filter(Map.class::isInstance)
      .map(o -> (Map<String, Object>) o)
      .ifPresent(subsection -> {
        RequirementSetting viewRequirement = new RequirementSetting(getMenu(), getName() + "_view");
        viewRequirement.loadFromSection(subsection);
        predicateButton.setViewPredicate(uuid -> {
          if (checkOnlyOnCreation && checked.contains(uuid)) {
            return true;
          }
          if (!viewRequirement.check(uuid)) {
            viewRequirement.sendFailActions(uuid);
            return false;
          }
          viewRequirement.getCheckedRequirement(uuid).ifPresent(requirementSet -> {
            requirementSet.take(uuid);
            requirementSet.sendSuccessActions(uuid);
          });
          checked.add(uuid);
          return true;
        });
      });
    Optional.ofNullable(keys.get("click-requirement"))
      .filter(Map.class::isInstance)
      .map(o -> (Map<String, Object>) o)
      .ifPresent(subsection -> {
        Map<AdvancedClickType, RequirementSetting> clickRequirements = setClickRequirements(subsection);
        predicateButton.setClickPredicate((uuid, event) -> {
          RequirementSetting clickRequirement = clickRequirements.get(ClickTypeUtils.getClickTypeFromEvent(event, Boolean.TRUE.equals(MainConfig.MODERN_CLICK_TYPE.getValue())));
          if (!clickRequirement.check(uuid)) {
            clickRequirement.sendFailActions(uuid);
            return false;
          }
          clickRequirement.getCheckedRequirement(uuid).ifPresent(requirementSet -> {
            requirementSet.take(uuid);
            requirementSet.sendSuccessActions(uuid);
          });
          return true;
        });
      });

    return predicateButton;
  }

  @Override
  public void refresh(UUID uuid) {
    checked.remove(uuid);
    if (!(this.button instanceof PredicateButton)) {
      return;
    }
    Button tempButton = ((PredicateButton) this.button).getButton();
    if (tempButton instanceof WrappedButton) {
      ((WrappedButton) tempButton).refresh(uuid);
    }
  }
}
