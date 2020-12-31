package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.requirement.RequirementSetting;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.bukkit.gui.Button;
import me.hsgamer.hscore.bukkit.gui.button.PredicateButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WrappedPredicateButton extends BaseWrappedButton {
  private final List<UUID> checked = new CopyOnWriteArrayList<>();
  private boolean checkOnlyOnCreation = false;

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public WrappedPredicateButton(Menu menu) {
    super(menu);
  }

  private Map<AdvancedClickType, RequirementSetting> setClickRequirements(ConfigurationSection section) {
    Map<AdvancedClickType, RequirementSetting> clickRequirements = new ConcurrentHashMap<>();

    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));

    RequirementSetting defaultSetting = new RequirementSetting(getMenu(), getName() + "_click_default");
    Optional.ofNullable(keys.get("default"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
      .ifPresent(defaultSetting::loadFromSection);

    clickTypeMap.forEach((clickTypeName, clickType) ->
      clickRequirements.put(clickType, Optional.ofNullable(keys.get(clickTypeName))
        .filter(o -> o instanceof ConfigurationSection)
        .map(o -> (ConfigurationSection) o)
        .map(subsection -> {
          RequirementSetting setting = new RequirementSetting(getMenu(), getName() + "_click_" + clickTypeName.toLowerCase(Locale.ROOT));
          setting.loadFromSection(subsection);
          return setting;
        }).orElse(defaultSetting))
    );

    return clickRequirements;
  }

  @Override
  protected Button createButton(ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));

    PredicateButton predicateButton = new PredicateButton(
      Optional.ofNullable(keys.get("button"))
        .filter(o -> o instanceof ConfigurationSection)
        .map(o -> (ConfigurationSection) o)
        .map(subsection -> (Button) ButtonBuilder.INSTANCE.getButton(getMenu(), getName() + "_button", subsection))
        .orElse(Button.EMPTY)
    );
    Optional.ofNullable(keys.get("fallback"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
      .map(subsection -> ButtonBuilder.INSTANCE.getButton(getMenu(), getName() + "_fallback", subsection))
      .ifPresent(predicateButton::setFallbackButton);

    this.checkOnlyOnCreation = Optional.ofNullable(keys.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(this.checkOnlyOnCreation);

    Optional.ofNullable(keys.get("view-requirement"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
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
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
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
    tempButton = ((PredicateButton) this.button).getFallbackButton();
    if (tempButton instanceof WrappedButton) {
      ((WrappedButton) tempButton).refresh(uuid);
    }
  }
}
