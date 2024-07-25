package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.bakedlibs.dough.blocks.ChunkPosition;
import io.github.thebusybiscuit.slimefun4.api.ErrorReport;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * The {@link TickerTask} is responsible for ticking every {@link BlockTicker},
 * synchronous or not.
 *
 * @author TheBusyBiscuit
 * @see BlockTicker
 */
public class TickerTask implements Runnable {
    /**
     * This Map holds all currently actively ticking locations.
     */
    private final Map<String, Map<ChunkPosition, Set<Location>>> tickingLocations = new ConcurrentHashMap<>();

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
    private boolean halted = false;
    private boolean running = false;

    @Setter
    private volatile boolean paused = false;

    /**
     * This method starts the {@link TickerTask} on an asynchronous schedule.
     *
     * @param plugin The instance of our {@link Slimefun}
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

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 8, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(3), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

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
                Set<Map.Entry<String, Map<ChunkPosition, Set<Location>>>> set = tickingLocations.entrySet();
                CompletableFuture<?>[] cfArr = set.stream().map(entry -> CompletableFuture.runAsync(() -> {
                    for (Map.Entry<ChunkPosition, Set<Location>> e : entry.getValue().entrySet()) {
                        tickChunk(e.getKey(), tickers, e.getValue());
                    }
                }, executor)).toArray(CompletableFuture[]::new);
                CompletableFuture.allOf(cfArr).join();
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
            return;
        }
        disableTicker(l);
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

    public void halt() {
        halted = true;
    }

    public Map<ChunkPosition, Set<Location>> getLocations() {
        Map<ChunkPosition, Set<Location>> map = new HashMap<>();
        Set<Map.Entry<String, Map<ChunkPosition, Set<Location>>>> set = tickingLocations.entrySet();
        for (Map.Entry<String, Map<ChunkPosition, Set<Location>>> entry : set) {
            map.putAll(entry.getValue());
        }
        return map;
    }

    public Set<Location> getLocations(Chunk chunk) {
        return tickingLocations.get(chunk.getWorld().getName()).getOrDefault(
                new ChunkPosition(chunk), new HashSet<>());
    }

    /**
     * This enables the ticker at the given {@link Location} and adds it to our "queue".
     *
     * @param l The {@link Location} to activate
     */
    public void enableTicker(Location l) {
        World world = l.getWorld();
        String name = world.getName();
        Map<ChunkPosition, Set<Location>> map =
                tickingLocations.computeIfAbsent(name,
                        k -> new ConcurrentHashMap<>());
        ChunkPosition cp = new ChunkPosition(world, l.getBlockX() >> 4, l.getBlockZ() >> 4);
        map.computeIfAbsent(
                        cp,
                        k -> ConcurrentHashMap.newKeySet())
                .add(l);
    }

    /**
     * This method disables the ticker at the given {@link Location} and removes it from our internal
     * "queue".
     *
     * @param l The {@link Location} to remove
     */
    public void disableTicker(Location l) {
        World world = l.getWorld();
        ChunkPosition chunk = new ChunkPosition(world, l.getBlockX() >> 4, l.getBlockZ() >> 4);
        Map<ChunkPosition, Set<Location>> map =
                tickingLocations.get(world.getName());
        if (map != null) {
            Set<Location> locations = map.get(chunk);
            if (locations != null) {
                locations.remove(l);

                if (locations.isEmpty()) {
                    map.remove(chunk);
                }
            }
        }
    }
}
