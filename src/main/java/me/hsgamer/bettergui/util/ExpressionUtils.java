package me.hsgamer.bettergui.util;

import com.udojava.evalex.Expression;
import com.udojava.evalex.Expression.ExpressionException;
import java.math.BigDecimal;

public class ExpressionUtils {

  private ExpressionUtils() {

  }

  public static boolean isBoolean(String input) {
    Expression expression = new Expression(input);
    try {
      return expression.isBoolean();
    } catch (ExpressionException e) {
      return false;
    }
  }

  public static BigDecimal getResult(String input) {
    Expression expression = new Expression(input);
    try {
      return expression.eval();
    } catch (ExpressionException e) {
      return null;
    }
  }

  public static boolean isValidExpression(String input) {
    return getResult(input) != null;
  }
}
