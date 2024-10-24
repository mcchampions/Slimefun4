package io.github.thebusybiscuit.slimefun4.core.services.profiler;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerTask;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;

/**
 * The {@link SlimefunProfiler} works closely to the {@link TickerTask} and is
 * responsible for monitoring that task.
 * It collects timings data for any ticked {@link Block} and the corresponding {@link SlimefunItem}.
 * This allows developers to identify laggy {@link SlimefunItem SlimefunItems} or {@link SlimefunAddon SlimefunAddons}.
 * But it also enables Server Admins to locate lag-inducing areas on the {@link Server}.
 *
 * @author TheBusyBiscuit
 *
 * @see TickerTask
 *
 */
@Deprecated
public class SlimefunProfiler {
    public void kill() {}

    public void start() {}

    public long newEntry() {
        return System.nanoTime();
    }

    public void scheduleEntries(int amount) {}

    public long closeEntry(Location l, SlimefunItem item, long timestamp) {
        if (timestamp == 0) {
            return 0;
        }

        return System.nanoTime() - timestamp;
    }

    /**
     * This stops the profiling.
     */
    public void stop() {}

    public void requestSummary(PerformanceInspector inspector) {}

    /**
     * This method returns the current {@link PerformanceRating}.
     *
     * @return The current performance grade
     */

    public PerformanceRating getPerformance() {
        return PerformanceRating.UNKNOWN;
    }

    public String getTime() {
        return "0ms";
    }

    public int getTickRate() {
        return Slimefun.getTickerTask().getTickRate();
    }

    public boolean hasTimings(Block b) {
        return true;
    }

    public String getTime(Block b) {
        return "0ms";
    }

    public String getTime(Chunk chunk) {
        return "0ms";
    }

    public String getTime(SlimefunItem item) {
        return "0ms";
    }

    public long getAndResetAverageTimings() {
        return 0;
    }

    public double getAndResetAverageNanosecondTimings() {
        return 0;
    }

    public double getAverageTimingsPerMachine() {
        return 0;
    }
}
