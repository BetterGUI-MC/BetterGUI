package me.hsgamer.bettergui.api.button;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.button.DisplayButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * The base class of wrapped button
 */
public abstract class BaseWrappedButton<B extends Button> implements WrappedButton {
  protected final Menu menu;
  protected final String name;
  protected final Map<String, Object> options;
  protected B button;

  /**
   * Create a new wrapped button
   *
   * @param input the input
   */
  protected BaseWrappedButton(ButtonBuilder.Input input) {
    this.menu = input.menu;
    this.name = input.name;
    this.options = input.options;
  }

  /**
   * Create the button from the section
   *
   * @param section the section
   *
   * @return the button
   */
  protected abstract B createButton(Map<String, Object> section);

  /**
   * Get the options of the button
   *
   * @return the options
   */
  public Map<String, Object> getOptions() {
    return options;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public @Nullable DisplayButton display(@NotNull UUID uuid) {
    if (button != null) {
      return button.display(uuid);
    }
    return null;
  }

  @Override
  public void init() {
    this.button = createButton(options);
    if (button != null) {
      button.init();
    }
  }

  @Override
  public void stop() {
    if (button != null) {
      button.stop();
    }
  }
}
