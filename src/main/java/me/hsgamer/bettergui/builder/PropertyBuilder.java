package me.hsgamer.bettergui.builder;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.Property;
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
import me.hsgamer.bettergui.object.property.item.impl.Material;
import me.hsgamer.bettergui.object.property.item.impl.Name;
import me.hsgamer.bettergui.object.property.item.impl.RawMaterial;
import me.hsgamer.bettergui.object.property.item.impl.Skull;
import me.hsgamer.bettergui.object.property.menu.MenuAction;
import me.hsgamer.bettergui.object.property.menu.MenuInventoryType;
import me.hsgamer.bettergui.object.property.menu.MenuRequirement;
import me.hsgamer.bettergui.object.property.menu.MenuRows;
import me.hsgamer.bettergui.object.property.menu.MenuTicks;
import me.hsgamer.bettergui.object.property.menu.MenuTitle;
import me.hsgamer.bettergui.object.property.menu.MenuVariable;
import me.hsgamer.hscore.map.CaseInsensitiveStringLinkedMap;
import me.hsgamer.hscore.map.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;

public final class PropertyBuilder {

  private static final Map<String, Function<Icon, ItemProperty<?, ?>>> itemProperties = new CaseInsensitiveStringMap<>();
  private static final Map<String, Function<Icon, IconProperty<?>>> iconProperties = new CaseInsensitiveStringMap<>();
  private static final Map<String, Function<Menu<?>, MenuProperty<?, ?>>> menuProperties = new CaseInsensitiveStringMap<>();
  private static final Map<String, Supplier<Property<?>>> otherProperties = new CaseInsensitiveStringMap<>();

  static {
    registerItemProperty(Name::new, "name");
    registerItemProperty(Lore::new, "lore");
    registerItemProperty(Amount::new, "amount");
    registerItemProperty(Material::new, "id", "material", "mat");
    registerItemProperty(RawMaterial::new, "raw-id", "raw-material", "raw-mat");
    registerItemProperty(Enchantment::new, "enchantment", "enchant");
    registerItemProperty(Flag::new, "flag", "item-flags", "itemflag");
    registerItemProperty(Durability::new, "durability", "damage");
    registerItemProperty(Skull::new, "head", "skull", "skull-owner");

    registerIconProperty(Variable::new, "variable", "placeholder");
    registerIconProperty(ViewRequirement::new, "view-requirement");
    registerIconProperty(ClickRequirement::new, "click-requirement");
    registerIconProperty(CloseOnClick::new, "close-on-click");
    registerIconProperty(ClickCommand::new, "command");

    registerMenuProperty(MenuAction::new, "open-action", "close-action");
    registerMenuProperty(MenuInventoryType::new, "inventory-type", "inventory");
    registerMenuProperty(MenuRows::new, "rows");
    registerMenuProperty(MenuTicks::new, "auto-refresh", "ticks");
    registerMenuProperty(MenuTitle::new, "name", "title");
    registerMenuProperty(MenuRequirement::new, "view-requirement", "close-requirement");
    registerMenuProperty(MenuVariable::new, "variable", "placeholder");
  }

  private PropertyBuilder() {

  }

  /**
   * Register new item property
   *
   * @param propertyFunction the "create property" function
   * @param name             the name of the type
   */
  public static void registerItemProperty(Function<Icon, ItemProperty<?, ?>> propertyFunction,
      String... name) {
    for (String s : name) {
      itemProperties.put(s, propertyFunction);
    }
  }

  /**
   * Register new icon property
   *
   * @param propertyFunction the "create property" function
   * @param name             the name of the type
   */
  public static void registerIconProperty(Function<Icon, IconProperty<?>> propertyFunction,
      String... name) {
    for (String s : name) {
      iconProperties.put(s, propertyFunction);
    }
  }

  /**
   * Register new menu property
   *
   * @param propertyFunction the "create property" function
   * @param name             the name of the type
   */
  public static void registerMenuProperty(Function<Menu<?>, MenuProperty<?, ?>> propertyFunction,
      String... name) {
    for (String s : name) {
      menuProperties.put(s, propertyFunction);
    }
  }

  /**
   * Register new other property
   *
   * @param propertySupplier the property supplier
   * @param name             the name of the type
   */
  public static void registerOtherProperty(Supplier<Property<?>> propertySupplier, String... name) {
    for (String s : name) {
      otherProperties.put(s, propertySupplier);
    }
  }

  /**
   * Register new item property
   *
   * @param name  the name of the type
   * @param clazz the class
   * @deprecated use {@link #registerItemProperty(Function, String...)} instead
   */
  @Deprecated
  public static void registerItemProperty(String name, Class<? extends ItemProperty<?, ?>> clazz) {
    itemProperties.put(name, icon -> {
      try {
        return clazz.getDeclaredConstructor(Icon.class).newInstance(icon);
      } catch (Exception e) {
        throw new RuntimeException("Invalid property class");
      }
    });
  }

  /**
   * Register new icon property
   *
   * @param name  the name of the type
   * @param clazz the class
   * @deprecated use {@link #registerIconProperty(Function, String...)} instead
   */
  @Deprecated
  public static void registerIconProperty(String name, Class<? extends IconProperty<?>> clazz) {
    iconProperties.put(name, icon -> {
      try {
        return clazz.getDeclaredConstructor(Icon.class).newInstance(icon);
      } catch (Exception e) {
        throw new RuntimeException("Invalid property class");
      }
    });
  }

  /**
   * Register new menu property
   *
   * @param name  the name of the type
   * @param clazz the class
   * @deprecated use {@link #registerMenuProperty(Function, String...)} instead
   */
  @Deprecated
  public static void registerMenuProperty(String name, Class<? extends MenuProperty<?, ?>> clazz) {
    menuProperties.put(name, menu -> {
      try {
        return clazz.getDeclaredConstructor(Menu.class).newInstance(menu);
      } catch (Exception e) {
        throw new RuntimeException("Invalid property class");
      }
    });
  }

  /**
   * Register new other property
   *
   * @param name  the name of the type
   * @param clazz the class
   * @deprecated use {@link #registerOtherProperty(Supplier, String...)} instead
   */
  @Deprecated
  public static void registerOtherProperty(String name, Class<? extends Property<?>> clazz) {
    otherProperties.put(name, () -> {
      try {
        return clazz.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Invalid property class");
      }
    });
  }

  public static Map<String, ItemProperty<?, ?>> loadItemPropertiesFromSection(Icon icon,
      ConfigurationSection section) {
    Map<String, ItemProperty<?, ?>> properties = new CaseInsensitiveStringLinkedMap<>();
    section.getKeys(false).stream().filter(itemProperties::containsKey).forEach(path -> {
      ItemProperty<?, ?> property = itemProperties.get(path).apply(icon);
      property.setValue(section.get(path));
      properties.put(path, property);
    });
    return properties;
  }

  public static Map<String, IconProperty<?>> loadIconPropertiesFromSection(Icon icon,
      ConfigurationSection section) {
    Map<String, IconProperty<?>> properties = new CaseInsensitiveStringLinkedMap<>();
    section.getKeys(false).stream().filter(iconProperties::containsKey).forEach(path -> {
      IconProperty<?> property = iconProperties.get(path).apply(icon);
      property.setValue(section.get(path));
      properties.put(path, property);
    });
    return properties;
  }

  public static Map<String, MenuProperty<?, ?>> loadMenuPropertiesFromSection(Menu<?> menu,
      ConfigurationSection section) {
    Map<String, MenuProperty<?, ?>> properties = new CaseInsensitiveStringLinkedMap<>();
    section.getKeys(false).stream().filter(menuProperties::containsKey).forEach(path -> {
      MenuProperty<?, ?> property = menuProperties.get(path).apply(menu);
      property.setValue(section.get(path));
      properties.put(path, property);
    });
    return properties;
  }

  public static Map<String, Property<?>> loadOtherPropertiesFromSection(
      ConfigurationSection section) {
    Map<String, Property<?>> properties = new CaseInsensitiveStringLinkedMap<>();
    section.getKeys(false).stream().filter(otherProperties::containsKey).forEach(path -> {
      Property<?> property = otherProperties.get(path).get();
      property.setValue(section.get(path));
      properties.put(path, property);
    });
    return properties;
  }
}
