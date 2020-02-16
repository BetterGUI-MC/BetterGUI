package me.hsgamer.bettergui.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class TestCase<T> {

  private Predicate<T> predicate;
  private Consumer<T> failConsumer = t -> {
  };
  private Consumer<T> successConsumer = t -> {
  };
  private T testObject;
  private Function<T, TestCase<?>> successNextTestCase;
  private Function<T, TestCase<?>> failNextTestCase;
  private UnaryOperator<T> beforeTestOperator;

  public TestCase() {
    super();
  }

  public TestCase(T testObject) {
    super();
    setTestObject(testObject);
  }

  public static <K> TestCase<K> create(K testObject) {
    return new TestCase<>(testObject);
  }

  public TestCase<T> setPredicate(Predicate<T> predicate) {
    this.predicate = predicate;
    return this;
  }

  public TestCase<T> setFailConsumer(Consumer<T> failConsumer) {
    this.failConsumer = failConsumer;
    return this;
  }

  public TestCase<T> setSuccessConsumer(Consumer<T> successConsumer) {
    this.successConsumer = successConsumer;
    return this;
  }

  public TestCase<T> setTestObject(T object) {
    this.testObject = object;
    return this;
  }

  public TestCase<T> setSuccessNextTestCase(TestCase<T> testCase) {
    setSuccessNextTestCase(testCase::setTestObject);
    return this;
  }

  public TestCase<T> setFailNextTestCase(TestCase<T> testCase) {
    setFailNextTestCase(testCase::setTestObject);
    return this;
  }

  public TestCase<T> setSuccessNextTestCase(Function<T, TestCase<?>> function) {
    this.successNextTestCase = function;
    return this;
  }

  public TestCase<T> setFailNextTestCase(Function<T, TestCase<?>> function) {
    this.failNextTestCase = function;
    return this;
  }

  public TestCase<T> setBeforeTestOperator(UnaryOperator<T> beforeTestOperator) {
    this.beforeTestOperator = beforeTestOperator;
    return this;
  }

  public boolean test() {
    if (predicate == null) {
      throw new NullPointerException("Predicate does not exist");
    }
    if (testObject == null) {
      throw new NullPointerException("Test object does not exist");
    }
    if (beforeTestOperator != null) {
      testObject = beforeTestOperator.apply(testObject);
    }
    if (predicate.test(testObject)) {
      successConsumer.accept(testObject);
      if (successNextTestCase != null) {
        return successNextTestCase.apply(testObject).test();
      } else {
        return true;
      }
    } else {
      failConsumer.accept(testObject);
      if (failNextTestCase != null) {
        return failNextTestCase.apply(testObject).test();
      } else {
        return false;
      }
    }
  }
}
