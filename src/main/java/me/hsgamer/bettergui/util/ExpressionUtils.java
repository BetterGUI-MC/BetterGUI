package me.hsgamer.bettergui.util;

import com.udojava.evalex.Expression;
import com.udojava.evalex.LazyFunction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.hsgamer.bettergui.util.expression.string.Contains;
import me.hsgamer.bettergui.util.expression.string.EndsWith;
import me.hsgamer.bettergui.util.expression.string.Equals;
import me.hsgamer.bettergui.util.expression.string.EqualsIgnoreCase;
import me.hsgamer.bettergui.util.expression.string.Length;
import me.hsgamer.bettergui.util.expression.string.StartsWith;

public final class ExpressionUtils {

  private static final List<LazyFunction> lazyFunctionList = new ArrayList<>();

  static {
    lazyFunctionList.add(new Equals());
    lazyFunctionList.add(new EqualsIgnoreCase());
    lazyFunctionList.add(new Contains());
    lazyFunctionList.add(new StartsWith());
    lazyFunctionList.add(new EndsWith());
    lazyFunctionList.add(new Length());
  }

  private ExpressionUtils() {

  }

  public static boolean isBoolean(String input) {
    Expression expression = new Expression(input);
    applyLazyFunction(expression);
    try {
      return expression.isBoolean();
    } catch (Exception e) {
      return false;
    }
  }

  public static BigDecimal getResult(String input) {
    Optional<BigDecimal> number = Validate.getNumber(input);
    if (number.isPresent()) {
      return number.get();
    }

    Expression expression = new Expression(input);
    applyLazyFunction(expression);
    try {
      return expression.eval();
    } catch (Exception e) {
      return null;
    }
  }

  public static boolean isValidExpression(String input) {
    return getResult(input) != null;
  }

  private static void applyLazyFunction(Expression expression) {
    lazyFunctionList.forEach(expression::addLazyFunction);
  }

  @SuppressWarnings("unused")
  public static void registerLazyFunction(LazyFunction lazyFunction) {
    lazyFunctionList.add(lazyFunction);
  }
}
