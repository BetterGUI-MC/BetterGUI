package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.bettergui.api.RunnableApplier;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.Map;
import java.util.Optional;

/**
 * The requirement that can take something
 *
 * @param <V> the type of the final value
 */
public abstract class TakableRequirement<V> extends BaseRequirement<V> {
  private boolean take = getDefaultTake();

  /**
   * Create a new requirement
   *
   * @param input the input
   */
  protected TakableRequirement(RequirementBuilder.Input input) {
    super(input);
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
  public Result successConditional(RunnableApplier applier) {
    return new Result(true, (uuid, batchRunnable) -> {
      if (take) {
        applier.accept(uuid, batchRunnable);
      }
    });
  }

  @Override
  protected Object handleValue(Object inputValue) {
    if (inputValue instanceof Map) {
      //noinspection unchecked
      Map<String, Object> keys = new CaseInsensitiveStringMap<>((Map<String, Object>) inputValue);
      this.take = Optional.ofNullable(keys.get("take")).map(String::valueOf).map(Boolean::parseBoolean).orElse(this.take);
      return Optional.ofNullable(keys.get("value")).orElse(getDefaultValue());
    } else {
      return super.handleValue(inputValue);
    }
  }
}
