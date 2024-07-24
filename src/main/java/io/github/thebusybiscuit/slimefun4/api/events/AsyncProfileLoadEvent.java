package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This {@link Event} is called when the {@link PlayerProfile} of a {@link Player}
 * is loaded into memory.
 * The {@link AsyncProfileLoadEvent} is called asynchronously and can be used to "inject"
 * a custom {@link PlayerProfile} if necessary.
 *
 * @author TheBusyBiscuit
 *
 * @see PlayerProfile
 *
 */
public class AsyncProfileLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final UUID uniqueId;
    /**
     * -- SETTER --
     *  This method can be used to inject your custom
     *  implementations.
     *  However, the passed
     *  must have the same
     *  as the original one!
     *
     */
    @Setter
    @Getter
    private PlayerProfile profile;

    public AsyncProfileLoadEvent(PlayerProfile profile) {
        super(true);

        this.uniqueId = profile.getUUID();
        this.profile = profile;
    }

    public UUID getPlayerUUID() {
        return uniqueId;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
