package io.github.thebusybiscuit.slimefun4.core.services.profiler;

import javax.annotation.Nonnull;

class PerformanceSummary {
    PerformanceSummary(@Nonnull SlimefunProfiler profiler, long totalElapsedTime, int totalTickedBlocks) {}

    public void send(@Nonnull PerformanceInspector sender) {}
}
