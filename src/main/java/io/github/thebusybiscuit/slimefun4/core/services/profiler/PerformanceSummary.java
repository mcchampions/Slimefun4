package io.github.thebusybiscuit.slimefun4.core.services.profiler;

import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

class PerformanceSummary {
    PerformanceSummary(SlimefunProfiler profiler, long totalElapsedTime, int totalTickedBlocks) {}

    public void send(PerformanceInspector sender) {}


    private void summarizeTimings(
            int count,
            String name,
            PerformanceInspector inspector,
            Map<String, Long> map,
            Function<Map.Entry<String, Long>, String> formatter) {}

    @Nonnull

    private TextComponent summarizeAsTextComponent(
            int count,
            String prefix,
            List<Map.Entry<String, Long>> results,
            Function<Entry<String, Long>, String> formatter) {
        return null;
    }

    @Nonnull

    private String summarizeAsString(
            PerformanceInspector inspector,
            int count,
            String prefix,
            List<Entry<String, Long>> results,
            Function<Entry<String, Long>, String> formatter) {
        return "";
    }

    @Nonnull
    private String getPerformanceRating() {
        return "";
    }
}
