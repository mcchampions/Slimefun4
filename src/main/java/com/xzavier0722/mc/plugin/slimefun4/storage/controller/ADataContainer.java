package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import lombok.Getter;
import me.qscbm.slimefun4.utils.CacheMap;

import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Abstract base class for data containers that store key-value data.
 */
@Getter
public abstract class ADataContainer {
    private final String key;
    private final Map<String, String> data;
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

    /**
     * Gets the cached value for the given key.
     *
     * @param key The key to look up
     * @return The cached value, or null if not found
     */
    protected String getCacheInternal(String key) {
        return data.get(key);
    }

    /**
     * Sets whether the data is loaded.
     *
     * @param isDataLoaded Whether data is loaded
     */
    protected void setIsDataLoaded(boolean isDataLoaded) {
        this.isDataLoaded = isDataLoaded;
    }

    /**
     * Sets a value in the cache.
     *
     * @param key      The key to set
     * @param val      The value to set
     * @param override Whether to override existing values
     */
    protected void setCacheInternal(String key, String val, boolean override) {
        if (override) {
            data.put(key, val);
        } else {
            data.putIfAbsent(key, val);
        }
    }

    /**
     * Removes a value from the cache.
     *
     * @param key The key to remove
     * @return The removed value, or null if not found
     */
    protected String removeCacheInternal(String key) {
        return data.remove(key);
    }

    /**
     * Checks if data is loaded and throws an exception if not.
     */
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
        checkData();
        return getCacheInternal(key);
    }


    /**
     * Sets data in the container.
     *
     * @param key The key to set
     * @param val The value to set
     */
    public abstract void setData(String key, String val);

    /**
     * Removes data from the container.
     *
     * @param key The key to remove
     */
    public abstract void removeData(String key);
}
