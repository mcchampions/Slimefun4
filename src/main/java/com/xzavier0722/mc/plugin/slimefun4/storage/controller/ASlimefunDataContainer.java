package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public abstract class ASlimefunDataContainer {
    @Getter
    private final String key;
    private final Map<String, String> data;
    private volatile boolean isDataLoaded = false;

    public ASlimefunDataContainer(String key) {
        this.key = key;
        data = new ConcurrentHashMap<>();
    }

    public ASlimefunDataContainer(String key, ASlimefunDataContainer other) {
        this.key = key;
        this.data = other.data;
        this.isDataLoaded = other.isDataLoaded;
    }

    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    protected String getCacheInternal(String key) {
        return data.get(key);
    }

    protected void setIsDataLoaded(boolean isDataLoaded) {
        this.isDataLoaded = isDataLoaded;
    }

    protected void setCacheInternal(String key, String val, boolean override) {
        if (override) {
            data.put(key, val);
        } else {
            data.putIfAbsent(key, val);
        }
    }

    protected String removeCacheInternal(String key) {
        return data.remove(key);
    }

    protected void checkData() {
        if (!isDataLoaded) {
            throw new IllegalStateException("Unable to access unloaded data!");
        }
    }


    public Map<String, String> getAllData() {
        checkData();
        return Collections.unmodifiableMap(data);
    }


    public Set<String> getDataKeys() {
        checkData();
        return Collections.unmodifiableSet(data.keySet());
    }

    @Nullable public String getData(String key) {
        checkData();
        return getCacheInternal(key);
    }

    public abstract void setData(String key, String val);
}
