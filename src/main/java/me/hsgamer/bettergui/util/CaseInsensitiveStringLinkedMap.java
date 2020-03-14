package me.hsgamer.bettergui.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class CaseInsensitiveStringLinkedMap<V> extends LinkedHashMap<String, V> {

  public CaseInsensitiveStringLinkedMap() {
    super();
  }

  @Override
  public V put(String key, V value) {
    return super.put(key.toLowerCase(), value);
  }

  @Override
  public V get(Object key) {
    return super.get(key.getClass() == String.class ? key.toString().toLowerCase() : key);
  }

  @Override
  public boolean containsKey(Object key) {
    return super.containsKey(key.getClass() == String.class ? key.toString().toLowerCase() : key);
  }

  @Override
  public V remove(Object key) {
    return super.remove(key.getClass() == String.class ? key.toString().toLowerCase() : key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends V> m) {
    Map<String, V> mp = new LinkedHashMap<>();
    m.forEach((s, o) -> mp.put(s.toLowerCase(), o));
    super.putAll(mp);
  }
}
