package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.gps.GPSNetwork;
import io.github.thebusybiscuit.slimefun4.api.gps.TeleportationManager;
import io.github.thebusybiscuit.slimefun4.api.gps.Waypoint;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * A {@link WaypointCreateEvent} is called when a {@link Player} creates a new waypoint.
 * Either manually or through dying with an emergency transmitter.
 *
 * @author TheBusyBiscuit
 *
 * @see GPSNetwork
 * @see TeleportationManager
 * @see Waypoint
 *
 */
public class WaypointCreateEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /**
     * -- SETTER --
     *  This sets the
     *  of the waypoint.
     *  The
     *  may never be null!
     *
     */
    @Setter
    @Getter
    private Location location;
    /**
     * -- SETTER --
     *  This sets the name of the waypoint to the given argument.
     *
     */
    @Setter
    @Getter
    private String name;

    @Getter
    private final boolean deathpoint;
    private boolean cancelled;

    public WaypointCreateEvent(Player player, String name, Location location) {
        super(player);
        this.location = location;
        this.name = name;
        this.deathpoint = name.startsWith("player:death ");
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
