package me.hsgamer.bettergui.api.action;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.util.CommandUtil;

import java.util.UUID;

/**
 * The command action
 */
public abstract class CommandAction extends BaseAction {
  /**
   * Create a new action
   *
   * @param input the input
   */
  protected CommandAction(ActionBuilder.Input input) {
    super(input);
  }

  /**
   * Get the final command to use in the dispatch method
   *
   * @param uuid the unique id
   *
   * @return the final command
   */
  protected String getFinalCommand(UUID uuid) {
    return CommandUtil.normalizeCommand(getReplacedString(uuid));
  }

  @Override
  protected boolean shouldBeTrimmed() {
    return true;
  }
}