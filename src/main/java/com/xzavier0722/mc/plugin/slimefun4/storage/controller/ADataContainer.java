package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import lombok.Getter;
import me.qscbm.slimefun4.utils.CacheMap;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class ADataContainer {
    @Getter
    private final String key;
    private final Map<String, String> data;
    @Getter
    private volatile boolean isDataLoaded;

    public ADataContainer(String key) {
        this.key = key;
        data = new CacheMap();
    }

    public ADataContainer(String key, ADataContainer other) {
        this.key = key;
        this.data = other.data;
        this.isDataLoaded = other.isDataLoaded;
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
    }

    public Map<String, String> getAllData() {
        return data;
    }

    public Set<String> getDataKeys() {
        return data.keySet();
    }

    @Nullable
    public String getData(String key) {
        return getCacheInternal(key);
    }

    public abstract void setData(String key, String val);
}
