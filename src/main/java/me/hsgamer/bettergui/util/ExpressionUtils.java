package me.hsgamer.bettergui.util;

import com.udojava.evalex.Expression;
import com.udojava.evalex.Expression.LazyNumber;
import com.udojava.evalex.LazyFunction;
import java.math.BigDecimal;
import java.util.List;

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
    expression.addLazyFunction(new LazyFunction() {
      private final LazyNumber zero = new LazyNumber() {
        public BigDecimal eval() {
          return BigDecimal.ZERO;
        }

        public String getString() {
          return "0";
        }
      };
      private final LazyNumber one = new LazyNumber() {
        public BigDecimal eval() {
          return BigDecimal.ONE;
        }

        public String getString() {
          return null;
        }
      };

      @Override
      public String getName() {
        return "STREQ";
      }

      @Override
      public int getNumParams() {
        return 2;
      }

      @Override
      public boolean numParamsVaries() {
        return false;
      }

      @Override
      public boolean isBooleanFunction() {
        return true;
      }

      @Override
      public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
        if (lazyParams.get(0).getString().equals(lazyParams.get(1).getString())) {
          return one;
        }
        return zero;
      }
    });
  }
}
