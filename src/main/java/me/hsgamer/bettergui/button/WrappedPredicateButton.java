package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.button.PredicateButton;
import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.bukkit.clicktype.BukkitClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class WrappedPredicateButton extends BaseWrappedButton<PredicateButton> {
  private final Set<UUID> checked = new ConcurrentSkipListSet<>();

  public WrappedPredicateButton(ButtonBuilder.Input input) {
    super(input);
  }

  public static void applyRequirement(Map<String, Object> section, WrappedButton wrappedButton, Set<UUID> checked, PredicateButton predicateButton) {
    boolean checkOnlyOnCreation = Optional.ofNullable(section.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    // TODO: Regain support for prevent-spam-click for asynchronous click-requirement check
//    boolean preventSpamClick = Optional.ofNullable(section.get("prevent-spam-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(true);
//    predicateButton.setPreventSpamClick(preventSpamClick);
    Optional.ofNullable(section.get("view-requirement"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .ifPresent(subsection -> {
        RequirementApplier requirementApplier = new RequirementApplier(wrappedButton.getMenu(), wrappedButton.getName() + "_view", subsection);
        predicateButton.setViewPredicate(uuid -> {
          if (checkOnlyOnCreation && checked.contains(uuid)) {
            return true;
          }
          Requirement.Result result = requirementApplier.getResult(uuid);
          if (result.isSuccess) {
            checked.add(uuid);
          }
          BatchRunnable batchRunnable = new BatchRunnable();
          batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
          });
          SchedulerUtil.async().run(batchRunnable);
          return result.isSuccess;
        });
      });
    // TODO: Regain support for asynchronous click-requirement check
    Optional.ofNullable(section.get("click-requirement"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .ifPresent(subsection -> {
        Map<BukkitClickType, RequirementApplier> clickRequirements = RequirementApplier.convertClickRequirementAppliers(subsection, wrappedButton);
        predicateButton.setActionPredicate(InventoryClickEvent.class, clickEvent -> {
          UUID uuid = clickEvent.getWhoClicked().getUniqueId();
          RequirementApplier clickRequirement = clickRequirements.get(ClickTypeUtils.getClickTypeFromEvent(clickEvent, BetterGUI.getInstance().get(MainConfig.class).isModernClickType()));
          Requirement.Result result = clickRequirement.getResult(uuid);
          BatchRunnable batchRunnable = new BatchRunnable();
          batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
          });
          SchedulerUtil.async().run(batchRunnable);
          return result.isSuccess;
        });
      });
  }

  @Override
  protected PredicateButton createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);

    PredicateButton predicateButton = new PredicateButton();
    Optional.ofNullable(keys.get("button"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .flatMap(subsection -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_button", subsection)))
      .ifPresent(predicateButton::setButton);
    Optional.ofNullable(keys.get("fallback"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .flatMap(subsection -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_fallback", subsection)))
      .ifPresent(predicateButton::setFallbackButton);

    applyRequirement(keys, this, checked, predicateButton);
    return predicateButton;
  }

  @Override
  public void refresh(UUID uuid) {
    checked.remove(uuid);
    if (this.button == null) {
      return;
    }
    Button tempButton = this.button.getButton();
    if (tempButton instanceof WrappedButton) {
      ((WrappedButton) tempButton).refresh(uuid);
    }
    tempButton = this.button.getFallbackButton();
    if (tempButton instanceof WrappedButton) {
      ((WrappedButton) tempButton).refresh(uuid);
    }
  }
}
