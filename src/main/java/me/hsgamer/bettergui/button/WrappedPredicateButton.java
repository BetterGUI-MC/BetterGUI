package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.BaseWrappedButton;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.requirement.RequirementApplier;
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
import java.util.concurrent.ConcurrentSkipListSet;

public class WrappedPredicateButton extends BaseWrappedButton {
  private final Set<UUID> checked = new ConcurrentSkipListSet<>();
  private boolean checkOnlyOnCreation = false;

  public WrappedPredicateButton(ButtonBuilder.Input input) {
    super(input);
  }

  @Override
  protected Button createButton(Map<String, Object> section) {
    Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);

    PredicateButton predicateButton = new PredicateButton(
      Optional.ofNullable(keys.get("button"))
        .filter(Map.class::isInstance)
        .<Map<String, Object>>map(Map.class::cast)
        .map(subsection -> new ButtonBuilder.Input(getMenu(), getName() + "_button", subsection))
        .<Button>map(input -> ButtonBuilder.INSTANCE.build(input).orElseGet(() -> new EmptyButton(input)))
        .orElse(Button.EMPTY)
    );
    Optional.ofNullable(keys.get("fallback"))
      .filter(Map.class::isInstance)
      .<Map<String, Object>>map(Map.class::cast)
      .flatMap(subsection -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_fallback", subsection)))
      .ifPresent(predicateButton::setFallbackButton);
    this.checkOnlyOnCreation = Optional.ofNullable(keys.get("check-only-on-creation")).map(String::valueOf).map(Boolean::parseBoolean).orElse(this.checkOnlyOnCreation);

    Optional.ofNullable(keys.get("view-requirement"))
      .filter(Map.class::isInstance)
      .<Map<String, Object>>map(Map.class::cast)
      .ifPresent(subsection -> {
        RequirementApplier requirementApplier = new RequirementApplier(getMenu(), getName() + "_view", subsection);
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
    Optional.ofNullable(keys.get("click-requirement"))
      .filter(Map.class::isInstance)
      .<Map<String, Object>>map(Map.class::cast)
      .ifPresent(subsection -> {
        Map<AdvancedClickType, RequirementApplier> clickRequirements = RequirementApplier.convertClickRequirementAppliers(subsection, this);
        predicateButton.setClickPredicate((uuid, event) -> {
          RequirementApplier clickRequirement = clickRequirements.get(ClickTypeUtils.getClickTypeFromEvent(event, BetterGUI.getInstance().getMainConfig().modernClickType));
          Requirement.Result result = clickRequirement.getResult(uuid);
          BetterGUI.runBatchRunnable(batchRunnable -> batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
            result.applier.accept(uuid, process);
            process.next();
          }));
          return result.isSuccess;
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
