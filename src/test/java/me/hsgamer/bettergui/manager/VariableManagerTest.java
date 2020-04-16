package me.hsgamer.bettergui.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VariableManagerTest {

  @BeforeEach
  void setup() {
    VariableManager.register("test", (player, identifier) -> "output1");
  }

  @Test
  void hasVariables() {
    assertFalse(VariableManager.hasVariables("test"));
    assertTrue(VariableManager.hasVariables("{test}"));
    assertFalse(VariableManager.hasVariables("{{test"));
    assertTrue(VariableManager.hasVariables("{test}}"));
    assertTrue(VariableManager.hasVariables("{test} here is test"));
  }

  @Test
  void setVariables() {
    Player dummyPlayer = mock(Player.class);
    assertEquals("output1", VariableManager.setVariables("{test}", dummyPlayer));
    assertEquals("output1", VariableManager.setVariables("{test {test}}", dummyPlayer));
    assertEquals("output1 {something output1}",
        VariableManager.setVariables("{test} {something {test}}", dummyPlayer));
  }

  @Test
  void isMatch() {
    List<String> list = Arrays.asList("test1", "test2", "test3");
    assertFalse(VariableManager.isMatch("", list));
    assertTrue(VariableManager.isMatch("{test1}", list));
    assertFalse(VariableManager.isMatch("{test4}", list));
    assertTrue(VariableManager.isMatch("{test1{test2{test3}}}", list));
  }
}
