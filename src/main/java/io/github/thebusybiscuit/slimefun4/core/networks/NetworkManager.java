package io.github.thebusybiscuit.slimefun4.core.networks;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.network.Network;
import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.NetworkListener;
import org.bukkit.Location;
import org.bukkit.Server;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * The {@link NetworkManager} is responsible for holding all instances of {@link Network}
 * and providing some utility methods that would have probably been static otherwise.
 *
 * @author TheBusyBiscuit
 * @author meiamsome
 *
 * @see Network
 * @see NetworkListener
 *
 */
public class NetworkManager {
    private final int maxNodes;
    private final boolean enableVisualizer;
    private final boolean deleteExcessItems;

    /**
     * Fixes #3041
     * <p>
     * We use a {@link CopyOnWriteArrayList} here to ensure thread-safety.
     * This {@link List} is also much more frequently read than being written to.
     * Therefore a {@link CopyOnWriteArrayList} should be perfect for this, even
     * if insertions come at a slight cost.
     */
    private final List<Network> networks = new CopyOnWriteArrayList<>();

    /**
     * This creates a new {@link NetworkManager} with the given capacity.
     *
     * @param maxStepSize
     *            The maximum amount of nodes a {@link Network} can have
     * @param enableVisualizer
     *            Whether the {@link Network} visualizer is enabled
     * @param deleteExcessItems
     *            Whether excess items from a {@link CargoNet} should be voided
     */
    public NetworkManager(int maxStepSize, boolean enableVisualizer, boolean deleteExcessItems) {
        this.enableVisualizer = enableVisualizer;
        this.deleteExcessItems = deleteExcessItems;
        maxNodes = maxStepSize;
    }

    /**
     * This creates a new {@link NetworkManager} with the given capacity.
     *
     * @param maxStepSize
     *            The maximum amount of nodes a {@link Network} can have
     */
    public NetworkManager(int maxStepSize) {
        this(maxStepSize, true, false);
    }

    /**
     * This method returns the limit of nodes a {@link Network} can have.
     * This value is read from the {@link Config} file.
     *
     * @return the maximum amount of nodes a {@link Network} can have
     */
    public int getMaxSize() {
        return maxNodes;
    }

    /**
     * This returns whether the {@link Network} visualizer is enabled.
     *
     * @return Whether the {@link Network} visualizer is enabled
     */
    public boolean isVisualizerEnabled() {
        return enableVisualizer;
    }

    /**
     * This returns whether excess items from a {@link CargoNet} should be voided
     * instead of being dropped to the ground.
     *
     * @return Whether to delete excess items
     */
    public boolean isItemDeletionEnabled() {
        return deleteExcessItems;
    }

    /**
     * This returns a {@link List} of every {@link Network} on the {@link Server}.
     * The returned {@link List} is not modifiable.
     *
     * @return A {@link List} containing every {@link Network} on the {@link Server}
     */

    public List<Network> getNetworkList() {
        return networks;
    }

    public <T extends Network> Optional<T> getNetworkFromLocation(@Nullable Location l, Class<T> type) {
        if (l == null) {
            return Optional.empty();
        }

        for (Network network : networks) {
            if (type.isInstance(network) && network.connectsTo(l)) {
                return Optional.of(type.cast(network));
            }
        }

        return Optional.empty();
    }

    public <T extends Network> List<T> getNetworksFromLocation(@Nullable Location l, Class<T> type) {
        if (l == null) {
            // No networks here, if the location does not even exist
            return new ArrayList<>();
        }

        List<T> list = new ArrayList<>();

        for (Network network : networks) {
            if (type.isInstance(network) && network.connectsTo(l)) {
                list.add(type.cast(network));
            }
        }

        return list;
    }

    /**
     * This registers a given {@link Network}.
     *
     * @param network
     *            The {@link Network} to register
     */
    public void registerNetwork(Network network) {
        networks.add(network);
    }

    /**
     * This removes a {@link Network} from the network system.
     *
     * @param network
     *            The {@link Network} to remove
     */
    public void unregisterNetwork(Network network) {
        networks.remove(network);
    }

    /**
     * This method updates every {@link Network} found at the given {@link Location}.
     * More precisely, {@link Network#markDirty(Location)} will be called.
     *
     * @param l
     *            The {@link Location} to update
     */
    public void updateAllNetworks(Location l) {
        try {
            /*
             * No need to create a sublist and loop through it if
             * there aren't even any networks on the server.
             */
            if (networks.isEmpty()) {
                return;
            }

            for (Network network : getNetworksFromLocation(l, Network.class)) {
                network.markDirty(l);
            }
        } catch (RuntimeException x) {
            Slimefun.logger()
                    .log(
                            Level.SEVERE,
                            x,
                            () -> "An Exception was thrown while causing a networks update @ " + new BlockPosition(l));
        }
    }
}
