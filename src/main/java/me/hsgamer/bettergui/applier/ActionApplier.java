package me.hsgamer.bettergui.applier;

import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.task.BatchRunnable;

import java.util.List;
import java.util.UUID;

/**
 * The action applier
 */
public class ActionApplier implements ProcessApplier {
  private final List<Action> actions;

  /**
   * Create a new action applier
   *
   * @param actions the actions
   */
  public ActionApplier(List<Action> actions) {
    this.actions = actions;
  }

  /**
   * Create a new action applier
   *
   * @param menu  the menu
   * @param value the value
   */
  public ActionApplier(Menu menu, Object value) {
    this(ActionBuilder.INSTANCE.build(menu, value));
  }

  @Override
  public void accept(UUID uuid, BatchRunnable.Process process) {
    BatchRunnable.TaskPool currentPool = process.getCurrentTaskPool();
    for (Action action : actions) {
      currentPool.addLast(subProcess -> action.accept(uuid, subProcess));
    }
    process.next();
  }
}
