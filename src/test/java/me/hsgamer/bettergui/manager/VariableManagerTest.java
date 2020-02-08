package me.hsgamer.bettergui.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.jupiter.api.Test;

class VariableManagerTest {
  @Before
  public void setup() {
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
  }
}
