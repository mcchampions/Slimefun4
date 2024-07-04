package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.geo.ResourceManager;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOScanner;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This {@link Event} is fired whenever a {@link GEOResource} is being freshly generated.
 * This only occurs when a {@link GEOScanner} queries the {@link Chunk} for a {@link GEOResource}
 * but cannot find it.
 *
 * You can modify this {@link Event} by listening to it.
 *
 * @author TheBusyBiscuit
 *
 * @see ResourceManager
 * @see GEOResource
 * @see GEOScanner
 *
 */
public class GEOResourceGenerationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final World world;
    @Getter
    private final Biome biome;
    @Getter
    private final GEOResource resource;
    private final int x;
    private final int z;

    @Getter
    private int value;

    @ParametersAreNonnullByDefault
    public GEOResourceGenerationEvent(World world, Biome biome, int x, int z, GEOResource resource, int value) {
        this.world = world;
        this.biome = biome;
        this.resource = resource;
        this.x = x;
        this.z = z;

        this.value = value;
    }

    /**
     * This modifies the amount that will be generated.
     *
     * @param value The new supply for this {@link GEOResource}
     */
    public void setValue(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("You cannot set a GEO-Resource supply to a negative value.");
        }

        this.value = value;
    }


    /**
     * This returns the X coordinate of the {@link Chunk} in which the {@link GEOResource}
     * is generated.
     *
     * @return The x value of this {@link Chunk}
     */
    public int getChunkX() {
        return x;
    }

    /**
     * This returns the Z coordinate of the {@link Chunk} in which the {@link GEOResource}
     * is generated.
     *
     * @return The z value of this {@link Chunk}
     */
    public int getChunkZ() {
        return z;
    }

    /**
     * This method returns the {@link Environment} in which the resource is generated.
     * It is equivalent to {@link World#getEnvironment()}.
     *
     * @return The {@link Environment} of this generation
     */
    
    public Environment getEnvironment() {
        return world.getEnvironment();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
