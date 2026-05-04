package me.hsgamer.bettergui.action;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.element.MenuElement;
import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.task.element.TaskPool;
import me.hsgamer.hscore.task.element.TaskProcess;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The action applier
 */
public class ActionApplier implements ProcessApplier {
  /**
   * The empty action applier
   */
  public static final ActionApplier EMPTY = new ActionApplier(Collections.emptyList(), StringReplacer.DUMMY);
  private final List<Action> actions;
  private final StringReplacer replacer;

  /**
   * Create a new action applier
   *
   * @param actions  the actions
   * @param replacer the replacer
   */
  public ActionApplier(List<Action> actions, StringReplacer replacer) {
    this.actions = actions;
    this.replacer = replacer;
  }

  /**
   * Create a new action applier
   *
   * @param menuElement the menu element
   * @param value       the value
   */
  public ActionApplier(MenuElement menuElement, Object value) {
    this(BetterGUI.getInstance().get(ActionBuilder.class).build(menuElement, value), StringReplacer.of((original, uuid) -> StringReplacerApplier.replace(original, uuid, menuElement)));
  }

  /**
   * Check if the applier is empty
   *
   * @return true if it is empty
   */
  public boolean isEmpty() {
    return actions.isEmpty();
  }

  /**
   * Apply the action to the process without the next action
   *
   * @param uuid    the unique id
   * @param process the process
   */
  public void acceptWithoutNext(UUID uuid, TaskProcess process) {
    TaskPool currentPool = process.getCurrentTaskPool();
    for (Action action : actions) {
      currentPool.addLast(subProcess -> action.apply(uuid, subProcess, replacer));
    }
  }

  @Override
  public void accept(UUID uuid, TaskProcess process) {
    acceptWithoutNext(uuid, process);
    process.next();
  }
}
