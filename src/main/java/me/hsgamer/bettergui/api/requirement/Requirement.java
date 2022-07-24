package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.bettergui.api.RunnableApplier;
import me.hsgamer.bettergui.api.menu.MenuElement;

import java.util.UUID;

/**
 * The requirement
 */
public interface Requirement extends MenuElement {
  /**
   * Check the requirement for the unique id
   *
   * @param uuid the unique id
   *
   * @return the result
   */
  Result check(UUID uuid);

  /**
   * The result of the requirement
   */
  class Result {
    /**
     * Whether the requirement is met
     */
    public final boolean isSuccess;
    /**
     * The action if the requirement is met
     */
    public final RunnableApplier applier;

    /**
     * Create a new result
     *
     * @param success whether the requirement is met
     * @param applier the action if the requirement is met
     */
    public Result(boolean success, RunnableApplier applier) {
      this.isSuccess = success;
      this.applier = applier;
    }

    /**
     * Create a new result
     *
     * @param success whether the requirement is met
     */
    public Result(boolean success) {
      this(success, (uuid, batchRunnable) -> {
      });
    }

    /**
     * Create a success result
     *
     * @param applier the action if the requirement is met
     *
     * @return the result
     */
    public Result success(RunnableApplier applier) {
      return new Result(true, applier);
    }

    /**
     * Create a success result
     *
     * @return the result
     */
    public Result success() {
      return new Result(true);
    }

    /**
     * Create a failure result
     *
     * @return the result
     */
    public Result fail() {
      return new Result(false);
    }
  }
}
