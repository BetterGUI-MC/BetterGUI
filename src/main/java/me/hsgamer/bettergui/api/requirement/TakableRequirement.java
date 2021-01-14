package me.hsgamer.bettergui.api.requirement;

import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The requirement that can take something
 *
 * @param <V> the type of the final value
 */
public abstract class TakableRequirement<V> extends BaseRequirement<V> {
  private boolean take = getDefaultTake();

  public TakableRequirement(String name) {
    super(name);
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
   * Take something if the "take" state is true
   *
   * @param uuid the unique id
   */
  protected abstract void takeChecked(UUID uuid);

  @Override
  public void setValue(Object value) {
    if (value instanceof ConfigurationSection) {
      Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(((ConfigurationSection) value).getValues(false));
      this.take = Optional.ofNullable(keys.get("take")).map(String::valueOf).map(Boolean::parseBoolean).orElse(this.take);
      super.setValue(Optional.ofNullable(keys.get("value")).orElse(getDefaultValue()));
    } else {
      super.setValue(value);
    }
  }

  @Override
  public void take(UUID uuid) {
    if (isTake()) {
      takeChecked(uuid);
    }
  }

  public boolean isTake() {
    return take;
  }
}
