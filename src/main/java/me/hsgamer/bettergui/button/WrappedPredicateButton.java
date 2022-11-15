package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.button.impl.PredicateButton;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

public class WrappedPredicateButton extends BaseWrappedButton {
  private Set<UUID> checked;

  public WrappedPredicateButton(ButtonBuilder.Input input) {
    super(input);
  }

  public static void applyRequirement(Map<String, Object> section, WrappedButton wrappedButton, Set<UUID> checked, PredicateButton predicateButton) {
    boolean checkOnlyOnCreation = Optional.ofNullable(section.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    boolean preventSpamClick = Optional.ofNullable(section.get("prevent-spam-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(true);

    predicateButton.setPreventSpamClick(preventSpamClick);
    Optional.ofNullable(section.get("view-requirement"))
      .flatMap(MapUtil::castOptionalStringObjectMap)
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
          BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
          }));
          return result.isSuccess;
        });
      });
    Optional.ofNullable(section.get("click-requirement"))
      .flatMap(MapUtil::castOptionalStringObjectMap)
      .ifPresent(subsection -> {
        Map<AdvancedClickType, RequirementApplier> clickRequirements = RequirementApplier.convertClickRequirementAppliers(subsection, wrappedButton);
        predicateButton.setClickFuturePredicate((uuid, event) -> {
          RequirementApplier clickRequirement = clickRequirements.get(ClickTypeUtils.getClickTypeFromEvent(event, BetterGUI.getInstance().getMainConfig().modernClickType));
          return CompletableFuture.supplyAsync(() -> clickRequirement.getResult(uuid))
            .thenApply(result -> {
              BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
                result.applier.accept(uuid, process);
                process.next();
              }));
              return result.isSuccess;
            });
        });
      });
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);

    PredicateButton predicateButton = new PredicateButton(
      Optional.ofNullable(keys.get("button"))
        .flatMap(MapUtil::castOptionalStringObjectMap)
        .map(subsection -> new ButtonBuilder.Input(getMenu(), getName() + "_button", subsection))
        .<Button>map(input -> ButtonBuilder.INSTANCE.build(input).orElseGet(() -> new EmptyButton(input)))
        .orElse(Button.EMPTY)
    );
    Optional.ofNullable(keys.get("fallback"))
      .flatMap(MapUtil::castOptionalStringObjectMap)
      .flatMap(subsection -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_fallback", subsection)))
      .ifPresent(predicateButton::setFallbackButton);

    checked = new ConcurrentSkipListSet<>();
    applyRequirement(keys, this, checked, predicateButton);
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
