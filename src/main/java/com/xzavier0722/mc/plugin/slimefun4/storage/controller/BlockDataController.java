package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import city.norain.slimefun4.api.menu.UniversalMenu;
import city.norain.slimefun4.api.menu.UniversalMenuPreset;
import city.norain.slimefun4.utils.InventoryUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.IDataSourceAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.callback.IAsyncReadCallback;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataScope;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.FieldKey;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.RecordKey;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.RecordSet;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.ScopeKey;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes.UniversalBlock;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes.UniversalDataTrait;
import com.xzavier0722.mc.plugin.slimefun4.storage.event.SlimefunChunkDataLoadEvent;
import com.xzavier0722.mc.plugin.slimefun4.storage.task.DelayedSavingLooperTask;
import com.xzavier0722.mc.plugin.slimefun4.storage.task.DelayedTask;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.DataUtils;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.InvStorageUtils;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.LocationUtils;
import io.github.bakedlibs.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.services.BlockDataService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nullable;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * 方块数据控制器
 * <p>
 * 用于管理区块中的 Slimefun 方块数据
 * <p>
 * {@link SlimefunBlockData}
 * {@link SlimefunUniversalData}
 *
 * @author Xzavier0722
 * @author NoRainCity
 */
public class BlockDataController extends ADataController {
    private final Map<LinkedKey, DelayedTask> delayedWriteTasks;
    /**
     * 区块数据缓存
     */
    private final Map<String, SlimefunChunkData> loadedChunk;
    /**
     * 通用数据缓存
     */
    private final Map<UUID, SlimefunUniversalData> loadedUniversalData;
    /**
     * 方块物品栏快照
     */
    private final Map<String, List<Pair<ItemStack, Integer>>> invSnapshots;
    /**
     * 全局控制器加载数据锁
     * <p>
     * {@link ScopedLock}
     */
    private final ScopedLock lock;
    /**
     * 延时加载模式标志
     */
    private boolean enableDelayedSaving;

    private int delayedSecond;
    private BukkitTask looperTask;
    /**
     * 区块数据加载模式
     * {@link ChunkDataLoadMode}
     */
    private ChunkDataLoadMode chunkDataLoadMode;
    /**
     * 初始化加载中标志
     */
    private boolean initLoading;

    BlockDataController() {
        super(DataType.BLOCK_STORAGE);
        delayedWriteTasks = new HashMap<>();
        loadedChunk = new ConcurrentHashMap<>();
        loadedUniversalData = new ConcurrentHashMap<>();
        invSnapshots = new ConcurrentHashMap<>();
        lock = new ScopedLock();
    }

    /**
     * 初始化数据控制器
     *
     * @param dataAdapter    使用的 {@link IDataSourceAdapter}
     * @param maxReadThread  最大数据库读线程数
     * @param maxWriteThread 最大数据库写线程数
     */
    @Override
    public void init(IDataSourceAdapter<?> dataAdapter, int maxReadThread, int maxWriteThread) {
        super.init(dataAdapter, maxReadThread, maxWriteThread);
        this.chunkDataLoadMode = Slimefun.getDatabaseManager().getChunkDataLoadMode();
        initLoadData();
    }

    /**
     * 初始化加载数据
     */
    private void initLoadData() {
        switch (chunkDataLoadMode) {
            case LOAD_WITH_CHUNK -> loadLoadedChunks();
            case LOAD_ON_STARTUP -> loadLoadedWorlds();
        }

        Bukkit.getScheduler()
                .runTaskLater(
                        Slimefun.instance(),
                        () -> {
                            initLoading = true;
                            loadUniversalRecord();
                            initLoading = false;
                        },
                        1);
    }

    /**
     * 加载所有服务器已加载的世界中的数据
     */
    private void loadLoadedWorlds() {
        Bukkit.getScheduler()
                .runTaskLater(
                        Slimefun.instance(),
                        () -> {
                            initLoading = true;
                            for (World world : Bukkit.getWorlds()) {
                                loadWorld(world);
                            }
                            initLoading = false;
                        },
                        1);
    }

    /**
     * 加载所有服务器已加载的世界区块中的数据
     */
    private void loadLoadedChunks() {
        Bukkit.getScheduler()
                .runTaskLater(
                        Slimefun.instance(),
                        () -> {
                            initLoading = true;
                            for (World world : Bukkit.getWorlds()) {
                                for (Chunk chunk : world.getLoadedChunks()) {
                                    loadChunk(chunk, false);
                                }
                            }
                            initLoading = false;
                        },
                        1);
    }

    /**
     * 初始化延时加载任务
     *
     * @param p               插件实例
     * @param delayedSecond   首次执行延时
     * @param forceSavePeriod 强制保存周期
     */
    public void initDelayedSaving(Plugin p, int delayedSecond, int forceSavePeriod) {
        enableDelayedSaving = true;
        this.delayedSecond = delayedSecond;
        looperTask = Bukkit.getScheduler()
                .runTaskTimerAsynchronously(
                        p,
                        new DelayedSavingLooperTask(
                                forceSavePeriod,
                                () -> {
                                    synchronized (delayedWriteTasks) {
                                        return new HashMap<>(delayedWriteTasks);
                                    }
                                },
                                key -> {
                                    synchronized (delayedWriteTasks) {
                                        delayedWriteTasks.remove(key);
                                    }
                                }),
                        20,
                        20);
    }

    public boolean isDelayedSavingEnabled() {
        return enableDelayedSaving;
    }

    public void setDelayedSavingEnable(boolean isEnable) {
        enableDelayedSaving = isEnable;
    }

    /**
     * 在指定位置新建方块
     *
     * @param l    Slimefun 方块位置 {@link Location}
     * @param sfId Slimefun 物品 ID {@link SlimefunItem#getId()}
     * @return 方块数据, {@link SlimefunBlockData}
     */
    public ASlimefunDataContainer createBlock(Location l, String sfId) {
        SlimefunItem sfItem = SlimefunItem.getById(sfId);

        if (sfItem instanceof UniversalBlock) {
            SlimefunUniversalData re = createUniversalBlock(l, sfId);
            if (Slimefun.getRegistry().getTickerBlocks().contains(sfId)) {
                Slimefun.getTickerTask().enableTicker(l, re.getUUID());
            }
            return re;
        }
        SlimefunBlockData re = getChunkDataCache(l.getChunk(), true).createBlockData(l, sfId);
        if (Slimefun.getRegistry().getTickerBlocks().contains(sfId)) {
            if ("CARGO_MANAGER".equalsIgnoreCase(sfId)) {
                Slimefun.instance().getCargoTickerTask().enableTicker(l);
            } else {
                Slimefun.getTickerTask().enableTicker(l);
            }
        }
        return re;
    }

    public SlimefunUniversalBlockData createUniversalBlock(Location l, String sfId) {
        UUID uuid = UUID.randomUUID();
        SlimefunUniversalBlockData uniData = new SlimefunUniversalBlockData(uuid, sfId, l);

        uniData.setIsDataLoaded(true);

        uniData.initTraits();
        uniData.initLastPresent();

        loadedUniversalData.put(uuid, uniData);

        UniversalMenuPreset preset = UniversalMenuPreset.getPreset(sfId);
        if (preset != null) {
            uniData.setMenu(new UniversalMenu(preset, uuid, l));
        }

        if (Slimefun.getRegistry().getTickerBlocks().contains(sfId)) {
            Slimefun.getTickerTask().enableTicker(l, uuid);
        }

        Slimefun.getDatabaseManager().getBlockDataController().saveUniversalData(uuid, sfId, uniData.getTraits());

        return uniData;
    }

    void saveNewBlock(Location l, String sfId) {
        String lKey = LocationUtils.getLocKey(l);

        RecordKey key = new RecordKey(DataScope.BLOCK_RECORD);
        // key.addCondition(FieldKey.LOCATION, lKey);

        RecordSet data = new RecordSet();
        data.put(FieldKey.LOCATION, lKey);
        data.put(FieldKey.CHUNK, LocationUtils.getChunkKey(l.getChunk()));
        data.put(FieldKey.SLIMEFUN_ID, sfId);

        LocationKey scopeKey = new LocationKey(DataScope.NONE, l);
        removeDelayedBlockDataUpdates(scopeKey); // Shouldn't have.. But for safe..
        scheduleWriteTask(scopeKey, key, data, true);
    }

    /**
     * Save certain universal data
     *
     * @param uuid universal data uuid
     * @param sfId the item universal data represents
     */
    void saveUniversalData(UUID uuid, String sfId, Set<UniversalDataTrait> traits) {
        RecordKey key = new RecordKey(DataScope.UNIVERSAL_RECORD);

        RecordSet data = new RecordSet();
        data.put(FieldKey.UNIVERSAL_UUID, uuid.toString());
        data.put(FieldKey.SLIMEFUN_ID, sfId);
        data.put(
                FieldKey.UNIVERSAL_TRAITS,
                String.join(",", traits.stream().map(Enum::name).toList()));

        UUIDKey scopeKey = new UUIDKey(DataScope.NONE, uuid);
        removeDelayedBlockDataUpdates(scopeKey); // Shouldn't have.. But for safe..
        scheduleWriteTask(scopeKey, key, data, true);
    }

    /**
     * Remove slimefun block data at specific location
     *
     * @param l slimefun block location {@link Location}
     */
    public void removeBlock(Location l) {
        SlimefunBlockData removed = getChunkDataCache(l.getChunk(), true).removeBlockData(l);
        if (removed == null) {
            getUniversalBlockDataFromCache(l)
                    .ifPresentOrElse(data -> removeUniversalBlockData(data.getUUID(), l), () -> Slimefun.getBlockDataService()
                            .getUniversalDataUUID(l.getBlock())
                            .ifPresent(uuid -> removeUniversalBlockData(uuid, l)));
            return;
        }

        if (!removed.isDataLoaded()) {
            return;
        }

        BlockMenu menu = removed.getBlockMenu();
        if (menu != null) {
            menu.lock();
        }

        if (Slimefun.getRegistry().getTickerBlocks().contains(removed.getSfId())) {
            if ("CARGO_MANAGER".equalsIgnoreCase(removed.getSfId())) {
                Slimefun.instance().getCargoTickerTask().disableTicker(l);
            } else {
                Slimefun.getTickerTask().disableTicker(l);
            }
        }
    }

    public void removeBlockData(Location l) {
        SlimefunBlockData removed = getChunkDataCache(l.getChunk(), true).removeBlockData(l);

        if (removed == null || !removed.isDataLoaded()) {
            return;
        }

        BlockMenu menu = removed.getBlockMenu();
        if (menu != null) {
            InventoryUtil.closeInventory(menu.toInventory());
        }

        if (Slimefun.getRegistry().getTickerBlocks().contains(removed.getSfId())) {
            Slimefun.getTickerTask().disableTicker(l);
        }
    }

    public void removeUniversalBlockData(UUID uuid, Location lastPresent) {
        SlimefunUniversalData toRemove = loadedUniversalData.get(uuid);

        if (toRemove == null) {
            return;
        }

        if (!toRemove.isDataLoaded()) {
            return;
        }

        toRemove.setPendingRemove(true);

        if (toRemove instanceof SlimefunUniversalBlockData ubd) {
            toRemove.setPendingRemove(true);
            removeUniversalBlockDirectly(uuid);

            UniversalMenu menu = ubd.getMenu();
            if (menu != null) {
                menu.lock();
            }

            if (Slimefun.getRegistry().getTickerBlocks().contains(toRemove.getSfId())) {
                Slimefun.getTickerTask().disableTicker(lastPresent);
            }
        }

        loadedUniversalData.remove(uuid);
    }

    void removeBlockDirectly(Location l) {
        LocationKey scopeKey = new LocationKey(DataScope.NONE, l);
        removeDelayedBlockDataUpdates(scopeKey);

        RecordKey key = new RecordKey(DataScope.BLOCK_RECORD);
        key.addCondition(FieldKey.LOCATION, LocationUtils.getLocKey(l));
        scheduleDeleteTask(scopeKey, key, true);
    }

    void removeUniversalBlockDirectly(UUID uuid) {
        UUIDKey scopeKey = new UUIDKey(DataScope.NONE, uuid);
        removeDelayedBlockDataUpdates(scopeKey);

        RecordKey key = new RecordKey(DataScope.UNIVERSAL_RECORD);
        key.addCondition(FieldKey.UNIVERSAL_UUID, uuid.toString());
        scheduleDeleteTask(scopeKey, key, true);
    }

    /**
     * Get slimefun block data at specific location
     *
     * @param l slimefun block location {@link Location}
     * @return {@link SlimefunBlockData}
     */
    @Nullable
    public SlimefunBlockData getBlockData(Location l) {
        if (chunkDataLoadMode.readCacheOnly()) {
            return getBlockDataFromCache(l);
        }

        // fix issue #935
        SlimefunChunkData chunkData = getChunkDataCache(l, false);
        String lKey = LocationUtils.getLocKey(l);
        if (chunkData != null) {
            SlimefunBlockData re = chunkData.getBlockCacheInternal(lKey);
            if (re != null || chunkData.hasBlockCache(lKey) || chunkData.isDataLoaded()) {
                return re;
            }
        }

        RecordKey key = new RecordKey(DataScope.BLOCK_RECORD);
        key.addCondition(FieldKey.LOCATION, lKey);
        key.addField(FieldKey.SLIMEFUN_ID);

        List<RecordSet> result = getData(key);
        SlimefunBlockData re =
                result.isEmpty() ? null : new SlimefunBlockData(l, result.get(0).get(FieldKey.SLIMEFUN_ID));
        if (re != null) {
            // fix issue #935
            chunkData = getChunkDataCache(l, true);
            chunkData.addBlockCacheInternal(re, false);
            re = chunkData.getBlockCacheInternal(lKey);
        }
        return re;
    }

    /**
     * Get slimefun block data at specific location asynchronous
     *
     * @param l        slimefun block location {@link Location}
     * @param callback operation when block data fetched {@link IAsyncReadCallback}
     */
    public void getBlockDataAsync(Location l, IAsyncReadCallback<SlimefunBlockData> callback) {
        scheduleReadTask(() -> invokeCallback(callback, getBlockData(l)));
    }

    /**
     * Get slimefun block data at specific location from cache
     *
     * @param l slimefun block location {@link Location}
     * @return {@link SlimefunBlockData}
     */
    public SlimefunBlockData getBlockDataFromCache(Location l) {
        return getBlockDataFromCache(LocationUtils.getChunkKey(l), LocationUtils.getLocKey(l));
    }

    /**
     * Get slimefun universal data
     *
     * @param uuid universal data uuid {@link UUID}
     */
    @Nullable
    public SlimefunUniversalBlockData getUniversalBlockData(UUID uuid) {
        RecordKey key = new RecordKey(DataScope.UNIVERSAL_RECORD);
        key.addCondition(FieldKey.UNIVERSAL_UUID, uuid.toString());
        key.addField(FieldKey.SLIMEFUN_ID);

        List<RecordSet> result = getData(key);

        if (result.isEmpty()) {
            return null;
        }

        SlimefunUniversalBlockData newData = new SlimefunUniversalBlockData(uuid, result.get(0).get(FieldKey.SLIMEFUN_ID));

        Arrays.stream(result.get(0).get(FieldKey.UNIVERSAL_TRAITS).split(",")).forEach(tname -> {
            for (UniversalDataTrait trait : UniversalDataTrait.values()) {
                if (trait.name().equals(tname)) {
                    newData.getTraits().add(trait);
                }
            }
        });

        return newData;
    }

    /**
     * Get slimefun universal data asynchronous
     *
     * @param uuid     universal data uuid {@link UUID}
     * @param callback operation when block data fetched {@link IAsyncReadCallback}
     */
    public void getUniversalBlockData(UUID uuid, IAsyncReadCallback<SlimefunUniversalBlockData> callback) {
        scheduleReadTask(() -> invokeCallback(callback, getUniversalBlockData(uuid)));
    }

    /**
     * Get slimefun universal data from cache
     *
     * @param uuid universal data uuid {@link UUID}
     */
    @Nullable
    public SlimefunUniversalBlockData getUniversalBlockDataFromCache(UUID uuid) {
        SlimefunUniversalData cache = loadedUniversalData.get(uuid);

        if (cache instanceof SlimefunUniversalBlockData ubd) {
            return ubd;
        } else {
            return null;
        }
    }

    /**
     * Get slimefun universal data from cache by location
     *
     * @param l Slimefun block location {@link Location}
     */
    public Optional<SlimefunUniversalBlockData> getUniversalBlockDataFromCache(Location l) {
        for (SlimefunUniversalData uniData : loadedUniversalData.values()) {
            if (uniData instanceof SlimefunUniversalBlockData ubd) {
                if (!ubd.isDataLoaded() || !ubd.hasTrait(UniversalDataTrait.BLOCK) || ubd.getLastPresent() == null) {
                    continue;
                }

                if (l.equals(ubd.getLastPresent().toLocation())) {
                    return Optional.of(ubd);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Move block data to specific location
     * <p>
     * Similar to original BlockStorage#move.
     *
     * @param blockData the block data {@link SlimefunBlockData} need to move
     * @param target    move target {@link Location}
     */
    public void setBlockDataLocation(SlimefunBlockData blockData, Location target) {
        if (LocationUtils.isSameLoc(blockData.getLocation(), target)) {
            return;
        }

        boolean hasTicker = false;

        if (blockData.isDataLoaded() && Slimefun.getRegistry().getTickerBlocks().contains(blockData.getSfId())) {
            if ("CARGO_MANAGER".equalsIgnoreCase(blockData.getSfId())) {
                Slimefun.instance().getCargoTickerTask().disableTicker(blockData.getLocation());
            } else {
                Slimefun.getTickerTask().disableTicker(blockData.getLocation());
            }
            hasTicker = true;
        }

        BlockMenu menu = null;

        if (blockData.isDataLoaded() && blockData.getBlockMenu() != null) {
            menu = blockData.getBlockMenu();
            menu.lock();
        }

        try {
            var chunk = blockData.getLocation().getChunk();
            var chunkData = getChunkDataCache(chunk, false);
            if (chunkData != null) {
                chunkData.removeBlockDataCacheInternal(blockData.getKey());
            }

            var newBlockData = new SlimefunBlockData(target, blockData);
            var key = new RecordKey(DataScope.BLOCK_RECORD);
            if (LocationUtils.isSameChunk(blockData.getLocation().getChunk(), target.getChunk())) {
                if (chunkData == null) {
                    chunkData = getChunkDataCache(chunk, true);
                }
                key.addField(FieldKey.CHUNK);
            } else {
                chunkData = getChunkDataCache(target.getChunk(), true);
            }

            chunkData.addBlockCacheInternal(newBlockData, true);

            if (menu != null) {
                newBlockData.setBlockMenu(new BlockMenu(menu.getPreset(), target, menu.getInventory()));
            }

            key.addField(FieldKey.LOCATION);
            key.addCondition(FieldKey.LOCATION, blockData.getKey());

            var data = new RecordSet();
            data.put(FieldKey.LOCATION, newBlockData.getKey());
            data.put(FieldKey.CHUNK, chunkData.getKey());
            data.put(FieldKey.SLIMEFUN_ID, blockData.getSfId());
            var scopeKey = new LocationKey(DataScope.NONE, blockData.getLocation());
            synchronized (delayedWriteTasks) {
                var it = delayedWriteTasks.entrySet().iterator();
                while (it.hasNext()) {
                    var next = it.next();
                    if (scopeKey.equals(next.getKey().getParent())) {
                        next.getValue().runUnsafely();
                        it.remove();
                    }
                }
            }

            scheduleWriteTask(scopeKey, key, data, true);

            if (hasTicker) {
                if ("CARGO_MANAGER".equalsIgnoreCase(blockData.getSfId())) {
                    Slimefun.instance().getCargoTickerTask().enableTicker(target);
                } else {
                    Slimefun.getTickerTask().enableTicker(target);
                }
            }
        } finally {
            if (menu != null) {
                menu.unlock();
            }
        }
    }

    private SlimefunBlockData getBlockDataFromCache(String cKey, String lKey) {
        SlimefunChunkData chunkData = loadedChunk.get(cKey);
        return chunkData == null ? null : chunkData.getBlockCacheInternal(lKey);
    }

    public void loadChunk(Chunk chunk, boolean isNewChunk) {
        SlimefunChunkData chunkData = getChunkDataCache(chunk, true);

        if (isNewChunk) {
            chunkData.setIsDataLoaded(true);
            Bukkit.getPluginManager().callEvent(new SlimefunChunkDataLoadEvent(chunkData));
            return;
        }

        if (chunkData.isDataLoaded()) {
            return;
        }

        loadChunkData(chunkData);

        // 按区块加载方块数据
        RecordKey key = new RecordKey(DataScope.BLOCK_RECORD);
        key.addField(FieldKey.LOCATION);
        key.addField(FieldKey.SLIMEFUN_ID);
        key.addCondition(FieldKey.CHUNK, chunkData.getKey());

        getData(key).forEach(block -> {
            String lKey = block.get(FieldKey.LOCATION);
            String sfId = block.get(FieldKey.SLIMEFUN_ID);
            SlimefunItem sfItem = SlimefunItem.getById(sfId);
            if (sfItem == null) {
                return;
            }

            SlimefunBlockData cache = getBlockDataFromCache(chunkData.getKey(), lKey);
            SlimefunBlockData blockData = cache == null ? new SlimefunBlockData(LocationUtils.toLocation(lKey), sfId) : cache;
            chunkData.addBlockCacheInternal(blockData, false);

            if (sfItem.loadDataByDefault()) {
                scheduleReadTask(() -> loadBlockData(blockData));
            }
        });

        Bukkit.getPluginManager().callEvent(new SlimefunChunkDataLoadEvent(chunkData));
    }

    public void loadWorld(World world) {
        long start = System.currentTimeMillis();
        String worldName = world.getName();
        logger.log(Level.INFO, "正在加载世界 {0} 的 Slimefun 方块数据...", worldName);
        HashSet<String> chunkKeys = new HashSet<>();
        RecordKey key = new RecordKey(DataScope.CHUNK_DATA);
        key.addField(FieldKey.CHUNK);
        key.addCondition(FieldKey.CHUNK, worldName + ";%");
        getData(key, true).forEach(data -> chunkKeys.add(data.get(FieldKey.CHUNK)));

        key = new RecordKey(DataScope.BLOCK_RECORD);
        key.addField(FieldKey.CHUNK);
        key.addCondition(FieldKey.CHUNK, world.getName() + ";%");
        getData(key, true).forEach(data -> chunkKeys.add(data.get(FieldKey.CHUNK)));

        chunkKeys.forEach(cKey -> loadChunk(LocationUtils.toChunk(world, cKey), false));
        logger.log(
                Level.INFO, "世界 {0} 数据加载完成, 耗时 {1}ms", new Object[]{worldName, (System.currentTimeMillis() - start)});
    }

    public void loadUniversalRecord() {
        RecordKey uniKey = new RecordKey(DataScope.UNIVERSAL_RECORD);
        uniKey.addField(FieldKey.UNIVERSAL_UUID);
        uniKey.addField(FieldKey.SLIMEFUN_ID);
        uniKey.addField(FieldKey.UNIVERSAL_TRAITS);

        getData(uniKey).forEach(data -> {
            String sfId = data.get(FieldKey.SLIMEFUN_ID);
            SlimefunItem sfItem = SlimefunItem.getById(sfId);

            if (sfItem == null) {
                return;
            }

            UUID uuid = data.getUUID(FieldKey.UNIVERSAL_UUID);
            String traitsData = data.get(FieldKey.UNIVERSAL_TRAITS);
            Set<UniversalDataTrait> traits = EnumSet.noneOf(UniversalDataTrait.class);

            // Read trait(s) of universal data
            if (traitsData != null && !traitsData.isBlank()) {
                for (String traitStr : traitsData.split(",")) {
                    try {
                        traits.add(UniversalDataTrait.valueOf(traitStr.toUpperCase()));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }

            SlimefunUniversalData uniData = traits.contains(UniversalDataTrait.BLOCK)
                    ? new SlimefunUniversalBlockData(uuid, sfId)
                    : new SlimefunUniversalData(uuid, sfId);

            traits.forEach(uniData::addTrait);

            scheduleReadTask(() -> loadUniversalData(uniData));
        });
    }

    private void loadChunkData(SlimefunChunkData chunkData) {
        if (chunkData.isDataLoaded()) {
            return;
        }
        RecordKey key = new RecordKey(DataScope.CHUNK_DATA);
        key.addField(FieldKey.DATA_KEY);
        key.addField(FieldKey.DATA_VALUE);
        key.addCondition(FieldKey.CHUNK, chunkData.getKey());

        lock.lock(key);
        try {
            if (chunkData.isDataLoaded()) {
                return;
            }
            getData(key)
                    .forEach(data -> chunkData.setCacheInternal(
                            data.get(FieldKey.DATA_KEY),
                            DataUtils.blockDataDebase64(data.get(FieldKey.DATA_VALUE)),
                            false));
            chunkData.setIsDataLoaded(true);
        } finally {
            lock.unlock(key);
        }
    }

    public void loadBlockData(SlimefunBlockData blockData) {
        if (blockData.isDataLoaded()) {
            return;
        }
        RecordKey key = new RecordKey(DataScope.BLOCK_DATA);
        key.addCondition(FieldKey.LOCATION, blockData.getKey());
        key.addField(FieldKey.DATA_KEY);
        key.addField(FieldKey.DATA_VALUE);

        lock.lock(key);
        try {
            if (blockData.isDataLoaded()) {
                return;
            }

            SlimefunItem sfItem = SlimefunItem.getById(blockData.getSfId());
            boolean universal = sfItem instanceof UniversalBlock;

            List<RecordSet> kvData = getData(key);

            RecordKey menuKey = new RecordKey(DataScope.BLOCK_INVENTORY);
            menuKey.addCondition(FieldKey.LOCATION, blockData.getKey());
            menuKey.addField(FieldKey.INVENTORY_SLOT);
            menuKey.addField(FieldKey.INVENTORY_ITEM);

            List<RecordSet> invData = getData(menuKey);

            if (universal) {
                migrateUniversalData(blockData.getLocation(), blockData.getSfId(), kvData, invData);
            } else {
                kvData.forEach(recordSet -> blockData.setCacheInternal(
                        recordSet.get(FieldKey.DATA_KEY),
                        DataUtils.blockDataDebase64(recordSet.get(FieldKey.DATA_VALUE)),
                        false));

                blockData.setIsDataLoaded(true);

                BlockMenuPreset menuPreset = BlockMenuPreset.getPreset(blockData.getSfId());

                if (menuPreset != null) {
                    ItemStack[] inv = new ItemStack[54];

                    invData.forEach(record ->
                            inv[record.getInt(FieldKey.INVENTORY_SLOT)] = record.getItemStack(FieldKey.INVENTORY_ITEM));

                    blockData.setBlockMenu(new BlockMenu(menuPreset, blockData.getLocation(), inv));

                    ItemStack[] content = blockData.getMenuContents();
                    if (content != null) {
                        invSnapshots.put(blockData.getKey(), InvStorageUtils.getInvSnapshot(content));
                    }
                }
            }

            if (sfItem != null && sfItem.isTicking()) {
                if ("CARGO_MANAGER".equalsIgnoreCase(sfItem.getId())) {
                    Slimefun.instance().getCargoTickerTask().enableTicker(blockData.getLocation());
                } else {
                    Slimefun.getTickerTask().enableTicker(blockData.getLocation());
                }
            }
        } finally {
            lock.unlock(key);
        }
    }

    public void loadBlockDataAsync(SlimefunBlockData blockData, IAsyncReadCallback<SlimefunBlockData> callback) {
        scheduleReadTask(() -> {
            loadBlockData(blockData);
            invokeCallback(callback, blockData);
        });
    }

    public void loadBlockDataAsync(
            List<SlimefunBlockData> blockDataList, IAsyncReadCallback<List<SlimefunBlockData>> callback) {
        scheduleReadTask(() -> blockDataList.forEach(this::loadBlockData));
        invokeCallback(callback, blockDataList);
    }

    public void loadUniversalData(SlimefunUniversalData uniData) {
        if (uniData.isDataLoaded()) {
            return;
        }

        RecordKey key = new RecordKey(DataScope.UNIVERSAL_DATA);
        key.addCondition(FieldKey.UNIVERSAL_UUID, uniData.getKey());
        key.addField(FieldKey.DATA_KEY);
        key.addField(FieldKey.DATA_VALUE);

        lock.lock(key);

        try {
            if (uniData.isDataLoaded()) {
                return;
            }

            getData(key)
                    .forEach(recordSet -> uniData.setCacheInternal(
                            recordSet.get(FieldKey.DATA_KEY),
                            DataUtils.blockDataDebase64(recordSet.get(FieldKey.DATA_VALUE)),
                            false));

            loadedUniversalData.putIfAbsent(uniData.getUUID(), uniData);

            uniData.setIsDataLoaded(true);

            if (uniData.hasTrait(UniversalDataTrait.INVENTORY)) {
                UniversalMenuPreset menuPreset = UniversalMenuPreset.getPreset(uniData.getSfId());
                if (menuPreset != null) {
                    RecordKey menuKey = new RecordKey(DataScope.UNIVERSAL_INVENTORY);
                    menuKey.addCondition(FieldKey.UNIVERSAL_UUID, uniData.getKey());
                    menuKey.addField(FieldKey.INVENTORY_SLOT);
                    menuKey.addField(FieldKey.INVENTORY_ITEM);

                    ItemStack[] inv = new ItemStack[54];

                    getData(menuKey)
                            .forEach(recordSet -> inv[recordSet.getInt(FieldKey.INVENTORY_SLOT)] =
                                    recordSet.getItemStack(FieldKey.INVENTORY_ITEM));

                    Location location = uniData.hasTrait(UniversalDataTrait.BLOCK)
                            ? ((SlimefunUniversalBlockData) uniData)
                            .getLastPresent()
                            .toLocation()
                            : null;

                    uniData.setMenu(new UniversalMenu(menuPreset, uniData.getUUID(), location, inv));

                    ItemStack[] content = uniData.getMenuContents();
                    if (content != null) {
                        invSnapshots.put(uniData.getKey(), InvStorageUtils.getInvSnapshot(content));
                    }
                }
            }

            if (uniData.hasTrait(UniversalDataTrait.BLOCK)) {
                SlimefunItem sfItem = SlimefunItem.getById(uniData.getSfId());

                if (sfItem != null && sfItem.isTicking()) {
                    Slimefun.getTickerTask()
                            .enableTicker(
                                    ((SlimefunUniversalBlockData) uniData)
                                            .getLastPresent()
                                            .toLocation(),
                                    uniData.getUUID());
                }
            }
        } finally {
            lock.unlock(key);
        }
    }

    public void loadUniversalDataAsync(
            SlimefunUniversalData uniData, IAsyncReadCallback<SlimefunUniversalData> callback) {
        scheduleReadTask(() -> {
            loadUniversalData(uniData);
            invokeCallback(callback, uniData);
        });
    }

    public SlimefunChunkData getChunkData(Chunk chunk) {
        loadChunk(chunk, false);
        return getChunkDataCache(chunk, false);
    }

    public void getChunkDataAsync(Chunk chunk, IAsyncReadCallback<SlimefunChunkData> callback) {
        scheduleReadTask(() -> invokeCallback(callback, getChunkData(chunk)));
    }

    public void saveAllBlockInventories() {
        Set<SlimefunChunkData> chunks = new HashSet<>(loadedChunk.values());
        chunks.forEach(chunk -> chunk.getAllCacheInternal().forEach(block -> {
            if (block.isPendingRemove() || !block.isDataLoaded()) {
                return;
            }
            BlockMenu menu = block.getBlockMenu();
            if (menu == null || menu.isNoDirty()) {
                return;
            }

            saveBlockInventory(block);
        }));
    }

    public void saveAllUniversalInventories() {
        Set<SlimefunUniversalData> uniData = new HashSet<>(loadedUniversalData.values());
        uniData.forEach(data -> {
            if (data.isPendingRemove() || !data.isDataLoaded()) {
                return;
            }
            UniversalMenu menu = data.getMenu();
            if (menu == null || !menu.isDirty()) {
                return;
            }

            saveUniversalInventory(data);
        });
    }

    public void saveBlockInventory(SlimefunBlockData blockData) {
        ItemStack[] newInv = blockData.getMenuContents();
        List<Pair<ItemStack, Integer>> lastSave;
        if (newInv == null) {
            lastSave = invSnapshots.remove(blockData.getKey());
            if (lastSave == null) {
                return;
            }
        } else {
            lastSave = invSnapshots.put(blockData.getKey(), InvStorageUtils.getInvSnapshot(newInv));
        }

        Set<Integer> changed = InvStorageUtils.getChangedSlots(lastSave, newInv);
        if (changed.isEmpty()) {
            return;
        }

        changed.forEach(slot -> saveBlockInventorySlot(blockData, slot));
    }

    public void saveBlockInventorySlot(SlimefunBlockData blockData, int slot) {
        scheduleDelayedBlockInvUpdate(blockData, slot);
    }

    public Set<SlimefunChunkData> getAllLoadedChunkData() {
        return new HashSet<>(loadedChunk.values());
    }

    public void removeAllDataInChunk(Chunk chunk) {
        String cKey = LocationUtils.getChunkKey(chunk);
        SlimefunChunkData cache = loadedChunk.remove(cKey);

        if (cache != null && cache.isDataLoaded()) {
            cache.getAllBlockData().forEach(this::clearBlockCacheAndTasks);
        }
        deleteChunkAndBlockDataDirectly(cKey);
    }

    public void removeAllDataInChunkAsync(Chunk chunk, Runnable onFinishedCallback) {
        scheduleWriteTask(() -> {
            removeAllDataInChunk(chunk);
            onFinishedCallback.run();
        });
    }

    public void removeAllDataInWorld(World world) {
        // 1. remove block cache
        HashSet<SlimefunBlockData> loadedBlockData = new HashSet<>();
        for (SlimefunChunkData chunkData : getAllLoadedChunkData(world)) {
            loadedBlockData.addAll(chunkData.getAllBlockData());
            chunkData.removeAllCacheInternal();
        }

        // 2. remove ticker and delayed tasks
        loadedBlockData.forEach(this::clearBlockCacheAndTasks);

        // 3. remove from database
        String prefix = world.getName() + ";";
        deleteChunkAndBlockDataDirectly(prefix + "%");

        // 4. remove chunk cache
        loadedChunk.entrySet().removeIf(entry -> entry.getKey().startsWith(prefix));
    }

    public void removeAllDataInWorldAsync(World world, Runnable onFinishedCallback) {
        scheduleWriteTask(() -> {
            removeAllDataInWorld(world);
            onFinishedCallback.run();
        });
    }

    public void saveUniversalInventory(SlimefunUniversalData universalData) {
        UniversalMenu menu = universalData.getMenu();
        UUID universalID = universalData.getUUID();

        ItemStack[] newInv = menu.getContents();
        List<Pair<ItemStack, Integer>> lastSave;
        if (newInv == null) {
            lastSave = invSnapshots.remove(universalID.toString());
            if (lastSave == null) {
                return;
            }
        } else {
            lastSave = invSnapshots.put(universalID.toString(), InvStorageUtils.getInvSnapshot(newInv));
        }

        Set<Integer> changed = InvStorageUtils.getChangedSlots(lastSave, newInv);
        if (changed.isEmpty()) {
            return;
        }

        changed.forEach(slot -> scheduleDelayedUniversalInvUpdate(universalID, menu, slot));
    }

    public Set<SlimefunChunkData> getAllLoadedChunkData(World world) {
        String prefix = world.getName() + ";";
        HashSet<SlimefunChunkData> re = new HashSet<>();
        loadedChunk.forEach((k, v) -> {
            if (k.startsWith(prefix)) {
                re.add(v);
            }
        });
        return re;
    }

    public void removeFromAllChunkInWorld(World world, String key) {
        RecordKey req = new RecordKey(DataScope.CHUNK_DATA);
        req.addCondition(FieldKey.CHUNK, world.getName() + ";%");
        req.addCondition(FieldKey.DATA_KEY, key);
        deleteData(req);
        getAllLoadedChunkData(world).forEach(data -> data.removeData(key));
    }

    public void removeFromAllChunkInWorldAsync(World world, String key, Runnable onFinishedCallback) {
        scheduleWriteTask(() -> {
            removeFromAllChunkInWorld(world, key);
            onFinishedCallback.run();
        });
    }

    private void scheduleDelayedBlockInvUpdate(SlimefunBlockData blockData, int slot) {
        LocationKey scopeKey = new LocationKey(DataScope.NONE, blockData.getLocation());
        RecordKey reqKey = new RecordKey(DataScope.BLOCK_INVENTORY);
        reqKey.addCondition(FieldKey.LOCATION, blockData.getKey());
        reqKey.addCondition(FieldKey.INVENTORY_SLOT, slot + "");
        reqKey.addField(FieldKey.INVENTORY_ITEM);

        if (enableDelayedSaving) {
            scheduleDelayedUpdateTask(
                    new LinkedKey(scopeKey, reqKey),
                    () -> scheduleBlockInvUpdate(
                            scopeKey, reqKey, blockData.getKey(), blockData.getMenuContents(), slot));
        } else {
            scheduleBlockInvUpdate(scopeKey, reqKey, blockData.getKey(), blockData.getMenuContents(), slot);
        }
    }

    private void scheduleBlockInvUpdate(ScopeKey scopeKey, RecordKey reqKey, String lKey, ItemStack[] inv, int slot) {
        ItemStack item = inv != null && slot < inv.length ? inv[slot] : null;

        if (item == null) {
            scheduleDeleteTask(scopeKey, reqKey, true);
        } else {
            RecordSet data = new RecordSet();
            data.put(FieldKey.LOCATION, lKey);
            data.put(FieldKey.INVENTORY_SLOT, slot + "");
            data.put(FieldKey.INVENTORY_ITEM, item);
            scheduleWriteTask(scopeKey, reqKey, data, true);
        }
    }

    /**
     * Save universal inventory by async way
     *
     * @param uuid Universal Inventory UUID
     * @param menu Universal menu
     * @param slot updated item slot
     */
    private void scheduleDelayedUniversalInvUpdate(UUID uuid, UniversalMenu menu, int slot) {
        UUIDKey scopeKey = new UUIDKey(DataScope.NONE, uuid);
        RecordKey reqKey = new RecordKey(DataScope.UNIVERSAL_INVENTORY);
        reqKey.addCondition(FieldKey.UNIVERSAL_UUID, uuid.toString());
        reqKey.addCondition(FieldKey.INVENTORY_SLOT, slot + "");
        reqKey.addField(FieldKey.INVENTORY_ITEM);

        if (enableDelayedSaving) {
            scheduleDelayedUpdateTask(
                    new LinkedKey(scopeKey, reqKey),
                    () -> scheduleUniversalInvUpdate(scopeKey, reqKey, uuid, menu.getContents(), slot));
        } else {
            scheduleUniversalInvUpdate(scopeKey, reqKey, uuid, menu.getContents(), slot);
        }
    }

    private void scheduleUniversalInvUpdate(ScopeKey scopeKey, RecordKey reqKey, UUID uuid, ItemStack[] inv, int slot) {
        ItemStack item = inv != null && slot < inv.length ? inv[slot] : null;

        if (item == null) {
            scheduleDeleteTask(scopeKey, reqKey, true);
        } else {
            RecordSet data = new RecordSet();
            data.put(FieldKey.UNIVERSAL_UUID, uuid.toString());
            data.put(FieldKey.INVENTORY_SLOT, slot + "");
            data.put(FieldKey.INVENTORY_ITEM, item);
            scheduleWriteTask(scopeKey, reqKey, data, true);
        }
    }

    @Override
    public void shutdown() {
        saveAllBlockInventories();
        saveAllUniversalInventories();
        if (enableDelayedSaving) {
            looperTask.cancel();
            executeAllDelayedTasks();
        }
        super.shutdown();
    }

    void scheduleDelayedBlockDataUpdate(SlimefunBlockData blockData, String key) {
        LocationKey scopeKey = new LocationKey(DataScope.NONE, blockData.getLocation());
        RecordKey reqKey = new RecordKey(DataScope.BLOCK_DATA);
        reqKey.addCondition(FieldKey.LOCATION, blockData.getKey());
        reqKey.addCondition(FieldKey.DATA_KEY, key);
        if (enableDelayedSaving) {
            scheduleDelayedUpdateTask(
                    new LinkedKey(scopeKey, reqKey),
                    () -> scheduleBlockDataUpdate(scopeKey, reqKey, blockData.getKey(), key, blockData.getData(key)));
        } else {
            scheduleBlockDataUpdate(scopeKey, reqKey, blockData.getKey(), key, blockData.getData(key));
        }
    }

    void scheduleDelayedUniversalDataUpdate(SlimefunUniversalData universalData, String key) {
        UUIDKey scopeKey = new UUIDKey(DataScope.NONE, universalData.getKey());
        RecordKey reqKey = new RecordKey(DataScope.UNIVERSAL_DATA);
        reqKey.addCondition(FieldKey.UNIVERSAL_UUID, universalData.getKey());
        reqKey.addCondition(FieldKey.DATA_KEY, key);
        if (enableDelayedSaving) {
            scheduleDelayedUpdateTask(
                    new LinkedKey(scopeKey, reqKey),
                    () -> scheduleUniversalDataUpdate(
                            scopeKey, reqKey, universalData.getKey(), key, universalData.getData(key)));
        } else {
            scheduleUniversalDataUpdate(scopeKey, reqKey, universalData.getKey(), key, universalData.getData(key));
        }
    }

    private void removeDelayedBlockDataUpdates(ScopeKey scopeKey) {
        synchronized (delayedWriteTasks) {
            delayedWriteTasks
                    .entrySet()
                    .removeIf(each -> scopeKey.equals(each.getKey().getParent()));
        }
    }

    private void scheduleBlockDataUpdate(ScopeKey scopeKey, RecordKey reqKey, String lKey, String key, String val) {
        if (val == null) {
            scheduleDeleteTask(scopeKey, reqKey, false);
        } else {
            RecordSet data = new RecordSet();
            reqKey.addField(FieldKey.DATA_VALUE);
            data.put(FieldKey.LOCATION, lKey);
            data.put(FieldKey.DATA_KEY, key);
            data.put(FieldKey.DATA_VALUE, DataUtils.blockDataBase64(val));
            scheduleWriteTask(scopeKey, reqKey, data, true);
        }
    }

    private void scheduleUniversalDataUpdate(ScopeKey scopeKey, RecordKey reqKey, String uuid, String key, String val) {
        if (val == null) {
            scheduleDeleteTask(scopeKey, reqKey, false);
        } else {
            RecordSet data = new RecordSet();
            reqKey.addField(FieldKey.DATA_VALUE);
            data.put(FieldKey.UNIVERSAL_UUID, uuid);
            data.put(FieldKey.DATA_KEY, key);
            data.put(FieldKey.DATA_VALUE, DataUtils.blockDataBase64(val));
            scheduleWriteTask(scopeKey, reqKey, data, true);
        }
    }

    void scheduleDelayedChunkDataUpdate(SlimefunChunkData chunkData, String key) {
        ChunkKey scopeKey = new ChunkKey(DataScope.NONE, chunkData.getChunk());
        RecordKey reqKey = new RecordKey(DataScope.CHUNK_DATA);
        reqKey.addCondition(FieldKey.CHUNK, chunkData.getKey());
        reqKey.addCondition(FieldKey.DATA_KEY, key);

        if (enableDelayedSaving) {
            scheduleDelayedUpdateTask(
                    new LinkedKey(scopeKey, reqKey),
                    () -> scheduleChunkDataUpdate(scopeKey, reqKey, chunkData.getKey(), key, chunkData.getData(key)));
        } else {
            scheduleChunkDataUpdate(scopeKey, reqKey, chunkData.getKey(), key, chunkData.getData(key));
        }
    }

    private void scheduleDelayedUpdateTask(LinkedKey key, Runnable run) {
        synchronized (delayedWriteTasks) {
            DelayedTask task = delayedWriteTasks.get(key);
            if (task != null && !task.isExecuted()) {
                task.setRunAfter(delayedSecond, TimeUnit.SECONDS);
                return;
            }

            task = new DelayedTask(delayedSecond, TimeUnit.SECONDS, run);
            delayedWriteTasks.put(key, task);
        }
    }

    private void scheduleChunkDataUpdate(ScopeKey scopeKey, RecordKey reqKey, String cKey, String key, String val) {
        if (val == null) {
            scheduleDeleteTask(scopeKey, reqKey, false);
        } else {
            RecordSet data = new RecordSet();
            reqKey.addField(FieldKey.DATA_VALUE);
            data.put(FieldKey.CHUNK, cKey);
            data.put(FieldKey.DATA_KEY, key);
            data.put(FieldKey.DATA_VALUE, DataUtils.blockDataBase64(val));
            scheduleWriteTask(scopeKey, reqKey, data, false);
        }
    }

    private void executeAllDelayedTasks() {
        synchronized (delayedWriteTasks) {
            delayedWriteTasks.values().forEach(DelayedTask::runUnsafely);
        }
    }

    private SlimefunChunkData getChunkDataCache(Chunk chunk, boolean createOnNotExists) {
        return createOnNotExists
                ? loadedChunk.computeIfAbsent(LocationUtils.getChunkKey(chunk), k -> {
            SlimefunChunkData re = new SlimefunChunkData(chunk);
            if (!initLoading && chunkDataLoadMode.readCacheOnly()) {
                re.setIsDataLoaded(true);
            }
            return re;
        })
                : loadedChunk.get(LocationUtils.getChunkKey(chunk));
    }

    // fix issue 935: auto chunk load when using loc.getChunk(),if chunk data is already loaded into cache, we generate
    // keyString using location,instead of loc.getChunk
    private SlimefunChunkData getChunkDataCache(Location loc, boolean createOnNotExists) {
        SlimefunChunkData re = loadedChunk.get(LocationUtils.getChunkKey(loc));
        if (re != null) {
            return re;
        } else {
            // jump to origin getChunkDataCache and call getChunk() to trigger chunkLoad
            return getChunkDataCache(loc.getChunk(), createOnNotExists);
        }
    }

    private void deleteChunkAndBlockDataDirectly(String cKey) {
        RecordKey req = new RecordKey(DataScope.BLOCK_RECORD);
        req.addCondition(FieldKey.CHUNK, cKey);
        deleteData(req);

        req = new RecordKey(DataScope.CHUNK_DATA);
        req.addCondition(FieldKey.CHUNK, cKey);
        deleteData(req);
    }

    private void clearBlockCacheAndTasks(SlimefunBlockData blockData) {
        Location l = blockData.getLocation();
        if (blockData.isDataLoaded() && Slimefun.getRegistry().getTickerBlocks().contains(blockData.getSfId())) {
            if ("CARGO_MANAGER".equalsIgnoreCase(blockData.getSfId())) {
                Slimefun.instance().getCargoTickerTask().disableTicker(blockData.getLocation());
            } else {
                Slimefun.getTickerTask().disableTicker(blockData.getLocation());
            }
        }
        Slimefun.getNetworkManager().updateAllNetworks(l);

        LocationKey scopeKey = new LocationKey(DataScope.NONE, l);
        removeDelayedBlockDataUpdates(scopeKey);
        abortScopeTask(scopeKey);
    }

    /**
     * 迁移旧 Slimefun 机器数据至通用数据
     */
    private void migrateUniversalData(
            Location l,
            String sfId,
            List<RecordSet> kvData,
            List<RecordSet> invData) {
        try {
            if (l == null || sfId == null) {
                return;
            }

            SlimefunUniversalBlockData universalData = createUniversalBlock(l, sfId);

            Slimefun.runSync(
                    () -> {
                        if (BlockDataService
                                .isTileEntity(l.getBlock().getType())) {
                            Slimefun.getBlockDataService()
                                    .updateUniversalDataUUID(l.getBlock(), String.valueOf(universalData.getUUID()));
                        }
                    },
                    10L);

            kvData.forEach(recordSet -> universalData.setData(
                    recordSet.get(FieldKey.DATA_KEY), DataUtils.blockDataDebase64(recordSet.get(FieldKey.DATA_VALUE))));

            UniversalMenuPreset preset = UniversalMenuPreset.getPreset(sfId);
            if (preset != null) {
                ItemStack[] inv = new ItemStack[54];

                invData.forEach(record ->
                        inv[record.getInt(FieldKey.INVENTORY_SLOT)] = record.getItemStack(FieldKey.INVENTORY_ITEM));

                universalData.setMenu(new UniversalMenu(preset, universalData.getUUID(), l, inv));

                ItemStack[] content = universalData.getMenuContents();
                if (content != null) {
                    invSnapshots.put(universalData.getKey(), InvStorageUtils.getInvSnapshot(content));
                }
            }

            removeBlockData(l);

            if (Slimefun.getRegistry().getTickerBlocks().contains(universalData.getSfId())) {
                Slimefun.getTickerTask()
                        .enableTicker(universalData.getLastPresent().toLocation(), universalData.getUUID());
            }
        } catch (Exception e) {
            Slimefun.logger().log(Level.WARNING, "迁移机器人数据时出现错误", e);
        }
    }
}
