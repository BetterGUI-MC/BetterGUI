package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.api.process.ProcessApplier;

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
   * Get the name of the requirement
   *
   * @return the name
   */
  String getName();

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
    public final ProcessApplier applier;

    /**
     * Create a new result
     *
     * @param success whether the requirement is met
     * @param applier the action if the requirement is met
     */
    public Result(boolean success, ProcessApplier applier) {
      this.isSuccess = success;
      this.applier = applier;
    }

    /**
     * Create a success result
     *
     * @param applier the action if the requirement is met
     *
     * @return the result
     */
    public static Result success(ProcessApplier applier) {
      return new Result(true, applier);
    }

    /**
     * Create a failure result
     *
     * @param applier the action if the requirement is not met
     *
     * @return the result
     */
    public static Result fail(ProcessApplier applier) {
      return new Result(false, applier);
    }

    /**
     * Create a success result
     *
     * @return the result
     */
    public static Result success() {
      return new Result(true, (uuid, process) -> process.next());
    }

    /**
     * Create a failure result
     *
     * @return the result
     */
    public static Result fail() {
      return new Result(false, (uuid, process) -> process.next());
    }
  }
}
