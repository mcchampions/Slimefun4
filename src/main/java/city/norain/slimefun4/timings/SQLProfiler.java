package city.norain.slimefun4.timings;

import city.norain.slimefun4.timings.entry.TimingEntry;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import org.bukkit.command.CommandSender;

public class SQLProfiler {
    @Getter
    private volatile boolean isProfiling;

    private final Map<TimingEntry, Long> samplingEntries = new HashMap<>();

    private final Map<TimingEntry, Long> entries = new HashMap<>();

    private final Set<CommandSender> subscribers = new HashSet<>();

    private long startTime = -1L;

    public void initSlowSqlCheck(Slimefun plugin) {}

    public void start() {}

    public void subscribe(CommandSender sender) {}

    public void recordEntry(TimingEntry timingEntry) {}

    public void finishEntry(TimingEntry timingEntry) {}

    public void stop() {}

    public void shutdown() {}

    public void generateReport() {}

    private String generateReportFile(Map<String, List<Map.Entry<TimingEntry, Long>>> entries) {
        return null;
    }
}
