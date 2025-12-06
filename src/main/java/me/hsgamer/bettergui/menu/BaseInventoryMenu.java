package me.hsgamer.bettergui.menu;

import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.spigot.SpigotInventoryUI;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import me.hsgamer.bettergui.api.requirement.Requirement;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.InventoryBuilder;
import me.hsgamer.bettergui.util.ProcessApplierConstants;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.bettergui.util.TickUtil;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Pair;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import static me.hsgamer.bettergui.BetterGUI.getInstance;

/**
 * A {@link BaseMenu} for menus using {@link SpigotInventoryUI}
 *
 * @param <M> the type of the {@link Mask} to use in the {@link SpigotInventoryUI}
 */
public abstract class BaseInventoryMenu<M extends Mask> extends BaseMenu {
  private final M mask;
  private final InventoryType inventoryType;
  private final int size;
  private final String title;
  private final Function<UUID, SpigotInventoryUI> inventoryFunction;

  private final Set<UUID> forceClose = new ConcurrentSkipListSet<>();
  private final Map<UUID, SpigotInventoryUI> inventoryMap = new ConcurrentHashMap<>();
  private final long refreshMillis;
  private final long clickDelay;
  private final Button defaultButton;

  protected BaseInventoryMenu(Config config) {
    super(config);

    this.inventoryType = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "inventory-type", "inventory"))
      .map(Object::toString)
      .map(s -> {
        try {
          return InventoryType.valueOf(s.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
          getInstance().getLogger().warning(() -> "The menu \"" + getName() + "\" contains an illegal inventory type");
          return null;
        }
      })
      .orElse(InventoryType.CHEST);

    int size = 27;
    Integer rows = Optional.ofNullable(menuSettings.get("rows"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::intValue)
      .map(i -> Math.max(1, i)).map(i -> i * 9)
      .orElse(null);
    if (rows != null) {
      size = rows;
    }
    Integer slots = Optional.ofNullable(menuSettings.get("slots"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::intValue)
      .map(i -> Math.max(1, i))
      .orElse(null);
    if (slots != null) {
      size = slots;
    }
    this.size = size;

    refreshMillis = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "auto-refresh", "ticks"))
      .map(String::valueOf)
      .flatMap(TickUtil::toMillis)
      .orElse(0L);

    title = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "name", "title"))
      .map(String::valueOf)
      .orElse("");

    Optional<BiFunction<UUID, InventoryHolder, Inventory>> optionalCreator = Optional.ofNullable(menuSettings.get("creator"))
      .map(String::valueOf)
      .flatMap(s -> getInstance().get(InventoryBuilder.class).build(s, Pair.of(this, menuSettings)));
    if (optionalCreator.isPresent()) {
      BiFunction<UUID, InventoryHolder, Inventory> creator = optionalCreator.get();
      this.inventoryFunction = uuid -> new InternalInventoryUI(uuid, holder -> creator.apply(uuid, holder));
    } else if (inventoryType == InventoryType.CHEST) {
      this.inventoryFunction = uuid -> new InternalInventoryUI(uuid, StringReplacerApplier.replace(title, uuid, this), this.size);
    } else {
      this.inventoryFunction = uuid -> new InternalInventoryUI(uuid, StringReplacerApplier.replace(title, uuid, this), inventoryType);
    }

    Button defaultButton = null;
    Map<String, Object> sectionMap = new LinkedHashMap<>();
    for (Map.Entry<String, Object> entry : configSettings.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (!(value instanceof Map)) {
        sectionMap.put(key, value);
        continue;
      }
      Map<String, Object> values = MapUtils.createLowercaseStringObjectMap((Map<?, ?>) value);
      if (key.equalsIgnoreCase("default-icon") || key.equalsIgnoreCase("default-button")) {
        defaultButton = getInstance().get(ButtonBuilder.class).build(new ButtonBuilder.Input(this, "button_" + key, values)).orElse(null);
        if (defaultButton != null) {
          Element.handleIfElement(defaultButton, Element::init);
        }
      } else {
        sectionMap.put(key, value);
      }
    }

    this.defaultButton = defaultButton;

    clickDelay = Optional.ofNullable(MapUtils.getIfFound(menuSettings, "click-delay"))
      .map(String::valueOf)
      .flatMap(Validate::getNumber)
      .map(BigDecimal::longValue)
      .orElse(50L);

    mask = createMask(sectionMap);
    Element.handleIfElement(mask, Element::init);
  }

  @Override
  protected boolean createChecked(Player player, String[] args, boolean bypass) {
    UUID uuid = player.getUniqueId();
    refreshMaskOnCreate(mask, uuid);
    SpigotInventoryUI inventoryUI = inventoryFunction.apply(uuid);
    inventoryMap.put(uuid, inventoryUI);
    inventoryUI.open();
    return true;
  }

  @Override
  public void update(Player player) {
    Optional.ofNullable(inventoryMap.get(player.getUniqueId())).ifPresent(SpigotInventoryUI::update);
  }

  @Override
  public void close(Player player) {
    forceClose.add(player.getUniqueId());
    player.closeInventory();
  }

  @Override
  public void closeAll() {
    Element.handleIfElement(mask, Element::stop);
    Element.handleIfElement(defaultButton, Element::stop);
    List<UUID> viewerIds = new ArrayList<>(inventoryMap.keySet());
    viewerIds.forEach(uuid -> {
      forceClose.add(uuid);
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.closeInventory();
      }
    });
    inventoryMap.clear();
    forceClose.clear();
  }

  protected abstract M createMask(Map<String, Object> sectionMap);

  protected void refreshMaskOnCreate(M mask, UUID uuid) {
    // EMPTY
  }

  public M getMask() {
    return mask;
  }

  public InventoryType getInventoryType() {
    return inventoryType;
  }

  public int getSize() {
    return size;
  }

  public String getTitle() {
    return title;
  }

  private static class UpdateTask {
    private final Runnable runnable;
    private final long millis;
    private Task task;

    private UpdateTask(Runnable runnable, long millis) {
      this.runnable = runnable;
      this.millis = millis;
    }

    public void start() {
      if (task != null && !task.isCancelled()) return;

      task = SchedulerUtil.async().runTimer(() -> {
        runnable.run();
        return true;
      }, millis, millis, TimeUnit.MILLISECONDS);
    }

    public void stop() {
      if (task != null) {
        task.cancel();
      }
    }
  }

  private class InternalInventoryUI extends SpigotInventoryUI {
    private UpdateTask updateTask;
    private Long lastClick;

    public InternalInventoryUI(UUID viewerId, Function<InventoryHolder, Inventory> inventoryFunction) {
      super(viewerId, inventoryFunction);
      setup();
    }

    public InternalInventoryUI(UUID viewerId, String title, int size) {
      super(viewerId, title, size);
      setup();
    }

    public InternalInventoryUI(UUID viewerId, String title, InventoryType type) {
      super(viewerId, title, type);
      setup();
    }

    private void setup() {
      setMask(mask);
      if (defaultButton != null) {
        setDefaultButton(defaultButton);
      }
      update();
      if (refreshMillis >= 0) {
        long millis = refreshMillis == 0 ? 50L : refreshMillis;
        updateTask = new UpdateTask(this::update, millis);
      }
    }

    @Override
    protected void onOpen(InventoryOpenEvent event) {
      UUID uuid = event.getPlayer().getUniqueId();

      if (!openActionApplier.isEmpty()) {
        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> openActionApplier.accept(uuid, process));
        SchedulerUtil.async().run(batchRunnable);
      }

      if (updateTask != null) {
        updateTask.start();
      }
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
      HumanEntity player = event.getPlayer();
      UUID uuid = player.getUniqueId();

      if (!closeActionApplier.isEmpty()) {
        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.ACTION_STAGE).addLast(process -> closeActionApplier.accept(uuid, process));
        SchedulerUtil.async().run(batchRunnable);
      }

      if (!closeRequirementApplier.isEmpty()) {
        if (forceClose.contains(uuid)) {
          forceClose.remove(uuid);
          return;
        }
        Requirement.Result result = closeRequirementApplier.getResult(uuid);

        BatchRunnable batchRunnable = new BatchRunnable();
        batchRunnable.getTaskPool(ProcessApplierConstants.REQUIREMENT_ACTION_STAGE).addLast(process -> {
          result.applier.accept(uuid, process);
          process.next();
        });
        SchedulerUtil.async().run(batchRunnable);

        if (!result.isSuccess) {
          SchedulerUtil.entity(player).run(() -> player.openInventory(event.getInventory()));
          return;
        }
      }

      if (updateTask != null) {
        updateTask.stop();
      }

      inventoryMap.remove(uuid);
    }

    @Override
    protected boolean onClick(InventoryClickEvent event) {
      if (clickDelay <= 0) return true;
      long currentTime = System.currentTimeMillis();
      long lastClick = this.lastClick == null ? 0 : this.lastClick;
      if (currentTime - lastClick < clickDelay) {
        return false;
      }
      this.lastClick = currentTime;
      return true;
    }
  }
}
