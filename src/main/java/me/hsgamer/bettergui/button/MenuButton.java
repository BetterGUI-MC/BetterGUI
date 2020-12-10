package me.hsgamer.bettergui.button;

import co.aikar.taskchain.TaskChain;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.action.Action;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.ItemModifierBuilder;
import me.hsgamer.bettergui.config.MainConfig;
import me.hsgamer.bettergui.utils.CommonStringReplacers;
import me.hsgamer.hscore.bukkit.clicktype.AdvancedClickType;
import me.hsgamer.hscore.bukkit.clicktype.ClickTypeUtils;
import me.hsgamer.hscore.bukkit.item.ItemBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.*;

public class MenuButton implements WrappedButton {
  private final Menu menu;
  private final ItemBuilder itemBuilder = new ItemBuilder()
    .addStringReplacer("variable", CommonStringReplacers.VARIABLE)
    .addStringReplacer("colorize", CommonStringReplacers.COLORIZE)
    .addStringReplacer("expression", CommonStringReplacers.EXPRESSION);
  private final Map<AdvancedClickType, List<Action>> actionMap = new HashMap<>();
  private String name;
  private boolean closeOnClick = false;

  /**
   * Create a new button
   *
   * @param menu the menu
   */
  public MenuButton(Menu menu) {
    this.menu = menu;
  }

  private void setActions(Menu menu, Object o) {
    Map<String, AdvancedClickType> clickTypeMap = ClickTypeUtils.getClickTypeMap();
    if (o instanceof ConfigurationSection) {
      Map<String, Object> keys = new CaseInsensitiveStringMap<>(((ConfigurationSection) o).getValues(false));
      List<Action> defaultActions = Optional.ofNullable(keys.get("default")).map(value -> ActionBuilder.INSTANCE.getActions(menu, value)).orElse(Collections.emptyList());
      clickTypeMap.forEach((clickTypeName, clickType) -> {
        if (keys.containsKey(clickTypeName)) {
          actionMap.put(clickType, ActionBuilder.INSTANCE.getActions(menu, keys.get(clickTypeName)));
        } else {
          actionMap.put(clickType, defaultActions);
        }
      });
    } else {
      clickTypeMap.values().forEach(advancedClickType -> actionMap.put(advancedClickType, ActionBuilder.INSTANCE.getActions(menu, o)));
    }
  }

  @Override
  public void setFromSection(ConfigurationSection section) {
    ItemModifierBuilder.INSTANCE.getItemModifiers(section).forEach(itemBuilder::addItemModifier);
    Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(section.getValues(false));
    this.closeOnClick = Optional.ofNullable(keys.get("close-on-click")).map(String::valueOf).map(Boolean::parseBoolean).orElse(false);
    Optional.ofNullable(keys.get("command")).ifPresent(o -> setActions(menu, o));
    Optional.ofNullable(keys.get("action")).ifPresent(o -> setActions(menu, o));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Menu getMenu() {
    return menu;
  }

  @Override
  public ItemStack getItemStack(UUID uuid) {
    return itemBuilder.build(uuid);
  }

  @Override
  public void handleAction(UUID uuid, InventoryClickEvent event) {
    TaskChain<?> taskChain = BetterGUI.newChain();
    if (closeOnClick) {
      Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> taskChain.sync(() -> menu.closeInventory(player)));
    }
    Optional
      .ofNullable(actionMap.get(ClickTypeUtils.getClickTypeFromEvent(event, Boolean.TRUE.equals(MainConfig.MODERN_CLICK_TYPE.getValue()))))
      .ifPresent(actions -> actions.forEach(action -> action.addToTaskChain(uuid, taskChain)));
    taskChain.execute();
  }

  @Override
  public void refresh(UUID uuid) {
    // EMPTY
  }

  @Override
  public void init() {
    // EMPTY
  }

  @Override
  public void stop() {
    actionMap.values().forEach(List::clear);
    actionMap.clear();
  }
}
