package io.github.thebusybiscuit.slimefun4.core.services.holograms;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * This service is responsible for handling holograms.
 *
 * @author TheBusyBiscuit
 * @see HologramOwner
 */
public class HologramsService {
    /**
     * The radius in which we scan for holograms
     */
    private static final double RADIUS = 0.5;

    /**
     * The frequency at which to purge.
     * Every 45 seconds.
     */
    private static final long PURGE_RATE = 45L * 20L;

    /**
     * Our {@link Plugin} instance
     */
    private final Plugin plugin;

    /**
     * The default hologram offset
     */
    @Getter
    private final Vector defaultOffset = new Vector(0.5, 0.75, 0.5);

    /**
     * The {@link NamespacedKey} used to store data on a hologram
     */
    private final NamespacedKey persistentDataKey;

    /**
     * Our cache to save {@link Entity} lookups
     */
    private final Map<BlockPosition, Hologram> cache = new HashMap<>();

    /**
     * This constructs a new {@link HologramsService}.
     *
     * @param plugin Our {@link Plugin} instance
     */
    public HologramsService(Plugin plugin) {
        this.plugin = plugin;

        // Null-Validation is performed in the NamespacedKey constructor
        persistentDataKey = new NamespacedKey(plugin, "hologram_id");
    }

    /**
     * This will start the {@link HologramsService} and schedule a repeating
     * purge-task.
     */
    public void start() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::purge, PURGE_RATE, PURGE_RATE);
    }

    /**
     * This purges any expired {@link Hologram}.
     */
    private void purge() {
        cache.values().removeIf(Hologram::hasExpired);
    }

    /**
     * This returns the {@link Hologram} associated with the given {@link Location}.
     * If createIfNoneExists is set to true a new {@link ArmorStand} will be spawned
     * if no existing one could be found.
     *
     * @param loc                The {@link Location}
     * @param createIfNoneExists Whether to create a new {@link ArmorStand} if none was found
     * @return The existing (or newly created) hologram
     */
    @Nullable
    private Hologram getHologram(Location loc, boolean createIfNoneExists) {
        BlockPosition position = new BlockPosition(loc);
        Hologram hologram = cache.get(position);

        // Check if the ArmorStand was cached and still exists
        if (hologram != null && !hologram.hasDespawned()) {
            return hologram;
        }

        // Scan all nearby entities which could be possible holograms
        Collection<Entity> holograms = loc.getWorld().getNearbyEntities(loc, RADIUS, RADIUS, RADIUS, HologramsService::isHologram);

        for (Entity n : holograms) {
            if (n instanceof ArmorStand) {
                PersistentDataContainer container = n.getPersistentDataContainer();

                /*
                 * Any hologram we created will have a persistent data key for identification.
                 * Make sure that the value matches our BlockPosition.
                 */
                if (hasHologramData(container, position)) {
                    if (hologram != null) {
                        // Fixes #2927 - Remove any duplicates we find
                        n.remove();
                    } else {
                        hologram = getAsHologram(position, n, container);
                    }
                }
            }
        }

        if (hologram == null && createIfNoneExists) {
            // Spawn a new ArmorStand
            ArmorStand armorstand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            PersistentDataContainer container = armorstand.getPersistentDataContainer();

            return getAsHologram(position, armorstand, container);
        } else {
            return hologram;
        }
    }

    private boolean hasHologramData(PersistentDataContainer container, BlockPosition position) {
        if (container.has(persistentDataKey, PersistentDataType.LONG)) {
            long value = container.get(persistentDataKey, PersistentDataType.LONG);
            return value == position.getPosition();
        } else {
            return false;
        }
    }

    /**
     * This checks if a given {@link Entity} is an {@link ArmorStand}
     * and whether it has the correct attributes to be considered a {@link Hologram}.
     *
     * @param n The {@link Entity} to check
     * @return Whether this could be a hologram
     */
    private static boolean isHologram(Entity n) {
        if (n instanceof ArmorStand armorStand) {
            // The absolute minimum requirements to count as a hologram
            return !armorStand.isVisible() && armorStand.isSilent() && !armorStand.hasGravity();
        } else {
            return false;
        }
    }

    /**
     * This will cast the {@link Entity} to an {@link ArmorStand} and it will apply
     * all necessary attributes to the {@link ArmorStand}, then return a {@link Hologram}.
     *
     * @param position  The {@link BlockPosition} of this hologram
     * @param entity    The {@link Entity}
     * @param container The {@link PersistentDataContainer} of the given {@link Entity}
     * @return The {@link Hologram}
     */
    @Nullable
    private Hologram getAsHologram(
            BlockPosition position, Entity entity, PersistentDataContainer container) {
        if (entity instanceof ArmorStand armorStand) {
            armorStand.setVisible(false);
            armorStand.setInvulnerable(true);
            armorStand.setSilent(true);
            armorStand.setMarker(true);
            armorStand.setAI(false);
            armorStand.setGravity(false);
            armorStand.setRemoveWhenFarAway(false);

            // Set a persistent tag to re-identify the correct hologram later
            container.set(persistentDataKey, PersistentDataType.LONG, position.getPosition());

            // Store in cache for faster access
            Hologram hologram = new Hologram(armorStand.getUniqueId());
            cache.put(position, hologram);

            return hologram;
        } else {
            // This should never be reached
            return null;
        }
    }

    /**
     * This updates the {@link Hologram}.
     * You can use it to set the nametag or other properties.
     * <p>
     * <strong>This method must be executed on the main {@link Server} {@link Thread}.</strong>
     *
     * @param loc      The {@link Location}
     * @param consumer The callback to run
     */
    private void updateHologram(Location loc, Consumer<Hologram> consumer) {
        Runnable runnable = () -> {
            try {
                Hologram hologram = getHologram(loc, true);

                if (hologram != null) {
                    consumer.accept(hologram);
                }
            } catch (RuntimeException | LinkageError x) {
                Slimefun.logger().log(Level.SEVERE, "Hologram located at {0}", new BlockPosition(loc));
                Slimefun.logger().log(Level.SEVERE, "Something went wrong while trying to update this hologram", x);
            }
        };

        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Slimefun.runSync(runnable);
        }
    }

    /**
     * This removes the {@link Hologram} at that given {@link Location}.
     * <p>
     * <strong>This method must be executed on the main {@link Server} {@link Thread}.</strong>
     *
     * @param loc The {@link Location}
     * @return Whether the {@link Hologram} could be removed, false if the {@link Hologram} does not
     * exist or was already removed
     */
    public boolean removeHologram(Location loc) {
        try {
            Hologram hologram = getHologram(loc, false);

            if (hologram != null) {
                cache.remove(new BlockPosition(loc));
                hologram.remove();
                return true;
            } else {
                return false;
            }
        } catch (RuntimeException | LinkageError x) {
            Slimefun.logger().log(Level.SEVERE, "Hologram located at {0}", new BlockPosition(loc));
            Slimefun.logger().log(Level.SEVERE, "Something went wrong while trying to remove this hologram", x);
            return false;
        }
    }

    /**
     * This will update the label of the {@link Hologram}.
     *
     * @param loc   The {@link Location} of this {@link Hologram}
     * @param label The label to set, can be null
     */
    public void setHologramLabel(Location loc, @Nullable String label) {
        updateHologram(loc, hologram -> hologram.setLabel(label));
    }
}
