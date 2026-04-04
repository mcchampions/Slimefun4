package io.github.thebusybiscuit.slimefun4.core.services.profiler;

import city.norain.slimefun4.utils.SlimefunPoolExecutor;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerTask;

import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import lombok.Getter;
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
 * @see TickerTask
 */
public class SlimefunProfiler {
    /**
     * All possible values of {@link PerformanceRating}.
     * We cache these for fast access since Enum#values() creates
     * an array everytime it is called.
     */
    private final PerformanceRating[] performanceRatings = PerformanceRating.values();

    /**
     * This boolean marks whether we are currently profiling or not.
     */
    @Getter
    private final boolean isProfiling = false;

    /**
     * This method terminates the {@link SlimefunProfiler}.
     * We need to call this method when the {@link Server} shuts down to prevent any
     * of our {@link Thread Threads} from being kept alive.
     */
    public void kill() {}

    /**
     * This method starts the profiling, data from previous runs will be cleared.
     */
    public void start() {}

    /**
     * This method starts a new profiler entry.
     *
     * @return A timestamp, best fed back into {@link #closeEntry(Location, SlimefunItem, long)}
     */
    public long newEntry() {
        return 0;
    }

    /**
     * This method schedules a given amount of entries for the future.
     * Be careful to {@link #closeEntry(Location, SlimefunItem, long)} all of them again!
     * No {@link PerformanceSummary} will be sent until all entries were closed.
     *
     * If the specified amount is negative, scheduled entries will be removed
     *
     * @param amount The amount of entries that should be scheduled. Can be negative
     */
    public void scheduleEntries(int amount) {}

    /**
     * This method closes a previously started entry.
     * Make sure to call {@link #newEntry()} to get the timestamp in advance.
     *
     * @param l         The {@link Location} of our {@link Block}
     * @param item      The {@link SlimefunItem} at this {@link Location}
     * @param timestamp The timestamp marking the start of this entry, you can retrieve it using {@link #newEntry()}
     * @return The total timings of this entry
     */
    public long closeEntry(@Nonnull Location l, @Nonnull SlimefunItem item, long timestamp) {
        return 0;
    }

    /**
     * This stops the profiling.
     */
    public void stop() {}

    public void registerPool(SlimefunPoolExecutor executor) {}

    private void finishReport() {}

    /**
     * This method requests a summary for the given {@link PerformanceInspector}.
     * The summary will be sent upon the next available moment in time.
     *
     * @param inspector The {@link PerformanceInspector} who shall receive this summary.
     */
    public void requestSummary(@Nonnull PerformanceInspector inspector) {}

    @Nonnull
    protected Map<String, Long> getByItem() {
        return Collections.emptyMap();
    }

    @Nonnull
    protected Map<String, Long> getByPlugin() {
        return Collections.emptyMap();
    }

    @Nonnull
    protected Map<String, Long> getByChunk() {
        return Collections.emptyMap();
    }

    protected int getBlocksInChunk(@Nonnull String chunk) {
        return 0;
    }

    protected int getBlocksOfId(@Nonnull String id) {
        return 0;
    }

    protected int getBlocksFromPlugin(@Nonnull String pluginName) {
        return 0;
    }

    protected float getPercentageOfTick() {
        return 0;
    }

    /**
     * This method returns the current {@link PerformanceRating}.
     *
     * @return The current performance grade
     */
    @Nonnull
    public PerformanceRating getPerformance() {
        float percentage = getPercentageOfTick();

        for (PerformanceRating rating : performanceRatings) {
            if (rating.test(percentage)) {
                return rating;
            }
        }

        return PerformanceRating.UNKNOWN;
    }

    @Nonnull
    public String getTime() {
        return "";
    }

    public int getTickRate() {
        return 20;
    }

    /**
     * This method checks whether the {@link SlimefunProfiler} has collected timings on
     * the given {@link Block}
     *
     * @param b The {@link Block}
     * @return Whether timings of this {@link Block} have been collected
     */
    public boolean hasTimings(@Nonnull Block b) {
        return false;
    }

    public String getTime(@Nonnull Block b) {
        return "";
    }

    public String getTime(@Nonnull Chunk chunk) {
        return "";
    }

    public String getTime(@Nonnull SlimefunItem item) {
        return "";
    }

    /**
     * Get and reset the average millisecond timing for this {@link SlimefunProfiler}.
     *
     * @return The average millisecond timing for this {@link SlimefunProfiler}.
     */
    public long getAndResetAverageTimings() {
        return 0;
    }

    /**
     * Get and reset the average nanosecond timing for this {@link SlimefunProfiler}.
     *
     * @return The average nanosecond timing for this {@link SlimefunProfiler}.
     */
    public double getAndResetAverageNanosecondTimings() {
        return 0;
    }

    /**
     * Get and reset the average millisecond timing for each machine.
     *
     * @return The average millisecond timing for each machine.
     */
    public double getAverageTimingsPerMachine() {
        return 0;
    }

    public String getThreadPoolStatus() {
        return "";
    }

    public String snapshotThreads() {
        return "";
    }
}
