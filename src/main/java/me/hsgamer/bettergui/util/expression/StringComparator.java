package me.hsgamer.bettergui.util.expression;

import com.udojava.evalex.AbstractLazyFunction;
import com.udojava.evalex.Expression.LazyNumber;
import java.math.BigDecimal;
import java.util.List;

public abstract class StringComparator extends AbstractLazyFunction {

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
      return "1";
    }
  };

  public StringComparator(String name) {
    super(name, 2, true);
  }

  public abstract boolean compare(String s1, String s2);

  @Override
  public LazyNumber lazyEval(List<LazyNumber> lazyParams) {
    if (compare(lazyParams.get(0).getString(), lazyParams.get(1).getString())) {
      return one;
    }
    return zero;
  }
}
