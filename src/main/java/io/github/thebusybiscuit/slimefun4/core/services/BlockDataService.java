package io.github.thebusybiscuit.slimefun4.core.services;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import java.util.Optional;
import java.util.logging.Level;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * The {@link BlockDataService} is similar to the {@link CustomItemDataService},
 * it is responsible for storing NBT data inside a {@link TileState}.
 * <p>
 * This is used to speed up performance and prevent
 *
 * @author TheBusyBiscuit
 *
 */
public class BlockDataService implements Keyed {
    private final NamespacedKey namespacedKey;

    /**
     * This creates a new {@link BlockDataService} for the given {@link Plugin}.
     * The {@link Plugin} and key will together form a {@link NamespacedKey} used to store
     * data on a {@link TileState}.
     *
     * @param plugin
     *            The {@link Plugin} responsible for this service
     * @param key
     *            The key under which to store data
     */
    public BlockDataService(Plugin plugin, String key) {
        namespacedKey = new NamespacedKey(plugin, key);
    }

    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }

    /**
     * This will store the given {@link String} inside the NBT data of the given {@link Block}
     *
     * @param b
     *            The {@link Block} in which to store the given value
     * @param value
     *            The value to store
     */
    public void setBlockData(Block b, String value) {
        BlockState state = b.getState();

        if (state instanceof TileState tileState) {
            try {
                PersistentDataContainer container = tileState.getPersistentDataContainer();
                container.set(namespacedKey, PersistentDataType.STRING, value);
                state.update();
            } catch (RuntimeException x) {
                Slimefun.logger().log(Level.SEVERE, "Please check if your Server Software is up to date!");

                String serverSoftware = Bukkit.getName();
                Slimefun.logger()
                        .log(
                                Level.SEVERE,
                                () -> serverSoftware + " | " + Bukkit.getVersion() + " | " + Bukkit.getBukkitVersion());

                Slimefun.logger()
                        .log(
                                Level.SEVERE,
                                "An Exception was thrown while trying to set Persistent Data for a Block",
                                x);
            }
        }
    }

    /**
     * This method returns the NBT data previously stored inside this {@link Block}.
     *
     * @param b
     *            The {@link Block} to retrieve data from
     *
     * @return The stored value
     */
    public Optional<String> getBlockData(Block b) {
        BlockState state = b.getState(false);
        PersistentDataContainer container = getPersistentDataContainer(state);

        if (container != null) {
            return Optional.ofNullable(container.get(namespacedKey, PersistentDataType.STRING));
        } else {
            return Optional.empty();
        }
    }

    @Nullable private PersistentDataContainer getPersistentDataContainer(BlockState state) {
        if (state instanceof TileState tileState) {
            return tileState.getPersistentDataContainer();
        } else {
            return null;
        }
    }

    /**
     * This method checks whether the given {@link Material} is a Tile Entity.
     * This is used to determine whether the {@link Block} produced by this {@link Material}
     * produces a {@link TileState}, making it useable as a {@link PersistentDataHolder}.
     * <p>
     * Due to {@link Block#getState()} being a very expensive call performance-wise though,
     * this simple lookup method is used instead.
     *
     * @param type
     *            The {@link Material} to check for
     *
     * @return Whether the given {@link Material} is considered a Tile Entity
     */
    public boolean isTileEntity(@Nullable Material type) {
        if (type == null || type.isAir()) {
            // Cannot store data on air
            return false;
        }

        return SlimefunTag.TILE_ENTITIES.isTagged(type);
    }
}
