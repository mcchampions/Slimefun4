package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ASlimefunDataContainer;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.bakedlibs.dough.blocks.ChunkPosition;
import io.github.thebusybiscuit.slimefun4.api.ErrorReport;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.ticker.TickLocation;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.qscbm.slimefun4.tasks.BaseTickerTask;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * The {@link TickerTask} is responsible for ticking every {@link BlockTicker},
 * synchronous or not.
 *
 * @author TheBusyBiscuit
 * @see BlockTicker
 */
public class TickerTask extends BaseTickerTask {
    /**
     * This Map holds all currently actively ticking locations.
     * The value of this map (Set entries) MUST be thread-safe and mutable.
     */
    private final Map<ChunkPosition, Set<TickLocation>> tickingLocations = new ConcurrentHashMap<>();

    /**
     * This Map tracks how many bugs have occurred in a given Location .
     * If too many bugs happen, we delete that Location.
     */
    private final Map<BlockPosition, Integer> bugs = new ConcurrentHashMap<>();

    /**
     * -- GETTER --
     * This returns the delay between ticks
     */
    @Getter
    private int tickRate;
    @Getter
    private boolean halted;
    private boolean running;

    @Setter
    @Getter
    private volatile boolean paused;

    /**
     * This method starts the {@link TickerTask} on an asynchronous schedule.
     *
     * @param plugin The instance of our {@link Slimefun}
     */
    @Override
    public void start(Slimefun plugin) {
        this.tickRate = Slimefun.getCfg().getInt("URID.custom-ticker-delay");

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, this, 100L, tickRate);
    }

    /**
     * This method resets this {@link TickerTask} to run again.
     */
    private void reset() {
        running = false;
    }

    @Override
    public void run() {
        if (paused) {
            return;
        }

        try {
            // If this method is actually still running... DON'T
            if (running) {
                return;
            }

            running = true;
            Set<BlockTicker> tickers = new HashSet<>();

            // Run our ticker code
            if (!halted) {
                for (Map.Entry<ChunkPosition, Set<TickLocation>> entry : tickingLocations.entrySet()) {
                    tickChunk(entry.getKey(), tickers, entry.getValue());
                }
            }

            // Start a new tick cycle for every BlockTicker
            for (BlockTicker ticker : tickers) {
                ticker.startNewTick();
            }

            reset();
        } catch (RuntimeException | LinkageError x) {
            Slimefun.logger()
                    .log(
                            Level.SEVERE,
                            x,
                            () -> "An Exception was caught while ticking the Block Tickers Task for Slimefun v"
                                  + Slimefun.getVersion());
            reset();
        }
    }

    private void tickChunk(ChunkPosition chunk, Set<BlockTicker> tickers, Set<TickLocation> locations) {
        try {
            // Only continue if the Chunk is actually loaded
            if (chunk.isLoaded()) {
                for (TickLocation l : locations) {
                    if (l.isUniversal()) {
                        tickUniversalLocation(l.getUuid(), l.getLocation(), tickers);
                    } else {
                        tickLocation(tickers, l.getLocation());
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException x) {
            Slimefun.logger()
                    .log(Level.SEVERE, x, () -> "An Exception has occurred while trying to resolve Chunk: " + chunk);
        }
    }

    private void tickLocation(Set<BlockTicker> tickers, Location l) {
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(l);
        if (blockData == null || !blockData.isDataLoaded() || blockData.isPendingRemove()) {
            return;
        }

        SlimefunItem item = SlimefunItem.getById(blockData.getSfId());
        if (item != null && item.getBlockTicker() != null) {
            if (item.isDisabledIn(l.getWorld())) {
                return;
            }

            try {
                if (item.getBlockTicker().isSynchronized()) {
                    item.getBlockTicker().update();

                    /*
                     * We are inserting a new timestamp because synchronized actions
                     * are always ran with a 50ms delay (1 game tick)
                     */
                    Slimefun.runSync(() -> {
                        if (blockData.isPendingRemove()) {
                            return;
                        }
                        tickBlock(l, item, blockData);
                    });
                } else {
                    item.getBlockTicker().update();
                    tickBlock(l, item, blockData);
                }

                tickers.add(item.getBlockTicker());
            } catch (RuntimeException x) {
                reportErrors(l, item, x);
            }
            return;
        }
        disableTicker(l);
    }

    private void tickUniversalLocation(UUID uuid, Location l, Set<BlockTicker> tickers) {
        SlimefunUniversalBlockData data = StorageCacheUtils.getUniversalBlock(uuid);
        SlimefunItem item = SlimefunItem.getById(data.getSfId());

        if (item != null && item.getBlockTicker() != null) {
            if (item.isDisabledIn(l.getWorld())) {
                return;
            }

            try {
                if (item.getBlockTicker().isSynchronized()) {
                    item.getBlockTicker().update();

                    /*
                      We are inserting a new timestamp because synchronized actions
                      are always ran with a 50ms delay (1 game tick)
                     */
                    Slimefun.runSync(() -> {
                        if (data.isPendingRemove()) {
                            return;
                        }
                        tickBlock(l, item, data);
                    });
                } else {
                    item.getBlockTicker().update();
                    tickBlock(l, item, data);
                }

                tickers.add(item.getBlockTicker());
            } catch (Exception x) {
                reportErrors(l, item, x);
            }
        }
    }

    private void tickBlock(Location l, SlimefunItem item, ASlimefunDataContainer data) {
        try {
            if (item.getBlockTicker().isUniversal()) {
                if (data instanceof SlimefunUniversalData universalData) {
                    item.getBlockTicker().tick(l.getBlock(), item, universalData);
                } else {
                    throw new IllegalStateException("BlockTicker is universal but item is non-universal!");
                }
            } else {
                if (data instanceof SlimefunBlockData blockData) {
                    item.getBlockTicker().tick(l.getBlock(), item, blockData);
                } else {
                    throw new IllegalStateException("BlockTicker is non-universal but item is universal!");
                }
            }
        } catch (Exception | LinkageError x) {
            reportErrors(l, item, x);
        }
    }

    private void reportErrors(Location l, SlimefunItem item, Throwable x) {
        BlockPosition position = new BlockPosition(l);
        int errors = bugs.getOrDefault(position, 0) + 1;

        if (errors == 1) {
            // Generate a new Error-Report
            new ErrorReport<>(x, l, item);
            bugs.put(position, 1);
        } else if (errors == 4) {
            Slimefun.logger().log(Level.SEVERE, "X: {0} Y: {1} Z: {2} ({3})", new Object[]{
                    l.getBlockX(), l.getBlockY(), l.getBlockZ(), item.getId()
            });
            Slimefun.logger().log(Level.SEVERE, "在过去的 4 个 Tick 中发生多次错误，该方块对应的机器已被停用。");
            Slimefun.logger().log(Level.SEVERE, "请在 /plugins/Slimefun/error-reports/ 文件夹中查看错误详情。");
            Slimefun.logger().log(Level.SEVERE, " ");
            bugs.remove(position);

            disableTicker(l);
        } else {
            bugs.put(position, errors);
        }
    }

    @Override
    public void halt() {
        halted = true;
    }

    public Map<ChunkPosition, Set<TickLocation>> getLocations() {
        return tickingLocations;
    }

    /**
     * This method returns a <strong>read-only</strong> {@link Map}
     * representation of every {@link ChunkPosition} and its corresponding
     * {@link Set} of ticking {@link Location Locations}.
     * <p>
     * This does include any {@link Location} from an unloaded {@link Chunk} too!
     *
     * @return A {@link Map} representation of all ticking {@link TickLocation Locations}
     */
    public Map<ChunkPosition, Set<TickLocation>> getTickLocations() {
        return tickingLocations;
    }

    /**
     * This method returns a <strong>read-only</strong> {@link Set}
     * of all ticking {@link Location Locations} in a given {@link Chunk}.
     * The {@link Chunk} does not have to be loaded.
     * If no {@link Location} is present, the returned {@link Set} will be empty.
     *
     * @param chunk The {@link Chunk}
     * @return A {@link Set} of all ticking {@link Location Locations}
     */
    public Set<TickLocation> getLocations(Chunk chunk) {
        return tickingLocations.getOrDefault(new ChunkPosition(chunk), new HashSet<>());
    }

    /**
     * This enables the ticker at the given {@link Location} and adds it to our "queue".
     *
     * @param l The {@link Location} to activate
     */
    public void enableTicker(Location l) {
        enableTicker(l, null);
    }

    public void enableTicker(Location l, UUID uuid) {
        ChunkPosition chunk = new ChunkPosition(l.getWorld(), l.getBlockX() >> 4, l.getBlockZ() >> 4);
        final TickLocation tickPosition = uuid == null
                ? new TickLocation(new BlockPosition(l))
                : new TickLocation(new BlockPosition(l), uuid);

            /*
              Note that all the values in #tickingLocations must be thread-safe.
              Thus, the choice is between the CHM KeySet or a synchronized set.
              The CHM KeySet was chosen since it at least permits multiple concurrent
              reads without blocking.
            */
        Set<TickLocation> newValue = ConcurrentHashMap.newKeySet();
        Set<TickLocation> oldValue = tickingLocations.putIfAbsent(chunk, newValue);

        //noinspection ReplaceNullCheck
        if (oldValue != null) {
            oldValue.add(tickPosition);
        } else {
            newValue.add(tickPosition);
        }
    }

    /**
     * This method disables the ticker at the given {@link Location} and removes it from our internal
     * "queue".
     *
     * @param l The {@link Location} to remove
     */
    @Override
    public void disableTicker(Location l) {
        ChunkPosition chunk = new ChunkPosition(l.getWorld(), l.getBlockX() >> 4, l.getBlockZ() >> 4);
        Set<TickLocation> locations = tickingLocations.get(chunk);

        if (locations != null) {
            locations.removeIf(tk -> l.equals(tk.getLocation()));

            if (locations.isEmpty()) {
                tickingLocations.remove(chunk);
            }
        }
    }

    /**
     * This method disables the ticker at the given {@link UUID} and removes it from our internal
     * "queue".
     * <p>
     * DO NOT USE THIS until you cannot disable by location,
     * or enjoy extremely slow.
     *
     * @param uuid The {@link UUID} to remove
     */
    public void disableTicker(UUID uuid) {
        tickingLocations.values().forEach(loc -> loc.removeIf(tk -> uuid.equals(tk.getUuid())));
    }
}
