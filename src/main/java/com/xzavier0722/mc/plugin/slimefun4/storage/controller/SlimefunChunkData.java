package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.LocationUtils;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class SlimefunChunkData extends ASlimefunDataContainer {
    private static final SlimefunBlockData INVALID_BLOCK_DATA = new SlimefunBlockData(
            new Location(Bukkit.getWorlds().get(0), Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
            SlimefunItem.getById("INVALID_BLOCK_DATA_SF_KEY"));
    private final Chunk chunk;
    private final Map<String, SlimefunBlockData> sfBlocks;

    @ParametersAreNonnullByDefault
    SlimefunChunkData(Chunk chunk) {
        super(LocationUtils.getChunkKey(chunk));
        this.chunk = chunk;
        sfBlocks = new ConcurrentHashMap<>();
    }

    
    public Chunk getChunk() {
        return chunk;
    }

    
    @ParametersAreNonnullByDefault
    public SlimefunBlockData createBlockData(Location l, String sfId) {
        var lKey = LocationUtils.getLocKey(l);
        if (getBlockCacheInternal(lKey) != null) {
            throw new IllegalStateException("There already a block in this location: " + lKey);
        }
        var re = new SlimefunBlockData(l, SlimefunItem.getById(sfId));
        re.setIsDataLoaded(true);
        sfBlocks.put(lKey, re);

        var preset = BlockMenuPreset.getPreset(sfId);
        if (preset != null) {
            re.setBlockMenu(new BlockMenu(preset, l));
        }

        Slimefun.getDatabaseManager().getBlockDataController().saveNewBlock(l, sfId);

        return re;
    }

    @ParametersAreNonnullByDefault
    public SlimefunBlockData createBlockData(Location l, ReadableNBT nbt) {
        var lKey = LocationUtils.getLocKey(l);
        if (getBlockCacheInternal(lKey) != null) {
            throw new IllegalStateException("There already a block in this location: " + lKey);
        }
        var re = new SlimefunBlockData(l, nbt.toString());
        re.setIsDataLoaded(true);
        sfBlocks.put(lKey, re);

        var preset = BlockMenuPreset.getPreset(nbt.getString("slimefun:slimefun_id"));
        if (preset != null) {
            re.setBlockMenu(new BlockMenu(preset, l));
        }

        Slimefun.getDatabaseManager().getBlockDataController().saveNewBlock(l, nbt);

        return re;
    }

    @Nullable @ParametersAreNonnullByDefault
    public SlimefunBlockData getBlockData(Location l) {
        checkData();
        return getBlockCacheInternal(LocationUtils.getLocKey(l));
    }

    @Nullable @ParametersAreNonnullByDefault
    public SlimefunBlockData removeBlockData(Location l) {
        var lKey = LocationUtils.getLocKey(l);
        var re = removeBlockDataCacheInternal(lKey);
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
        var re = sfBlocks.get(lKey);
        return re == INVALID_BLOCK_DATA ? null : re;
    }

    Set<SlimefunBlockData> getAllCacheInternal() {
        var re = new HashSet<>(sfBlocks.values());
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

    @ParametersAreNonnullByDefault
    public void setData(String key, String val) {
        checkData();
        setCacheInternal(key, val, true);
        Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedChunkDataUpdate(this, key);
    }

    @ParametersAreNonnullByDefault
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
