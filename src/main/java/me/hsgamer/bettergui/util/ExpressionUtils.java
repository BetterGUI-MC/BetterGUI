package me.hsgamer.bettergui.util;

import com.udojava.evalex.Expression;
import java.math.BigDecimal;
import me.hsgamer.bettergui.util.expression.string.Contains;
import me.hsgamer.bettergui.util.expression.string.EndsWith;
import me.hsgamer.bettergui.util.expression.string.Equals;
import me.hsgamer.bettergui.util.expression.string.EqualsIgnoreCase;
import me.hsgamer.bettergui.util.expression.string.StartsWith;

public class ExpressionUtils {

  private ExpressionUtils() {

  }

  public static boolean isBoolean(String input) {
    Expression expression = new Expression(input);
    addStringFunction(expression);
    try {
      return expression.isBoolean();
    } catch (Exception e) {
      return false;
    }
  }

  public static BigDecimal getResult(String input) {
    Expression expression = new Expression(input);
    addStringFunction(expression);
    try {
      return expression.eval();
    } catch (Exception e) {
      return null;
    }
  }

  public static boolean isValidExpression(String input) {
    return getResult(input) != null;
  }

  private static void addStringFunction(Expression expression) {
    expression.addLazyFunction(new Equals());
    expression.addLazyFunction(new EqualsIgnoreCase());
    expression.addLazyFunction(new Contains());
    expression.addLazyFunction(new StartsWith());
    expression.addLazyFunction(new EndsWith());
  }
}
