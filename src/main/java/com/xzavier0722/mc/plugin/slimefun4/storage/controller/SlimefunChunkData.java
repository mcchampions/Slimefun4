package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.LocationUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

/**
 * {@link SlimefunChunkData} 是 Slimefun 中用于存储区块内所有方块数据的容器类。
 */
public class SlimefunChunkData extends ADataContainer {
    private static final SlimefunBlockData INVALID_BLOCK_DATA = new SlimefunBlockData(
            new Location(Bukkit.getWorlds().get(0), Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
            "INVALID_BLOCK_DATA_SF_KEY");
    @Getter
    private final Chunk chunk;
    private final Map<String, SlimefunBlockData> sfBlocks;

    SlimefunChunkData(Chunk chunk) {
        super(LocationUtils.getChunkKey(chunk));
        this.chunk = chunk;
        sfBlocks = new ConcurrentHashMap<>();
    }

    public SlimefunBlockData createBlockData(Location l, String sfId) {
        String lKey = LocationUtils.getLocKey(l);
        SlimefunBlockData re = new SlimefunBlockData(l, sfId);
        re.setIsDataLoaded(true);
        sfBlocks.put(lKey, re);

        BlockMenuPreset preset = BlockMenuPreset.getPreset(sfId);
        if (preset != null) {
            re.setBlockMenu(new BlockMenu(preset, l));
        }

        Slimefun.getDatabaseManager().getBlockDataController().saveNewBlock(l, sfId);
        return re;
    }

    @Nullable
    public SlimefunBlockData getBlockData(Location l) {
        return getBlockCacheInternal(LocationUtils.getLocKey(l));
    }

    @Nullable
    public SlimefunBlockData removeBlockData(Location l) {
        String lKey = LocationUtils.getLocKey(l);
        SlimefunBlockData re = removeBlockDataCacheInternal(lKey);
        if (re == null) {
            if (isDataLoaded()) {
                return null;
            }
            sfBlocks.put(lKey, INVALID_BLOCK_DATA);
        }
        Slimefun.getDatabaseManager().getBlockDataController().removeBlockDirectly(l);
        return re;
    }

    void addBlockCacheInternal(SlimefunBlockData data, boolean override) {
        if (override) {
            sfBlocks.put(data.getKey(), data);
        } else {
            sfBlocks.putIfAbsent(data.getKey(), data);
        }
    }

    SlimefunBlockData getBlockCacheInternal(String lKey) {
        SlimefunBlockData re = sfBlocks.get(lKey);
        return re == INVALID_BLOCK_DATA ? null : re;
    }

    Set<SlimefunBlockData> getAllCacheInternal() {
        Set<SlimefunBlockData> re = new HashSet<>(sfBlocks.values());
        re.removeIf(v -> v == INVALID_BLOCK_DATA);
        return re;
    }

    void removeAllCacheInternal() {
        sfBlocks.clear();
    }

    boolean hasBlockCache(String lKey) {
        return sfBlocks.containsKey(lKey);
    }

    SlimefunBlockData removeBlockDataCacheInternal(String lKey) {
        var re = isDataLoaded() ? sfBlocks.remove(lKey) : sfBlocks.put(lKey, INVALID_BLOCK_DATA);
        return re == INVALID_BLOCK_DATA ? null : re;
    }

    public void setData(String key, String val) {
        setCacheInternal(key, val, true);
        Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedChunkDataUpdate(this, key);
    }

    public void removeData(String key) {
        if (removeCacheInternal(key) != null) {
            Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedChunkDataUpdate(this, key);
        }
    }

    public Set<SlimefunBlockData> getAllBlockData() {
        return getAllCacheInternal();
    }

    @Override
    protected void setIsDataLoaded(boolean isDataLoaded) {
        super.setIsDataLoaded(isDataLoaded);
        if (isDataLoaded) {
            sfBlocks.entrySet().removeIf(entry -> entry.getValue() == INVALID_BLOCK_DATA);
        }
    }
}
