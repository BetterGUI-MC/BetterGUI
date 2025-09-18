package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.button.PredicateButton;
import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

public class WrappedPredicateButton extends BaseWrappedButton<WrappedPredicateButton.PredicateClickButton> {
  private final Set<UUID> checked = new ConcurrentSkipListSet<>();

  public WrappedPredicateButton(ButtonBuilder.Input input) {
    super(input);
  }

  public static PredicateClickButton getPredicateButton(Map<String, Object> section, WrappedButton wrappedButton, Set<UUID> checked, PredicateButton predicateButton) {
    boolean checkOnlyOnCreation = Optional.ofNullable(section.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    boolean preventSpamClick = Optional.ofNullable(section.get("prevent-spam-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(true);
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
    Map<BukkitClickType, RequirementApplier> clickRequirements = Optional.ofNullable(section.get("click-requirement"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .map(map -> RequirementApplier.convertClickRequirementAppliers(map, wrappedButton))
      .orElse(null);

    return new PredicateClickButton(predicateButton, clickRequirements, preventSpamClick);
  }

  @Override
  protected PredicateClickButton createButton(Map<String, Object> section) {
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

    return getPredicateButton(keys, this, checked, predicateButton);
  }

  @Override
  public void refresh(UUID uuid) {
    checked.remove(uuid);
    if (this.button == null) {
      return;
    }
    PredicateButton predicateButton = this.button.getPredicateButton();
    Button tempButton = predicateButton.getButton();
    if (tempButton instanceof WrappedButton) {
      ((WrappedButton) tempButton).refresh(uuid);
    }
    tempButton = predicateButton.getFallbackButton();
    if (tempButton instanceof WrappedButton) {
      ((WrappedButton) tempButton).refresh(uuid);
    }
  }

  public static class PredicateClickButton implements Button, Element {
    private final PredicateButton predicateButton;
    private final Map<BukkitClickType, RequirementApplier> clickRequirements;
    private final boolean preventSpamClick;
    private final Set<UUID> clickCheckList = new ConcurrentSkipListSet<>();

    public PredicateClickButton(PredicateButton predicateButton, Map<BukkitClickType, RequirementApplier> clickRequirements, boolean preventSpamClick) {
      this.predicateButton = predicateButton;
      this.clickRequirements = clickRequirements;
      this.preventSpamClick = preventSpamClick;
    }

    @Override
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
      boolean isApplied = predicateButton.apply(uuid, actionItem);
      if (!isApplied) return false;

      if (clickRequirements != null && !clickRequirements.isEmpty()) {
        actionItem.extendAction(InventoryClickEvent.class, (event, objectConsumer) -> {
          UUID clickUUID = event.getWhoClicked().getUniqueId();
          if (preventSpamClick && clickCheckList.contains(clickUUID)) {
            return;
          }
          RequirementApplier clickRequirement = clickRequirements.get(ClickTypeUtils.getClickTypeFromEvent(event, BetterGUI.getInstance().get(MainConfig.class).isModernClickType()));
          if (clickRequirement == null) {
            objectConsumer.accept(event);
            return;
          }

          clickCheckList.add(clickUUID);
          CompletableFuture.supplyAsync(() -> clickRequirement.getResult(clickUUID)).thenAccept((result) -> {
            clickCheckList.remove(clickUUID);
            BatchRunnable batchRunnable = new BatchRunnable();
            batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
              result.applier.accept(clickUUID, process);
              process.next();
            });
            if (result.isSuccess) {
              batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(() -> objectConsumer.accept(event));
            }
            SchedulerUtil.async().run(batchRunnable);
          });
        });
      }
      return true;
    }

    public PredicateButton getPredicateButton() {
      return predicateButton;
    }

    @Override
    public void init() {
      predicateButton.init();
    }

    @Override
    public void stop() {
      predicateButton.stop();
      clickCheckList.clear();
    }
  }
}
