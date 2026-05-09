package me.hsgamer.bettergui.button;

import io.github.projectunified.craftux.button.PredicateButton;
import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.MenuButton;
import me.hsgamer.bettergui.api.replacer.LookupStringReplacer;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.requirement.ClickRequirementApplier;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class WrappedPredicateButton extends MenuButton {
  public WrappedPredicateButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected PredicateClickButton createButton(Map<String, Object> section) {
    Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap(section);

    PredicateClickButtonContext context = new PredicateClickButtonContext(keys, this);
    PredicateClickButton predicateButton = new PredicateClickButton(context);
    Optional.ofNullable(keys.get("button"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .flatMap(subsection -> BetterGUI.getInstance().get(ButtonBuilder.class).build(new ButtonBuilder.Input(this, "button", subsection)))
      .ifPresent(predicateButton::setButton);
    Optional.ofNullable(keys.get("fallback"))
      .flatMap(MapUtils::castOptionalStringObjectMap)
      .flatMap(subsection -> BetterGUI.getInstance().get(ButtonBuilder.class).build(new ButtonBuilder.Input(this, "fallback", subsection)))
      .ifPresent(predicateButton::setFallbackButton);

    return predicateButton;
  }

  @Override
  public void refresh(UUID uuid) {
    if (this.button == null) {
      return;
    }
    PredicateClickButton predicateButton = ((PredicateClickButton) this.button);
    predicateButton.refresh(uuid);
  }

  @Override
  public StringReplacer getStringReplacer() {
    if (this.button == null) return StringReplacer.DUMMY;
    return ((PredicateClickButton) this.button).getStringReplacer();
  }

  public static class PredicateClickButtonContext {
    private final boolean checkOnlyOnCreation;
    private final boolean preventSpamClick;
    private final RequirementApplier viewRequirement;
    private final ClickRequirementApplier clickRequirements;

    public PredicateClickButtonContext(Map<String, Object> section, MenuButton menuButton) {
      checkOnlyOnCreation = Optional.ofNullable(section.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
      preventSpamClick = Optional.ofNullable(section.get("prevent-spam-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(true);
      viewRequirement = Optional.ofNullable(section.get("view-requirement"))
        .flatMap(MapUtils::castOptionalStringObjectMap)
        .map(subSection -> new RequirementApplier(menuButton, subSection))
        .orElse(null);
      clickRequirements = Optional.ofNullable(section.get("click-requirement"))
        .flatMap(MapUtils::castOptionalStringObjectMap)
        .map(map -> new ClickRequirementApplier(menuButton, map))
        .orElse(null);
    }

    public boolean exists() {
      return viewRequirement != null || clickRequirements != null;
    }
  }

  public static class PredicateClickButton extends PredicateButton {
    private final PredicateClickButtonContext context;
    private final Set<UUID> checked = new ConcurrentSkipListSet<>();
    private final Set<UUID> clickCheckList = new ConcurrentSkipListSet<>();

    public PredicateClickButton(PredicateClickButtonContext context) {
      this.context = context;
      if (context.viewRequirement != null) {
        setViewPredicate(uuid -> {
          if (context.checkOnlyOnCreation && checked.contains(uuid)) {
            return true;
          }
          Requirement.Result result = context.viewRequirement.getResult(uuid);
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
      }
    }

    @Override
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
      boolean isApplied = super.apply(uuid, actionItem);
      if (!isApplied) return false;

      if (context.clickRequirements != null && context.clickRequirements.exists()) {
        actionItem.extendAction(InventoryClickEvent.class, (event, objectConsumer) -> {
          UUID clickUUID = event.getWhoClicked().getUniqueId();
          if (context.preventSpamClick && clickCheckList.contains(clickUUID)) {
            return;
          }
          RequirementApplier clickRequirement = context.clickRequirements.getRequirementApplier(ClickTypeUtils.getClickTypeFromEvent(event, BetterGUI.getInstance().get(MainConfig.class).isModernClickType()));
          if (clickRequirement == null) {
            objectConsumer.accept(event);
            return;
          }

          BatchRunnable batchRunnable = new BatchRunnable();
          batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            Requirement.Result result = clickRequirement.getResult(clickUUID);
            result.applier.accept(uuid, process);
            if (result.isSuccess) {
              process.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(() -> objectConsumer.accept(event));
            }
            process.next();
          });

          SchedulerUtil.async().run(() -> {
            clickCheckList.add(clickUUID);
            batchRunnable.run();
            clickCheckList.remove(clickUUID);
          });
        });
      }
      return true;
    }

    public ClickRequirementApplier getClickRequirements() {
      return context.clickRequirements;
    }

    public RequirementApplier getViewRequirement() {
      return context.viewRequirement;
    }

    public void refresh(UUID uuid) {
      checked.remove(uuid);
      Button tempButton = getButton();
      if (tempButton instanceof MenuButton) {
        ((MenuButton) tempButton).refresh(uuid);
      }
      tempButton = getFallbackButton();
      if (tempButton instanceof MenuButton) {
        ((MenuButton) tempButton).refresh(uuid);
      }
    }

    public StringReplacer getStringReplacer() {
      return (LookupStringReplacer) original -> {
        if (original.startsWith("button")) {
          Button button = getButton();
          if (button instanceof MenuButton) {
            return Pair.of(((MenuButton) button).getStringReplacer(), original.substring("button".length()));
          }
        }
        if (original.startsWith("fallback")) {
          Button button = getFallbackButton();
          if (button instanceof MenuButton) {
            return Pair.of(((MenuButton) button).getStringReplacer(), original.substring("fallback".length()));
          }
        }
        if (getClickRequirements() != null && original.startsWith("click")) {
          return Pair.of(getClickRequirements().getStringReplacer(), original.substring("click".length()));
        }
        if (getViewRequirement() != null && original.startsWith("view")) {
          return Pair.of(getViewRequirement().getStringReplacer(), original.substring("view".length()));
        }
        return null;
      };
    }

    @Override
    public void stop() {
      super.stop();
      clickCheckList.clear();
    }
  }
}
