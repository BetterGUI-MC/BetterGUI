package me.hsgamer.bettergui.utils;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.interfaces.StringReplacer;
import me.hsgamer.hscore.expression.ExpressionUtils;
import me.hsgamer.hscore.variable.VariableManager;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Some common string replacers
 */
public class CommonStringReplacers {
  public static final StringReplacer COLORIZE = (original, uuid) -> MessageUtils.colorize(original);
  public static final StringReplacer VARIABLE = VariableManager::setVariables;
  public static final StringReplacer EXPRESSION = (original, uuid) -> Optional.ofNullable(ExpressionUtils.getResult(original)).map(BigDecimal::toPlainString).orElse(original);

  private CommonStringReplacers() {
    // EMPTY
  }
}
