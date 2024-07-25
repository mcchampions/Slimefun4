package me.qscbm.slimefun4.tasks;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.blocks.ChunkPosition;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.CargoManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;

public class CargoTickerTask implements Runnable {
    private final Map<String, Map<ChunkPosition, Set<Location>>> tickingLocations = new ConcurrentHashMap<>();

    @Getter
    private int tickRate;
    @Getter
    private boolean halted = false;
    private boolean running = false;

    @Setter
    private volatile boolean paused = false;

    public void start(Slimefun plugin) {
        this.tickRate = Slimefun.getCfg().getInt("URID.custom-ticker-delay");

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, this, 100L, tickRate);
    }

    private void reset() {
        running = false;
    }

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(3), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private boolean initialized = false;

    @Override
    public void run() {
        if (paused) {
            return;
        }

        try {
            if (running) {
                return;
            }

            running = true;
            if (!halted) {
                Set<Map.Entry<String, Map<ChunkPosition, Set<Location>>>> set = tickingLocations.entrySet();
                CompletableFuture<?>[] cfArr = set.stream().map(entry -> CompletableFuture.runAsync(() -> {
                    for (Map.Entry<ChunkPosition, Set<Location>> e : entry.getValue().entrySet()) {
                        tickChunk(e.getKey(), e.getValue());
                    }
                }, executor)).toArray(CompletableFuture[]::new);
                CompletableFuture.allOf(cfArr).join();
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

    private void tickChunk(ChunkPosition chunk, Set<Location> locations) {
        try {
            if (chunk.isLoaded()) {
                for (Location l : locations) {
                    tickLocation(l);
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException x) {
            Slimefun.logger()
                    .log(Level.SEVERE, x, () -> "An Exception has occurred while trying to resolve Chunk: " + chunk);
        }
    }

    private void tickLocation(Location l) {
        SlimefunBlockData blockData = StorageCacheUtils.getBlock(l);
        if (blockData == null || !blockData.isDataLoaded() || blockData.isPendingRemove()) {
            return;
        }
        SlimefunItem item = SlimefunItem.getById(blockData.getSfId());
        CargoManager cargoManager;
        if (item instanceof CargoManager) {
            cargoManager = (CargoManager) item;
        } else {
            disableTicker(l);
            return;
        }
        if (item.isDisabledIn(l.getWorld())) {
            return;
        }

        try {
            cargoManager.getTicker().update();
            Block b = l.getBlock();
            tickBlock(b, cargoManager, blockData);
            cargoManager.getTicker().startNewTick();
        } catch (RuntimeException x) {
            x.printStackTrace();
        }
    }

    private void tickBlock(Block b, CargoManager item, SlimefunBlockData data) {
        try {
            item.getTicker().tick(b, item, data);
        } catch (RuntimeException | LinkageError x) {
            x.printStackTrace();
        }
    }

    public void halt() {
        halted = true;
    }

    public void enableTicker(Location l) {
        if (!initialized) {
            initialized = true;
        }
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
