package io.github.thebusybiscuit.slimefun4.core.services.profiler;

/**
 * Holds the different types of ordering for summaries.
 *
 * @author Walshy
 */
@Deprecated
public enum SummaryOrderType {
    /**
     * Sort by highest to the lowest total timings
     */
    HIGHEST,
    /**
     * Sort by lowest to the highest total timings
     */
    LOWEST,
    /**
     * Sort by average timings (highest to lowest)
     */
    AVERAGE
}
