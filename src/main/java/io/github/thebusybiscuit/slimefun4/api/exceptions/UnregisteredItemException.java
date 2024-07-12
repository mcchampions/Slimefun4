package io.github.thebusybiscuit.slimefun4.api.exceptions;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.plugin.Plugin;

import java.io.Serial;

/**
 * An {@link UnregisteredItemException} is thrown whenever a {@link Plugin} tried to
 * access a method prematurely from {@link SlimefunItem} that can only be called after the
 * {@link SlimefunItem} was registered.
 * <p>
 * In other words... calling this method this early can not result in a logical output, making
 * this an {@link Exception}.
 *
 * @author TheBusyBiscuit
 *
 */
public class UnregisteredItemException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4684752240435069678L;

    /**
     * Constructs a new {@link UnregisteredItemException} with the given {@link SlimefunItem}
     *
     * @param item
     *            The {@link SlimefunItem} that was affected by this
     */
    @ParametersAreNonnullByDefault
    public UnregisteredItemException(SlimefunItem item) {
        super(item + " has not been registered yet.");
    }
}
