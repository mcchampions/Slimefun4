package city.norain.slimefun4.timings;

import city.norain.slimefun4.timings.entry.TimingEntry;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import javax.annotation.Nonnull;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
public class SQLProfiler {
    private final boolean isProfiling = false;

    public void initSlowSqlCheck(@Nonnull Slimefun plugin) {}

    public void start() {}

    public void subscribe(@Nonnull CommandSender sender) {}

    public void recordEntry(TimingEntry timingEntry) {}

    public void finishEntry(TimingEntry timingEntry) {}

    public void stop() {}

    public void shutdown() {}

    public void generateReport() {}
}
