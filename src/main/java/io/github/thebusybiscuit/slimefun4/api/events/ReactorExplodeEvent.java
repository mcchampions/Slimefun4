package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.Reactor;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
        Validate.notNull(l, "A Location must be provided");
        Validate.notNull(reactor, "A Reactor cannot be null");

        this.location = l;
        this.reactor = reactor;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }

    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
