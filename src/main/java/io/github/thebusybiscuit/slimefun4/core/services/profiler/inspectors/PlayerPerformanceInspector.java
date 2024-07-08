package io.github.thebusybiscuit.slimefun4.core.services.profiler.inspectors;

import io.github.thebusybiscuit.slimefun4.core.services.profiler.PerformanceInspector;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.SummaryOrderType;
import java.util.UUID;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This implementation of {@link PerformanceInspector} refers to a {@link Player}.
 * It also supports {@link TextComponent TextComponents} for rich text messages.
 *
 * @author TheBusyBiscuit
 *
 */
public class PlayerPerformanceInspector implements PerformanceInspector {

    /**
     * Our reference to the {@link UUID} of the {@link Player}.
     */
    private final UUID uuid;

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

        this.uuid = player.getUniqueId();
        this.orderType = orderType;
    }

    /**
     * {@inheritDoc}
     */
    private @Nullable Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        Player player = getPlayer();
        return player != null && player.isOnline();
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
    public void sendMessage(String msg) {
        Player player = getPlayer();

        if (player != null) {
            player.sendMessage(msg);
        }
    }

    public void sendMessage(TextComponent component) {
        Player player = getPlayer();

        if (player != null) {
            player.spigot().sendMessage(component);
        }
    }
}
