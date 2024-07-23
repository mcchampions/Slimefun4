package io.github.thebusybiscuit.slimefun4.core.services.profiler.inspectors;

import io.github.thebusybiscuit.slimefun4.core.services.profiler.PerformanceInspector;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.SummaryOrderType;
import java.util.UUID;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * This implementation of {@link PerformanceInspector} refers to a {@link Player}.
 * It also supports {@link TextComponent TextComponents} for rich text messages.
 *
 * @author TheBusyBiscuit
 *
 */
@Deprecated
public class PlayerPerformanceInspector implements PerformanceInspector {

    /**
     * The order type of the timings.
     */
    private final SummaryOrderType orderType;

    /**
     * This creates a new {@link PlayerPerformanceInspector} for the given {@link Player}.
     *
     * @param player
     *            The {@link Player}
     * @param orderType
     *            The {@link SummaryOrderType} of the timings
     */
    public PlayerPerformanceInspector(Player player, SummaryOrderType orderType) {
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
        return false;
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

    public void sendMessage(TextComponent component) {}
}
