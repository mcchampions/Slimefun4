package io.github.thebusybiscuit.slimefun4.implementation.items.elevator;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * This represents an {@link ElevatorFloor} to which a {@link Player}
 * can travel to using an {@link ElevatorPlate}.
 *
 * @author TheBusyBiscuit
 */
@Getter
public class ElevatorFloor {
    /**
     * The name of this floor.
     * -- GETTER --
     *  This returns the name of this
     * .
     */
    private final String name;

    /**
     * The floor number.
     * -- GETTER --
     *  This returns the number of this floor.
     *  The lowest floor will have the number 0 and it
     *  increments from there.
     *

     */
    private final int number;

    /**
     * The {@link Location} of this floor.
     * -- GETTER --
     *  This returns the
     *  of this
     * .
     */
    private final Location location;

    /**
     * This constructs a new {@link ElevatorFloor} with the given name
     * and the {@link Location} of the provided {@link Block}.
     *
     * @param name  The name of this {@link ElevatorFloor}
     * @param block The {@link Block} of this floor
     */
    public ElevatorFloor(String name, int number, Block block) {
        this.name = name;
        this.number = number;
        this.location = block.getLocation();
    }

    /**
     * This returns the "altitude" of this floor.
     * This is equivalent to the Y level of {@link #getLocation()}.
     *
     * @return The altitude of this floor
     */
    public int getAltitude() {
        return location.getBlockY();
    }

}
