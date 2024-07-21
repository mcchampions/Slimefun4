package io.github.thebusybiscuit.slimefun4.api.exceptions;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import java.io.Serial;

/**
 * An {@link IdConflictException} is thrown whenever two Addons try to add
 * a {@link SlimefunItem} with the same id.
 *
 * @author TheBusyBiscuit
 *
 */
public class IdConflictException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -733012666374895255L;

    /**
     * Constructs a new {@link IdConflictException} with the given items.
     *
     * @param item1
     *            The first {@link SlimefunItem} with this id
     * @param item2
     *            The second {@link SlimefunItem} with this id
     */
    public IdConflictException(SlimefunItem item1, SlimefunItem item2) {
        super("Two items have conflicting ids: " + item1 + " and " + item2);
    }
}
