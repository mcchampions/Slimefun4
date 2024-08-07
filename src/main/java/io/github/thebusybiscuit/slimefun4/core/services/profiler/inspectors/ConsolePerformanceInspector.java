package io.github.thebusybiscuit.slimefun4.core.services.profiler.inspectors;

import io.github.thebusybiscuit.slimefun4.core.services.profiler.PerformanceInspector;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.SummaryOrderType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * This implementation of {@link PerformanceInspector} refers to a {@link CommandSender}
 * which is preferabbly a {@link ConsoleCommandSender}.
 * But it can theoretically be used for any type of {@link CommandSender} as it uses uncolored texts.
 *
 * @author TheBusyBiscuit
 *
 */
@Deprecated
public class ConsolePerformanceInspector implements PerformanceInspector {
    /**
     * Whether a summary will be verbose or trimmed of.
     */
    private final boolean verbose;

    /**
     * The order type of the timings.
     */
    private final SummaryOrderType orderType;

    /**
     * This creates a new {@link ConsolePerformanceInspector} for the given {@link CommandSender}.
     *
     * @param console
     *            The {@link CommandSender}, preferably a {@link ConsoleCommandSender}
     * @param verbose
     *            Whether the summary will be verbose or not
     * @param orderType
     *            The {@link SummaryOrderType} of the timings
     */
    public ConsolePerformanceInspector(CommandSender console, boolean verbose, SummaryOrderType orderType) {
        this.verbose = verbose;
        this.orderType = orderType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SummaryOrderType getOrderType() {
        return orderType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(String msg) {}
}
