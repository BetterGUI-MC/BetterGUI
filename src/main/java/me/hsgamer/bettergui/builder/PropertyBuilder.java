package me.hsgamer.bettergui.builder;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Property;
import me.hsgamer.bettergui.object.property.IconProperty;
import me.hsgamer.bettergui.object.property.icon.ClickCommand;
import me.hsgamer.bettergui.object.property.icon.ClickRequirement;
import me.hsgamer.bettergui.object.property.icon.CloseOnClick;
import me.hsgamer.bettergui.object.property.icon.Cooldown;
import me.hsgamer.bettergui.object.property.icon.Variable;
import me.hsgamer.bettergui.object.property.icon.ViewRequirement;
import me.hsgamer.bettergui.object.property.item.ItemProperty;
import me.hsgamer.bettergui.object.property.item.impl.Amount;
import me.hsgamer.bettergui.object.property.item.impl.Enchantment;
import me.hsgamer.bettergui.object.property.item.impl.HideAttributes;
import me.hsgamer.bettergui.object.property.item.impl.Lore;
import me.hsgamer.bettergui.object.property.item.impl.Name;
import me.hsgamer.bettergui.object.property.item.impl.Type;
import me.hsgamer.bettergui.object.property.item.impl.Unbreakable;
import me.hsgamer.bettergui.util.CaseInsensitiveStringLinkedMap;
import me.hsgamer.bettergui.util.CaseInsensitiveStringMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PropertyBuilder {

  private static final Map<String, Class<? extends ItemProperty<?, ?>>> itemProperties = new CaseInsensitiveStringMap<>();
  private static final Map<String, Class<? extends IconProperty<?>>> iconProperties = new CaseInsensitiveStringMap<>();
  private static final Map<String, Class<? extends Property<?>>> otherProperties = new CaseInsensitiveStringMap<>();

  static {
    registerItemProperty("name", Name.class);
    registerItemProperty("lore", Lore.class);
    registerItemProperty("amount", Amount.class);
    registerItemProperty("id", Type.class);
    registerItemProperty("material", Type.class);
    registerItemProperty("hide-attributes", HideAttributes.class);
    registerItemProperty("unbreakable", Unbreakable.class);
    registerItemProperty("enchantment", Enchantment.class);
    registerItemProperty("enchant", Enchantment.class);

    registerIconProperty("cooldown", Cooldown.class);
    registerIconProperty("variable", Variable.class);
    registerIconProperty("placeholder", Variable.class);
    registerIconProperty("view-requirement", ViewRequirement.class);
    registerIconProperty("click-requirement", ClickRequirement.class);
    registerIconProperty("close-on-click", CloseOnClick.class);
    registerIconProperty("command", ClickCommand.class);
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
      clazz.getDeclaredConstructor(Icon.class).newInstance(new Icon("", null) {
        @Override
        public void setFromSection(ConfigurationSection section) {
          // IGNORED
        }

        @Override
        public Optional<ClickableItem> createClickableItem(Player player) {
          return Optional.empty();
        }

        @Override
        public Optional<ClickableItem> updateClickableItem(Player player) {
          return Optional.empty();
        }
      });
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
