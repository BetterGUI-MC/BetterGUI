package me.hsgamer.bettergui.builder;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.Property;
import me.hsgamer.bettergui.object.icon.RawIcon;
import me.hsgamer.bettergui.object.menu.DummyMenu;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.object.property.MenuProperty;
import me.hsgamer.bettergui.object.property.icon.impl.ClickCommand;
import me.hsgamer.bettergui.object.property.icon.impl.ClickRequirement;
import me.hsgamer.bettergui.object.property.icon.impl.CloseOnClick;
import me.hsgamer.bettergui.object.property.icon.impl.Variable;
import me.hsgamer.bettergui.object.property.icon.impl.ViewRequirement;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.object.property.item.impl.Amount;
import me.hsgamer.bettergui.object.property.item.impl.Durability;
import me.hsgamer.bettergui.object.property.item.impl.Enchantment;
import me.hsgamer.bettergui.object.property.item.impl.Flag;
import me.hsgamer.bettergui.object.property.item.impl.Lore;
import me.hsgamer.bettergui.object.property.item.impl.Name;
import me.hsgamer.bettergui.object.property.item.impl.Type;
import me.hsgamer.bettergui.object.property.menu.MenuAction;
import me.hsgamer.bettergui.object.property.menu.MenuInventoryType;
import me.hsgamer.bettergui.object.property.menu.MenuRows;
import me.hsgamer.bettergui.object.property.menu.MenuTicks;
import me.hsgamer.bettergui.object.property.menu.MenuTitle;
import me.hsgamer.bettergui.util.CaseInsensitiveStringLinkedMap;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;

public final class PropertyBuilder {

  private static final Map<String, Class<? extends ItemProperty<?, ?>>> itemProperties = new CaseInsensitiveStringMap<>();
  private static final Map<String, Class<? extends IconProperty<?>>> iconProperties = new CaseInsensitiveStringMap<>();
  private static final Map<String, Class<? extends MenuProperty<?, ?>>> menuProperties = new CaseInsensitiveStringMap<>();
  private static final Map<String, Class<? extends Property<?>>> otherProperties = new CaseInsensitiveStringMap<>();

  static {
    registerItemProperty("name", Name.class);
    registerItemProperty("lore", Lore.class);
    registerItemProperty("amount", Amount.class);
    registerItemProperty("id", Type.class);
    registerItemProperty("material", Type.class);
    registerItemProperty("enchantment", Enchantment.class);
    registerItemProperty("enchant", Enchantment.class);
    registerItemProperty("flag", Flag.class);
    registerItemProperty("item-flags", Flag.class);
    registerItemProperty("itemflag", Flag.class);
    registerItemProperty("durability", Durability.class);
    registerItemProperty("damage", Durability.class);

    registerIconProperty("variable", Variable.class);
    registerIconProperty("placeholder", Variable.class);
    registerIconProperty("view-requirement", ViewRequirement.class);
    registerIconProperty("click-requirement", ClickRequirement.class);
    registerIconProperty("close-on-click", CloseOnClick.class);
    registerIconProperty("command", ClickCommand.class);

    registerMenuProperty("open-action", MenuAction.class);
    registerMenuProperty("close-action", MenuAction.class);
    registerMenuProperty("inventory-type", MenuInventoryType.class);
    registerMenuProperty("inventory", MenuInventoryType.class);
    registerMenuProperty("rows", MenuRows.class);
    registerMenuProperty("auto-refresh", MenuTicks.class);
    registerMenuProperty("ticks", MenuTicks.class);
    registerMenuProperty("name", MenuTitle.class);
    registerMenuProperty("title", MenuTitle.class);
  }

  private PropertyBuilder() {

  }

  /**
   * Register new item property
   *
   * @param name  the name of the type
   * @param clazz the class
   */
  public static void registerItemProperty(String name, Class<? extends ItemProperty<?, ?>> clazz) {
    itemProperties.put(name, clazz);
  }

  /**
   * Register new icon property
   *
   * @param name  the name of the type
   * @param clazz the class
   */
  public static void registerIconProperty(String name, Class<? extends IconProperty<?>> clazz) {
    iconProperties.put(name, clazz);
  }

  /**
   * Register new menu property
   *
   * @param name  the name of the type
   * @param clazz the class
   */
  public static void registerMenuProperty(String name, Class<? extends MenuProperty<?, ?>> clazz) {
    menuProperties.put(name, clazz);
  }

  /**
   * Register new other property
   *
   * @param name  the name of the type
   * @param clazz the class
   */
  public static void registerOtherProperty(String name, Class<? extends Property<?>> clazz) {
    otherProperties.put(name, clazz);
  }

  /**
   * Check the integrity of the classes
   */
  public static void checkClass() {
    for (Class<? extends ItemProperty<?, ?>> clazz : itemProperties.values()) {
      checkIconProperty(clazz);
    }
    for (Class<? extends IconProperty<?>> clazz : iconProperties.values()) {
      checkIconProperty(clazz);
    }
    for (Class<? extends MenuProperty<?, ?>> clazz : menuProperties.values()) {
      try {
        clazz.getDeclaredConstructor(Menu.class).newInstance(new DummyMenu("dummy"));
      } catch (Exception ex) {
        getInstance().getLogger()
            .log(Level.WARNING, "There is an unknown error on " + clazz.getSimpleName()
                + ". The property will be ignored", ex);
      }
    }
    for (Class<? extends Property<?>> clazz : otherProperties.values()) {
      try {
        clazz.getDeclaredConstructor().newInstance();
      } catch (Exception ex) {
        getInstance().getLogger()
            .log(Level.WARNING, "There is an unknown error on " + clazz.getSimpleName()
                + ". The property will be ignored", ex);
      }
    }
  }

  private static void checkIconProperty(Class<? extends IconProperty<?>> clazz) {
    try {
      clazz.getDeclaredConstructor(Icon.class).newInstance(new RawIcon("", null));
    } catch (Exception ex) {
      getInstance().getLogger()
          .log(Level.WARNING, "There is an unknown error on " + clazz.getSimpleName()
              + ". The property will be ignored", ex);
    }
  }

  public static Map<String, ItemProperty<?, ?>> loadItemPropertiesFromSection(Icon icon,
      ConfigurationSection section) {
    Map<String, ItemProperty<?, ?>> properties = new CaseInsensitiveStringLinkedMap<>();
    Set<String> keys = section.getKeys(false);
    keys.removeIf(s -> !itemProperties.containsKey(s));
    keys.forEach(path -> {
      Class<? extends ItemProperty<?, ?>> clazz = itemProperties.get(path);
      try {
        ItemProperty<?, ?> property = clazz.getDeclaredConstructor(Icon.class).newInstance(icon);
        property.setValue(section.get(path));
        properties.put(path, property);
      } catch (Exception e) {
        getInstance().getLogger()
            .log(Level.WARNING,
                "Something wrong when creating the property '" + path + "' in the icon '" +
                    icon.getName() + "' in the menu '" + icon.getMenu().getName() + "'", e);
      }
    });
    return properties;
  }

  public static Map<String, IconProperty<?>> loadIconPropertiesFromSection(Icon icon,
      ConfigurationSection section) {
    Map<String, IconProperty<?>> properties = new CaseInsensitiveStringLinkedMap<>();
    Set<String> keys = section.getKeys(false);
    keys.removeIf(s -> !iconProperties.containsKey(s));
    keys.forEach(path -> {
      Class<? extends IconProperty<?>> clazz = iconProperties.get(path);
      try {
        IconProperty<?> property = clazz.getDeclaredConstructor(Icon.class).newInstance(icon);
        property.setValue(section.get(path));
        properties.put(path, property);
      } catch (Exception e) {
        getInstance().getLogger()
            .log(Level.WARNING,
                "Something wrong when creating the property '" + path + "' in the icon '" +
                    icon.getName() + "' in the menu '" + icon.getMenu().getName() + "'", e);
      }
    });
    return properties;
  }

  public static Map<String, MenuProperty<?, ?>> loadMenuPropertiesFromSection(Menu<?> menu,
      ConfigurationSection section) {
    Map<String, MenuProperty<?, ?>> properties = new CaseInsensitiveStringLinkedMap<>();
    Set<String> keys = section.getKeys(false);
    keys.removeIf(s -> !menuProperties.containsKey(s));
    keys.forEach(path -> {
      Class<? extends MenuProperty<?, ?>> clazz = menuProperties.get(path);
      try {
        MenuProperty<?, ?> property = clazz.getDeclaredConstructor(Menu.class).newInstance(menu);
        property.setValue(section.get(path));
        properties.put(path, property);
      } catch (Exception e) {
        getInstance().getLogger()
            .log(Level.WARNING,
                "Something wrong when creating the property '" + path + "' in the menu '" + menu
                    .getName() + "'", e);
      }
    });
    return properties;
  }

  public static Map<String, Property<?>> loadOtherPropertiesFromSection(
      ConfigurationSection section) {
    Map<String, Property<?>> properties = new CaseInsensitiveStringLinkedMap<>();
    Set<String> keys = section.getKeys(false);
    keys.removeIf(s -> !otherProperties.containsKey(s));
    keys.forEach(path -> {
      Class<? extends Property<?>> clazz = otherProperties.get(path);
      try {
        Property<?> property = clazz.getDeclaredConstructor().newInstance();
        property.setValue(section.get(path));
        properties.put(path, property);
      } catch (Exception e) {
        getInstance().getLogger()
            .log(Level.WARNING, "Something wrong when creating the property '" + path + "'", e);
      }
    });
    return properties;
  }
}
