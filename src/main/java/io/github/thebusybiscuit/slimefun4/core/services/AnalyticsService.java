package io.github.thebusybiscuit.slimefun4.core.services;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class represents an analytics service that sends data.
 * This data is used to analyse performance of this {@link Plugin}.
 * <p>
 * You can find more info in the README file of this Project on GitHub.
 *
 * @author WalshyDev
 */
public class AnalyticsService {
    public AnalyticsService(JavaPlugin plugin) {}

    public void start() {}

    // We'll send some timing data every minute.
    // To date, we collect the tick interval, the avg timing per tick and avg timing per machine
    @Nonnull
    private Runnable sendTimingsAnalytics() {
        return () -> {};
    }

    public void recordPlayerProfileDataTime(@Nonnull String backend, boolean load, long nanoseconds) {}

    // Important: Keep the order of these doubles and blobs the same unless you increment the version number
    // If a value is no longer used, just send null or replace it with a new value - don't shift the order
    @ParametersAreNonnullByDefault
    private void send(String id, double[] doubles, String[] blobs) {}
}
