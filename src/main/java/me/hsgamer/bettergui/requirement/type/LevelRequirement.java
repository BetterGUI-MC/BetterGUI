package me.hsgamer.bettergui.requirement.type;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.TakableRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;

public class LevelRequirement extends TakableRequirement<Integer> {
  public LevelRequirement(RequirementBuilder.Input input) {
    super(input);
    getMenu().getVariableManager().register(getName(), StringReplacer.of((original, uuid) -> {
      Player player = Bukkit.getPlayer(uuid);
      if (player == null) {
        return "";
      }
      int level = getFinalValue(uuid);
      if (level > 0 && player.getLevel() < level) {
        return String.valueOf(level);
      }
      return BetterGUI.getInstance().get(MessageConfig.class).getHaveMetRequirementPlaceholder();
    }), true);
  }

  @Override
  protected Integer convert(Object value, UUID uuid) {
    String replaced = StringReplacerApplier.replace(String.valueOf(value).trim(), uuid, this);
    return Validate.getNumber(replaced)
      .map(BigDecimal::intValue)
      .orElseGet(() -> {
        MessageUtils.sendMessage(uuid, BetterGUI.getInstance().get(MessageConfig.class).getInvalidNumber(replaced));
        return 0;
      });
  }

  @Override
  protected Result checkConverted(UUID uuid, Integer value) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return Result.success();
    }
    if (value > 0 && player.getLevel() < value) {
      return Result.fail();
    }
    return successConditional(uuid1 -> {
      Player player1 = Bukkit.getPlayer(uuid1);
      if (player1 == null) {
        return;
      }
      player1.setLevel(player1.getLevel() - value);
    });
  }

  @Override
  protected boolean getDefaultTake() {
    return true;
  }

  @Override
  protected Object getDefaultValue() {
    return "0";
  }
}
