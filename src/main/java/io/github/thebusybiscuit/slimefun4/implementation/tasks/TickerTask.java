package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.bakedlibs.dough.blocks.ChunkPosition;
import io.github.thebusybiscuit.slimefun4.api.ErrorReport;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * The {@link TickerTask} is responsible for ticking every {@link BlockTicker},
 * synchronous or not.
 *
 * @author TheBusyBiscuit
 *
 * @see BlockTicker
 *
 */
public class TickerTask implements Runnable {
    /**
     * This Map holds all currently actively ticking locations.
     */
    private final Map<ChunkPosition, Set<Location>> tickingLocations = new ConcurrentHashMap<>();

    /**
     * This Map tracks how many bugs have occurred in a given Location .
     * If too many bugs happen, we delete that Location.
     */
    private final Map<BlockPosition, Integer> bugs = new ConcurrentHashMap<>();

    /**
     * -- GETTER --
     *  This returns the delay between ticks
     *
     */
    @Getter
    private int tickRate;
    @Getter
    private boolean halted = false;
    private boolean running = false;

    @Setter
    private volatile boolean paused = false;

    /**
     * This method starts the {@link TickerTask} on an asynchronous schedule.
     *
     * @param plugin
     *            The instance of our {@link Slimefun}
     */
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
                Set<Map.Entry<ChunkPosition, Set<Location>>> loc;

                synchronized (tickingLocations) {
                    loc = new HashSet<>(tickingLocations.entrySet());
                }

                for (Map.Entry<ChunkPosition, Set<Location>> entry : loc) {
                    tickChunk(entry.getKey(), tickers, new HashSet<>(entry.getValue()));
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

    private void tickChunk(ChunkPosition chunk, Set<BlockTicker> tickers, Set<Location> locations) {
        try {
            // Only continue if the Chunk is actually loaded
            if (chunk.isLoaded()) {
                for (Location l : locations) {
                    tickLocation(tickers, l);
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException x) {
            Slimefun.logger()
                    .log(Level.SEVERE, x, () -> "An Exception has occurred while trying to resolve Chunk: " + chunk);
        }
    }

    private void tickLocation(Set<BlockTicker> tickers, Location l) {
        var blockData = StorageCacheUtils.getBlock(l);
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
                        Block b = l.getBlock();
                        tickBlock(l, b, item, blockData);
                    });
                } else {
                    item.getBlockTicker().update();
                    Block b = l.getBlock();
                    tickBlock(l, b, item, blockData);
                }

                tickers.add(item.getBlockTicker());
            } catch (RuntimeException x) {
                reportErrors(l, item, x);
            }
        }
    }

    private void tickBlock(Location l, Block b, SlimefunItem item, SlimefunBlockData data) {
        try {
            item.getBlockTicker().tick(b, item, data);
        } catch (RuntimeException | LinkageError x) {
            reportErrors(l, item, x);
        }
    }

    private void reportErrors(Location l, SlimefunItem item, Throwable x) {
        BlockPosition position = new BlockPosition(l);
        int errors = bugs.getOrDefault(position, 0) + 1;

        if (errors == 1) {
            // Generate a new Error-Report
            new ErrorReport<>(x, l, item);
            bugs.put(position, errors);
        } else if (errors == 4) {
            Slimefun.logger().log(Level.SEVERE, "X: {0} Y: {1} Z: {2} ({3})", new Object[] {
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

    public void halt() {
        halted = true;
    }

    /**
     * This method returns a <strong>read-only</strong> {@link Map}
     * representation of every {@link ChunkPosition} and its corresponding
     * {@link Set} of ticking {@link Location Locations}.
     * This does include any {@link Location} from an unloaded {@link Chunk} too!
     *
     * @return A {@link Map} representation of all ticking {@link Location Locations}
     */

    public Map<ChunkPosition, Set<Location>> getLocations() {
        return Collections.unmodifiableMap(tickingLocations);
    }

    /**
     * This method returns a <strong>read-only</strong> {@link Set}
     * of all ticking {@link Location Locations} in a given {@link Chunk}.
     * The {@link Chunk} does not have to be loaded.
     * If no {@link Location} is present, the returned {@link Set} will be empty.
     *
     * @param chunk
     *            The {@link Chunk}
     *
     * @return A {@link Set} of all ticking {@link Location Locations}
     */

    public Set<Location> getLocations(Chunk chunk) {
        Set<Location> locations = tickingLocations.getOrDefault(new ChunkPosition(chunk), new HashSet<>());
        return Collections.unmodifiableSet(locations);
    }

    /**
     * This enables the ticker at the given {@link Location} and adds it to our "queue".
     *
     * @param l
     *            The {@link Location} to activate
     */
    public void enableTicker(Location l) {
        synchronized (tickingLocations) {
            tickingLocations
                    .computeIfAbsent(
                            new ChunkPosition(l.getWorld(), l.getBlockX() >> 4, l.getBlockZ() >> 4),
                            k -> new HashSet<>())
                    .add(l);
        }
    }

    /**
     * This method disables the ticker at the given {@link Location} and removes it from our internal
     * "queue".
     *
     * @param l
     *            The {@link Location} to remove
     */
    public void disableTicker(Location l) {
        synchronized (tickingLocations) {
            ChunkPosition chunk = new ChunkPosition(l.getWorld(), l.getBlockX() >> 4, l.getBlockZ() >> 4);
            Set<Location> locations = tickingLocations.get(chunk);

            if (locations != null) {
                locations.remove(l);

                if (locations.isEmpty()) {
                    tickingLocations.remove(chunk);
                }
            }
        }
    }

}
