package me.hsgamer.bettergui.builder;

import me.hsgamer.bettergui.action.type.*;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.common.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The action builder
 */
public final class ActionBuilder extends MassBuilder<ActionBuilder.Input, Action> {
  /**
   * The instance of the action builder
   */
  public static final ActionBuilder INSTANCE = new ActionBuilder();
  /**
   * The pattern for the action.
   * The format is: {@code <type>(<option>): <value>}. Note that the {@code <option>} and {@code <value>} are optional.
   * Also, the allowed characters of the {@code <type>} are alphanumeric, {@code _}, {@code -} and {@code $}.
   * To get the {@code <type>}, {@code <option>} and {@code <value>}, use {@link Matcher#group(int)} with the index 1, 3 and 5 respectively.
   */
  public static final Pattern ACTION_PATTERN = Pattern.compile("\\s*([\\w\\-$]+)\\s*(\\((.*)\\))?\\s*(:\\s*(.*)\\s*)?");

  private ActionBuilder() {
    register(ConsoleAction::new, "console");
    register(OpAction::new, "op");
    register(PlayerAction::new, "player");
    register(DelayAction::new, "delay");
    register(OpenMenuAction::new, "open-menu", "open", "menu", "open-menu");
    register(input -> new BackAction(input.menu), "back-menu", "backmenu");
    register(TellAction::new, "tell");
    register(BroadcastAction::new, "broadcast");
    register(input -> new CloseMenuAction(input.menu), "close-menu", "closemenu");
    register(input -> new UpdateMenuAction(input.menu), "update-menu", "updatemenu");
    register(PermissionAction::new, "permission");
    register(SoundAction::new, "sound", "raw-sound");
  }

  /**
   * Register a new action creator
   *
   * @param creator the creator
   * @param type    the type
   */
  public void register(Function<Input, Action> creator, String... type) {
    register(new Element<Input, Action>() {
      @Override
      public boolean canBuild(Input input) {
        String action = input.type;
        for (String s : type) {
          if (action.equalsIgnoreCase(s)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public Action build(Input input) {
        return creator.apply(input);
      }
    });
  }

  /**
   * Build a list of actions
   *
   * @param menu   the menu involved in
   * @param object the object
   *
   * @return the list of actions
   */
  public List<Action> build(Menu menu, Object object) {
    return CollectionUtils.createStringListFromObject(object, true)
      .stream()
      .flatMap(string -> {
        Matcher matcher = ACTION_PATTERN.matcher(string);
        if (matcher.matches()) {
          String type = matcher.group(1);
          String option = Optional.ofNullable(matcher.group(3)).orElse("");
          String value = Optional.ofNullable(matcher.group(5)).orElse("");
          return Stream.of(build(new Input(menu, type, value, option)).orElseGet(() -> new PlayerAction(new Input(menu, "player", string.trim(), ""))));
        } else {
          return Stream.of(new PlayerAction(new Input(menu, "player", string, "")));
        }
      })
      .collect(Collectors.toList());
  }

  /**
   * The input for the action builder
   */
  public static class Input {
    public final Menu menu;
    public final String type;
    public final String value;
    public final String option;

    /**
     * Create a new input
     *
     * @param menu   the menu
     * @param type   the type of the action
     * @param value  the value of the action
     * @param option the option of the action
     */
    public Input(Menu menu, String type, String value, String option) {
      this.menu = menu;
      this.type = type;
      this.value = value;
      this.option = option;
    }

    /**
     * Get the option as a list
     *
     * @param separator the separator
     *
     * @return the list
     */
    public List<String> getOptionAsList(String separator) {
      if (option.isEmpty()) {
        return Collections.emptyList();
      }
      return Stream.of(option.split(separator))
        .map(String::trim)
        .collect(Collectors.toList());
    }

    /**
     * Get the option as a list.
     * The format is {@code value,value}
     *
     * @return the list
     */
    public List<String> getOptionAsList() {
      return getOptionAsList(",");
    }

    /**
     * Get the option as a map.
     * The format is {@code key=value,key=value}
     *
     * @return the map
     */
    public Map<String, String> getOptionAsMap() {
      return getOptionAsList().stream()
        .map(s -> s.split("="))
        .collect(Collectors.toMap(strings -> strings[0].trim(), strings -> strings.length > 1 ? strings[1].trim() : ""));
    }
  }
}
