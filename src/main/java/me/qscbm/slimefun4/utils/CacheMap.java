package me.qscbm.slimefun4.utils;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("NullableProblems")
public class CacheMap extends ConcurrentHashMap<String, String> {
    @Override
    public String put(String key, String value) {
        if (value == null) {
            value = "";
        }
        return super.put(key, value);
    }

    @Override
    public String putIfAbsent(String key, String value) {
        if (value == null) {
            value = "";
        }
        return super.put(key, value);
    }
}
