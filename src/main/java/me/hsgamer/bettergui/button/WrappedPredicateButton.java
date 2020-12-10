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
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WrappedPredicateButton extends BaseWrappedButton {
  private RequirementSetting viewRequirement;
  private final Map<AdvancedClickType, RequirementSetting> clickRequirements = new ConcurrentHashMap<>();
  private final List<UUID> checked = Collections.synchronizedList(new ArrayList<>());
  private WrappedButton wrappedButton = new EmptyButton(getMenu());
  private WrappedButton fallbackWrappedButton = new EmptyButton(getMenu());
  private boolean checkOnlyOnCreation = false;

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public WrappedPredicateButton(Menu menu) {
    super(menu);
  }

  private void setClickRequirements(ConfigurationSection section) {
    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section.getValues(false));

    RequirementSetting defaultSetting = new RequirementSetting(getMenu(), getName() + "_click_default");
    Optional.ofNullable(keys.get("default"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
      .ifPresent(defaultSetting::loadFromSection);

    this.checkOnlyOnCreation = Optional.ofNullable(keys.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(this.checkOnlyOnCreation);
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
  }

  @Override
  protected Button createButton(ConfigurationSection section) {
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));

    this.wrappedButton = Optional.ofNullable(keys.get("button"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
      .map(subsection -> ButtonBuilder.INSTANCE.getButton(getMenu(), getName() + "_button", subsection))
      .orElse(this.wrappedButton);
    this.fallbackWrappedButton = Optional.ofNullable(keys.get("fallback"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
      .map(subsection -> ButtonBuilder.INSTANCE.getButton(getMenu(), getName() + "_fallback", subsection))
      .orElse(this.fallbackWrappedButton);

    this.viewRequirement = new RequirementSetting(getMenu(), getName() + "_view");
    Optional.ofNullable(keys.get("view-requirement"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
      .ifPresent(this.viewRequirement::loadFromSection);
    Optional.ofNullable(keys.get("click-requirement"))
      .filter(o -> o instanceof ConfigurationSection)
      .map(o -> (ConfigurationSection) o)
      .ifPresent(this::setClickRequirements);

    PredicateButton predicateButton = new PredicateButton(this.wrappedButton);
    predicateButton.setFallbackButton(this.fallbackWrappedButton);
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

    return predicateButton;
  }

  @Override
  public void refresh(UUID uuid) {
    checked.remove(uuid);
    wrappedButton.refresh(uuid);
    fallbackWrappedButton.refresh(uuid);
  }
}
