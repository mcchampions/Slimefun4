package city.norain.slimefun4.timings;

import city.norain.slimefun4.timings.entry.TimingEntry;

import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
@Deprecated
public class SQLProfiler {
    public void start() {}

    public void subscribe(CommandSender sender) {}

    public void recordEntry(TimingEntry timingEntry) {}

    public void finishEntry(TimingEntry timingEntry) {}

    public void stop() {}

    public void generateReport() {}

}
