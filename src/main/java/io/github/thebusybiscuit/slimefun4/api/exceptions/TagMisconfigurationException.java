package io.github.thebusybiscuit.slimefun4.api.exceptions;

import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.NamespacedKey;

import java.io.Serial;

/**
 * An {@link TagMisconfigurationException} is thrown whenever a {@link SlimefunTag}
 * contains illegal, invalid or unknown values.
 *
 * @author TheBusyBiscuit
 */
public class TagMisconfigurationException extends Exception {
    @Serial
    private static final long serialVersionUID = 5412127960821774280L;

    /**
     * This constructs a new {@link TagMisconfigurationException} for the given
     * {@link SlimefunTag}'s {@link NamespacedKey} with the provided context.
     *
     * @param key     The {@link NamespacedKey} of our {@link SlimefunTag}
     * @param message The message to display
     */
    public TagMisconfigurationException(NamespacedKey key, String message) {
        super("Tag '" + key + "' has been misconfigured: " + message);
    }

    /**
     * This constructs a new {@link TagMisconfigurationException} for the given
     * {@link SlimefunTag}'s {@link NamespacedKey} with the provided context.
     *
     * @param key   The {@link NamespacedKey} of our {@link SlimefunTag}
     * @param cause The {@link Throwable} which has caused this to happen
     */
    public TagMisconfigurationException(NamespacedKey key, Throwable cause) {
        super("Tag '" + key + "' has been misconfigured (" + cause.getMessage() + ')', cause);
    }
}
