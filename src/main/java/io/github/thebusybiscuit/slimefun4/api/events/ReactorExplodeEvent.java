package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.Reactor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link ReactorExplodeEvent} is called whenever a reactor explodes.
 *
 * @author TheBusyBiscuit
 *
 */
@Getter
public class ReactorExplodeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /**
     * -- GETTER --
     *  This returns the
     *  where the reactor exploded.
     *
     */
    private final Location location;
    /**
     * -- GETTER --
     *  The
     *  instance of the exploded reactor.
     *
     */
    private final Reactor reactor;

    public ReactorExplodeEvent(Location l, Reactor reactor) {
        this.location = l;
        this.reactor = reactor;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
