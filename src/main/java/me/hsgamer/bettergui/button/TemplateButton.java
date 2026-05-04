package me.hsgamer.bettergui.button;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.button.MenuButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.config.TemplateConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class TemplateButton extends MenuButton {
  private Map<String, Object> finalOptions;

  /**
   * Create a new button
   *
   * @param input the input
   */
  public TemplateButton(ButtonBuilder.Input input) {
    super(input);
    finalOptions = input.options;
  }

  @Override
  protected MenuButton createButton(Map<String, Object> section) {
    finalOptions = BetterGUI.getInstance().get(TemplateConfig.class).getValues(section, "type");
    return BetterGUI.getInstance().get(ButtonBuilder.class).build(new ButtonBuilder.Input(getMenu(), getName(), finalOptions)).orElse(null);
  }

  @Override
  public Map<String, Object> getOptions() {
    return finalOptions;
  }

  @Override
  public @Nullable String replace(@NotNull String arguments) {
    if (this.button == null) return null;
    return ((MenuButton) this.button).replace(arguments);
  }

  @Override
  public @Nullable String replace(@NotNull String arguments, @NotNull UUID uuid) {
    if (this.button == null) return null;
    return ((MenuButton) this.button).replace(arguments, uuid);
  }
}
