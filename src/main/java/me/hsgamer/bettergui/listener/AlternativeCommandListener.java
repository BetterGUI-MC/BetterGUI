package me.hsgamer.bettergui.listener;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.annotation.converter.Converter;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.*;
import java.util.regex.Pattern;

@Deprecated
public class AlternativeCommandListener implements Listener {
  private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");
  private final BetterGUI plugin;

  public AlternativeCommandListener(BetterGUI plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCommand(PlayerCommandPreprocessEvent event) {
    if (event.isCancelled()) {
      return;
    }

    AlternativeCommandListener.Setting setting = plugin.getMainConfig().alternativeCommandManager;

    String rawCommand = event.getMessage().substring(1);
    if (setting.ignoredCommands.stream().anyMatch(s -> setting.caseInsensitive ? s.equalsIgnoreCase(rawCommand) : s.equals(rawCommand)) == setting.shouldIgnore) {
      return;
    }

    String[] split = SPACE_PATTERN.split(rawCommand);
    String command = split[0];
    String[] args = new String[0];
    if (split.length > 1) {
      args = Arrays.copyOfRange(split, 1, split.length);
    }

    Map<String, Command> menuCommand = BetterGUI.getInstance().getMenuCommandManager().getRegisteredMenuCommand();
    if (setting.caseInsensitive) {
      menuCommand = new CaseInsensitiveStringMap<>(menuCommand);
    }

    if (menuCommand.containsKey(command)) {
      event.setCancelled(true);
      menuCommand.get(command).execute(event.getPlayer(), command, args);
    }
  }

  public static class Setting {
    private static final String ENABLE_SETTING = "enable";
    private static final String IGNORED_COMMANDS_SETTING = "ignored-commands";
    private static final String CASE_INSENSITIVE_SETTING = "case-insensitive";
    private static final String SHOULD_IGNORE_SETTING = "should-ignore";

    public final boolean enable;
    public final List<String> ignoredCommands;
    public final boolean caseInsensitive;
    public final boolean shouldIgnore;

    public Setting() {
      enable = false;
      ignoredCommands = Collections.singletonList("warp test");
      caseInsensitive = true;
      shouldIgnore = true;
    }

    public Setting(Map<String, Object> map) {
      Map<String, Object> caseInsensitiveMap = new CaseInsensitiveStringMap<>(map);
      enable = Boolean.parseBoolean(Objects.toString(caseInsensitiveMap.get(ENABLE_SETTING)));
      caseInsensitive = Boolean.parseBoolean(Objects.toString(caseInsensitiveMap.get(CASE_INSENSITIVE_SETTING)));
      shouldIgnore = Boolean.parseBoolean(Objects.toString(caseInsensitiveMap.get(SHOULD_IGNORE_SETTING)));
      ignoredCommands = CollectionUtils.createStringListFromObject(caseInsensitiveMap.get(IGNORED_COMMANDS_SETTING), true);
    }

    public Map<String, Object> toMap() {
      Map<String, Object> map = new HashMap<>();
      map.put(ENABLE_SETTING, enable);
      map.put(CASE_INSENSITIVE_SETTING, caseInsensitive);
      map.put(SHOULD_IGNORE_SETTING, shouldIgnore);
      map.put(IGNORED_COMMANDS_SETTING, ignoredCommands);
      return map;
    }
  }

  public static class SettingConverter implements Converter {

    @Override
    public Object convert(Object raw) {
      if (raw instanceof Map) {
        //noinspection unchecked
        return new Setting((Map<String, Object>) raw);
      }
      return null;
    }

    @Override
    public Object convertToRaw(Object value) {
      if (value instanceof Setting) {
        return ((Setting) value).toMap();
      }
      return null;
    }
  }
}
