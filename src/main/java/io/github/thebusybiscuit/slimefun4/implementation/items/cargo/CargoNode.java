package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import javax.annotation.Nonnull;
import org.bukkit.block.Block;

/**
 * This interface marks a {@link SlimefunItem} as a {@link CargoNode}.
 * <p>
 * Do not implement this interface yourself, it will not have any effect.
 *
 * @author TheBusyBiscuit
 *
 */
public interface CargoNode {

    /**
     * This returns the selected channel for the given {@link Block}.
     *
     * @param b
     *            The {@link Block}
     *
     * @return The channel which this {@link CargoNode} is currently on
     */
    int getSelectedChannel(Block b);

    /**
     * This returns whether this {@link CargoNode} has item filtering capabilities.
     *
     * @return Whether this {@link CargoNode} can filter items
     */
    boolean hasItemFilter();
}
