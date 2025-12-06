package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.bettergui.api.process.ProcessApplier;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.hscore.common.MapUtils;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The requirement that can take something
 *
 * @param <V> the type of the final value
 */
public abstract class TakableRequirement<V> extends BaseRequirement<V> {
  private boolean take;

  /**
   * Create a new requirement
   *
   * @param input the input
   */
  protected TakableRequirement(RequirementBuilder.Input input) {
    super(input);
  }

  @Override
  protected Object handleValue(Object inputValue) {
    if (inputValue instanceof Map) {
      Map<String, Object> keys = MapUtils.createLowercaseStringObjectMap((Map<?, ?>) inputValue);
      this.take = Optional.ofNullable(keys.get("take")).map(String::valueOf).map(Boolean::parseBoolean).orElse(getDefaultTake());
      return super.handleValue(Optional.ofNullable(keys.get("value")).orElse(getDefaultValue()));
    } else {
      this.take = getDefaultTake();
      return super.handleValue(inputValue);
    }
  }

  /**
   * Get the default "take" state
   *
   * @return the default "take" state
   */
  protected abstract boolean getDefaultTake();

  /**
   * Get the default value
   *
   * @return the default value
   */
  protected abstract Object getDefaultValue();

  /**
   * Get the success result with the conditional "take" state
   *
   * @param applier the applier
   *
   * @return the success result
   */
  public Result successConditional(ProcessApplier applier) {
    return new Result(true, (uuid, process) -> {
      if (take) {
        applier.accept(uuid, process);
      } else {
        process.next();
      }
    });
  }

  /**
   * Get the success result with the conditional "take" state
   *
   * @param applier the applier
   *
   * @return the success result
   */
  public Result successConditional(Consumer<UUID> applier) {
    return successConditional((uuid, process) -> {
      applier.accept(uuid);
      process.next();
    });
  }
}
