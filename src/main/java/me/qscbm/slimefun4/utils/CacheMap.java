package me.qscbm.slimefun4.utils;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMap<K,V> extends ConcurrentHashMap<String,String> {
    @Override
    public String put(@Nonnull String key, String value) {
        if (value == null) {
            value = "";
        }
        return super.put(key, value);
    }

    @Override
    public String putIfAbsent(@Nonnull String key, String value) {
        if (value == null) {
            value = "";
        }
        return super.put(key, value);
    }
}
