package com.xzavier0722.mc.plugin.slimefun4.storage.util;

import city.norain.slimefun4.api.menu.UniversalMenu;

import com.xzavier0722.mc.plugin.slimefun4.storage.callback.IAsyncReadCallback;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ADataContainer;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ASlimefunDataContainer;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Utils to access the cached block data.
 * It is safe to use when the target block is in a loaded chunk(such as in block events).
 * By default, please use
 * {@link com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController#getBlockData}
 */
public class StorageCacheUtils {
    private static final Set<ADataContainer> loadingData = new HashSet<>();

    public static boolean hasBlock(Location l) {
        return getBlock(l) != null;
    }


    public static boolean hasUniversalBlock(Location l) {
        return Slimefun.getBlockDataService().getUniversalDataUUID(l.getBlock()).isPresent();
    }


    public static SlimefunBlockData getBlock(Location l) {
        return Slimefun.getDatabaseManager().getBlockDataController().getBlockDataFromCache(l);
    }

    public static boolean isBlock(Location l, String id) {
        SlimefunBlockData blockData = getBlock(l);
        return blockData != null && id.equals(blockData.getSfId());
    }

    public static SlimefunItem getSfItem(Location l) {
        SlimefunBlockData blockData = getBlock(l);

        if (blockData != null) {
            return SlimefunItem.getById(blockData.getSfId());
        } else {
            SlimefunUniversalData universalData = getUniversalBlock(l.getBlock());
            return universalData == null ? null : SlimefunItem.getById(universalData.getSfId());
        }
    }

    public static String getData(Location loc, String key) {
        SlimefunBlockData blockData = getBlock(loc);

        if (blockData != null) {
            return blockData.getData(key);
        } else {
            SlimefunUniversalData uniData = getUniversalBlock(loc.getBlock());

            if (uniData == null) {
                return null;
            }

            return uniData.getData(key);
        }
    }

    public static String getUniversalBlock(UUID uuid, Location loc, String key) {
        SlimefunUniversalBlockData universalData = getUniversalBlock(uuid, loc);
        return universalData == null ? null : universalData.getData(key);
    }

    public static void setData(Location loc, String key, String val) {
        SlimefunBlockData block = getBlock(loc);
        if (block != null) {
            block.setData(key, val);
        } else {
            var uni = getUniversalBlock(loc.getBlock());

            if (uni != null) {
                uni.setData(key, val);
            }
        }
    }

    public static void removeData(Location loc, String key) {
        var block = getBlock(loc);
        if (block != null) {
            block.removeData(key);
        } else {
            var uni = getUniversalBlock(loc.getBlock());

            if (uni != null) {
                uni.removeData(key);
            }
        }
    }

    public static BlockMenu getMenu(Location loc) {
        SlimefunBlockData blockData = getBlock(loc);
        if (blockData == null) {
            return null;
        }

        if (!blockData.isDataLoaded()) {
            requestLoad(blockData);
            return null;
        }

        return blockData.getBlockMenu();
    }

    public static SlimefunUniversalBlockData getUniversalBlock(UUID uuid) {
        var uniData = Slimefun.getDatabaseManager().getBlockDataController().getUniversalBlockDataFromCache(uuid);

        if (uniData == null) {
            return null;
        }

        if (!uniData.isDataLoaded()) {
            requestLoad(uniData);
            return null;
        }

        return uniData;
    }

    public static SlimefunUniversalBlockData getUniversalBlock(UUID uuid, Location l) {
        var uniData = getUniversalBlock(uuid);

        if (uniData != null) {
            uniData.setLastPresent(new BlockPosition(l));
        }

        return uniData;
    }

    /**
     * Get universal data from block
     *
     * @param block {@link Block}
     * @return {@link SlimefunUniversalBlockData}
     */
    public static SlimefunUniversalBlockData getUniversalBlock(Block block) {
        return Slimefun.getDatabaseManager()
                .getBlockDataController()
                .getUniversalBlockDataFromCache(block.getLocation())
                .orElse(null);
    }

    /**
     * Get universal menu from block
     *
     * @param block {@link Block}
     * @return {@link SlimefunUniversalData}
     */
    public static UniversalMenu getUniversalMenu(Block block) {
        SlimefunUniversalBlockData uniData = getUniversalBlock(block);

        if (uniData == null) {
            return null;
        }

        return uniData.getMenu();
    }

    public static UniversalMenu getUniversalMenu(UUID uuid, Location l) {
        SlimefunUniversalBlockData uniData = Slimefun.getDatabaseManager().getBlockDataController().getUniversalBlockDataFromCache(uuid);

        if (uniData == null) {
            return null;
        }

        if (!uniData.isDataLoaded()) {
            requestLoad(uniData);
            return null;
        }

        uniData.setLastPresent(new BlockPosition(l));

        return uniData.getMenu();
    }

    public static boolean isBlockPendingRemove(Block block) {
        if (hasBlock(block.getLocation())) {
            return getBlock(block.getLocation()).isPendingRemove();
        }

        if (hasUniversalBlock(block.getLocation())) {
            return getUniversalBlock(block).isPendingRemove();
        }

        return false;
    }

    public static void requestLoad(ADataContainer data) {
        if (data.isDataLoaded()) {
            return;
        }

        if (loadingData.contains(data)) {
            return;
        }

        synchronized (loadingData) {
            if (loadingData.contains(data)) {
                return;
            }
            loadingData.add(data);
        }

        if (data instanceof SlimefunBlockData blockData) {
            Slimefun.getDatabaseManager()
                    .getBlockDataController()
                    .loadBlockDataAsync(blockData, new IAsyncReadCallback<>() {
                        @Override
                        public void onResult(SlimefunBlockData result) {
                            loadingData.remove(data);
                        }
                    });
        } else if (data instanceof SlimefunUniversalData uniData) {
            Slimefun.getDatabaseManager()
                    .getBlockDataController()
                    .loadUniversalDataAsync(uniData, new IAsyncReadCallback<>() {
                        @Override
                        public void onResult(SlimefunUniversalData result) {
                            loadingData.remove(data);
                        }
                    });
        }
    }

    public static void executeAfterLoad(ASlimefunDataContainer data, Runnable execute, boolean runOnMainThread) {
        if (data instanceof SlimefunBlockData blockData) {
            executeAfterLoad(blockData, execute, runOnMainThread);
        } else if (data instanceof SlimefunUniversalData universalData) {
            executeAfterLoad(universalData, execute, runOnMainThread);
        }
    }

    public static void executeAfterLoad(SlimefunBlockData data, Runnable execute, boolean runOnMainThread) {
        if (data.isDataLoaded()) {
            execute.run();
            return;
        }

        Slimefun.getDatabaseManager().getBlockDataController().loadBlockDataAsync(data, new IAsyncReadCallback<>() {
            @Override
            public boolean runOnMainThread() {
                return runOnMainThread;
            }

            @Override
            public void onResult(SlimefunBlockData result) {
                execute.run();
            }
        });
    }

    public static void executeAfterLoad(SlimefunUniversalData data, Runnable execute, boolean runOnMainThread) {
        if (data.isDataLoaded()) {
            execute.run();
            return;
        }

        Slimefun.getDatabaseManager().getBlockDataController().loadUniversalDataAsync(data, new IAsyncReadCallback<>() {
            @Override
            public boolean runOnMainThread() {
                return runOnMainThread;
            }

            @Override
            public void onResult(SlimefunUniversalData result) {
                execute.run();
            }
        });
    }
}
