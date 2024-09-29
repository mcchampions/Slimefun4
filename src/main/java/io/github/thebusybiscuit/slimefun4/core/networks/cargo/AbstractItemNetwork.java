package io.github.thebusybiscuit.slimefun4.core.networks.cargo;

import io.github.thebusybiscuit.slimefun4.api.network.Network;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

/**
 * An abstract super class of {@link CargoNet} that handles
 * interactions with ChestTerminal.
 *
 * @author TheBusyBiscuit
 *
 */
abstract class AbstractItemNetwork extends Network {
    /**
     * This is a cache for the {@link BlockFace} a node is facing, so we don't need to
     * request the {@link BlockData} each time we visit a node
     */
    protected final Map<Location, BlockFace> connectorCache = new HashMap<>();

    /**
     * This is our cache for the {@link ItemFilter} for each node.
     */
    protected final Map<Location, ItemFilter> filterCache = new HashMap<>();

    protected AbstractItemNetwork(Location regulator) {
        super(Slimefun.getNetworkManager(), regulator);
    }

    protected Optional<Block> getAttachedBlock(Location l) {
        if (l.getWorld().isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)) {
            Block block = l.getBlock();
            if (block.getType() == Material.PLAYER_WALL_HEAD) {
                BlockFace cached = connectorCache.get(l);

                if (cached != null) {
                    return Optional.of(block.getRelative(cached));
                }

                BlockFace face =
                        ((Directional) block.getBlockData()).getFacing().getOppositeFace();
                connectorCache.put(l, face);
                return Optional.of(block.getRelative(face));
            }
        }

        return Optional.empty();
    }

    @Override
    public void markDirty(Location l) {
        markCargoNodeConfigurationDirty(l);
        super.markDirty(l);
    }

    /**
     * This will mark the {@link ItemFilter} of the given node dirty.
     * It will also invalidate the cached rotation.
     *
     * @param node
     *            The {@link Location} of the cargo node
     */
    public void markCargoNodeConfigurationDirty(Location node) {
        ItemFilter filter = filterCache.get(node);

        if (filter != null) {
            filter.markDirty();
        }

        connectorCache.remove(node);
    }

    protected ItemFilter getItemFilter(Block node) {
        Location loc = node.getLocation();
        ItemFilter filter = filterCache.get(loc);

        if (filter == null) {
            ItemFilter newFilter = new ItemFilter(node);
            filterCache.put(loc, newFilter);
            return newFilter;
        } else if (filter.isDirty()) {
            filter.update(node);
            return filter;
        } else {
            return filter;
        }
    }
}
