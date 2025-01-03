package io.github.thebusybiscuit.slimefun4.api.gps;

import io.github.thebusybiscuit.slimefun4.api.events.WaypointCreateEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.Teleporter;
import java.util.Objects;
import java.util.UUID;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link Waypoint} represents a named {@link Location} that was created by a {@link Player}.
 * It can be used via a {@link Teleporter}.
 *
 * @author TheBusyBiscuit
 *
 * @see WaypointCreateEvent
 * @see GPSNetwork
 * @see TeleportationManager
 * @see Teleporter
 *
 */
@Getter
public class Waypoint {
    /**
     * -- GETTER --
     *  This returns the owner's
     *  of the
     * .
     *
     */
    private final UUID ownerId;
    /**
     * -- GETTER --
     *  This method returns the unique identifier for this
     * .
     *
     */
    private final String id;
    /**
     * -- GETTER --
     *  This returns the name of this
     * .
     *
     */
    private final String name;
    /**
     * -- GETTER --
     *  This returns the
     *  of this
     *
     */
    private final Location location;

    /**
     * This constructs a new {@link Waypoint} object.
     *
     * @param profile
     *            The owning {@link PlayerProfile}
     * @param id
     *            The unique id for this {@link Waypoint}
     * @param loc
     *            The {@link Location} of the {@link Waypoint}
     * @param name
     *            The name of this {@link Waypoint}
     *
     * @deprecated Use {@link #Waypoint(UUID, String, Location, String)} instead
     */
    public Waypoint(PlayerProfile profile, String id, Location loc, String name) {
        this(profile.getUUID(), id, loc, name);
    }

    /**
     * This constructs a new {@link Waypoint} object.
     *
     * @param ownerId
     *            The owning {@link Player}'s {@link UUID}
     * @param id
     *            The unique id for this {@link Waypoint}
     * @param loc
     *            The {@link Location} of the {@link Waypoint}
     * @param name
     *            The name of this {@link Waypoint}
     */
    public Waypoint(UUID ownerId, String id, Location loc, String name) {
        this.ownerId = ownerId;
        this.id = id;
        this.location = loc;
        this.name = name;
    }

    /**
     * This returns the owner of the {@link Waypoint}.
     *
     * @return The corresponding {@link PlayerProfile}
     *
     * @deprecated Use {@link #getOwnerId()} instead
     */

    @Deprecated
    public PlayerProfile getOwner() {
        // This is jank and should never actually return null
        return PlayerProfile.find(Bukkit.getOfflinePlayer(ownerId)).orElse(null);
    }

    /**
     * This method returns whether this {@link Waypoint} is a Deathpoint.
     *
     * @return Whether this is a Deathpoint
     */
    public boolean isDeathpoint() {
        return name.startsWith("player:death ");
    }

    /**
     * This method returns the {@link ItemStack} icon for this {@link Waypoint}.
     * The icon is dependent on the {@link Environment} the {@link Waypoint} is in
     * and whether it is a Deathpoint.
     *
     * @return The {@link ItemStack} icon for this {@link Waypoint}
     */

    public ItemStack getIcon() {
        return GPSNetwork.getIcon(name, location.getWorld().getEnvironment());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.ownerId, this.id, this.name, this.location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Waypoint waypoint)) {
            return false;
        }

        return this.ownerId.equals(waypoint.ownerId)
                && id.equals(waypoint.id)
                && location.equals(waypoint.location)
                && name.equals(waypoint.name);
    }
}
