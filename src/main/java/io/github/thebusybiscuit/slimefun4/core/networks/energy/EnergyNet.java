package io.github.thebusybiscuit.slimefun4.core.networks.energy;

import city.norain.slimefun4.utils.MathUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.ErrorReport;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.network.Network;
import io.github.thebusybiscuit.slimefun4.api.network.NetworkComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * The {@link EnergyNet} is an implementation of {@link Network} that deals with
 * electrical energy being sent from and to nodes.
 *
 * @author meiamsome
 * @author TheBusyBiscuit
 * @see Network
 * @see EnergyNetComponent
 * @see EnergyNetProvider
 * @see EnergyNetComponentType
 */
@Getter
public class EnergyNet extends Network implements HologramOwner {
    private static final int RANGE = 6;

    private final Map<Location, EnergyNetProvider> generators = new HashMap<>();
    private final Map<Location, EnergyNetComponent> capacitors = new HashMap<>();
    private final Map<Location, EnergyNetComponent> consumers = new HashMap<>();

    protected EnergyNet(Location l) {
        super(Slimefun.getNetworkManager(), l);
    }

    @Override
    public int getRange() {
        return RANGE;
    }

    @Override
    public String getId() {
        return "ENERGY_NETWORK";
    }

    @Override
    public NetworkComponent classifyLocation(Location l) {
        if (regulator.equals(l)) {
            return NetworkComponent.REGULATOR;
        }

        EnergyNetComponent component = getComponent(l);

        if (component == null) {
            return null;
        } else {
            return switch (component.getEnergyComponentType()) {
                case CONNECTOR, CAPACITOR -> NetworkComponent.CONNECTOR;
                case CONSUMER, GENERATOR -> NetworkComponent.TERMINUS;
                default -> null;
            };
        }
    }

    @Override
    public void onClassificationChange(Location l, NetworkComponent from, NetworkComponent to) {
        if (from == NetworkComponent.TERMINUS) {
            generators.remove(l);
            consumers.remove(l);
        }

        EnergyNetComponent component = getComponent(l);

        if (component != null) {
            switch (component.getEnergyComponentType()) {
                case CAPACITOR -> capacitors.put(l, component);
                case CONSUMER -> consumers.put(l, component);
                case GENERATOR -> {
                    if (component instanceof EnergyNetProvider provider) {
                        generators.put(l, provider);
                    } else if (component instanceof SlimefunItem item) {
                        item.warn("This Item is marked as a GENERATOR but does not implement the interface"
                                  + " EnergyNetProvider!");
                    }
                }
                default -> {
                }
            }
        }
    }

    public void tick(Block b, SlimefunBlockData blockData) {
        AtomicLong timestamp = new AtomicLong(System.nanoTime());
        if (!regulator.equals(b.getLocation())) {
            updateHologram(b, "&4检测到附近有其他调节器", blockData::isPendingRemove);

            return;
        }

        super.tick();

        if (connectorNodes.isEmpty() && terminusNodes.isEmpty()) {
            updateHologram(b, "&4找不到能源网络", blockData::isPendingRemove);
        } else {
            long generatorsSupply = tickAllGenerators(timestamp::getAndAdd);
            long capacitorsSupply = tickAllCapacitors();
            long supply = generatorsSupply
                          + capacitorsSupply; // NumberUtils.flowSafeAddition(generatorsSupply, capacitorsSupply);
            long remainingEnergy = supply;
            long demand = 0;

            for (Map.Entry<Location, EnergyNetComponent> entry : consumers.entrySet()) {
                Location loc = entry.getKey();

                var data = StorageCacheUtils.getBlock(loc);
                if (data == null || data.isPendingRemove()) {
                    continue;
                }

                EnergyNetComponent component = entry.getValue();
                if (!((SlimefunItem) component).getId().equals(data.getSfId())) {
                    var newItem = SlimefunItem.getById(data.getSfId());
                    if (!(newItem instanceof EnergyNetComponent newComponent)
                        || newComponent.getEnergyComponentType() != EnergyNetComponentType.CONSUMER) {
                        continue;
                    }
                    consumers.put(loc, newComponent);
                    component = newComponent;
                }

                if (!data.isDataLoaded()) {
                    StorageCacheUtils.requestLoad(data);
                    continue;
                }

                int capacity = component.getCapacity();
                int charge = component.getCharge(loc);

                if (charge < capacity) {
                    int availableSpace = capacity - charge;
                    demand = demand + availableSpace; // NumberUtils.flowSafeAddition(demand, availableSpace);

                    if (remainingEnergy > 0) {
                        if (remainingEnergy > availableSpace) {
                            component.setCharge(loc, capacity);
                            remainingEnergy -= availableSpace;
                        } else {
                            component.setCharge(loc, charge + (int) remainingEnergy);
                            remainingEnergy = 0;
                        }
                    }
                }
            }
            storeRemainingEnergy(remainingEnergy);
            updateHologram(blockData, supply, demand);
        }
    }

    private void storeRemainingEnergy(long remainingEnergy) {
        for (Map.Entry<Location, EnergyNetComponent> entry : capacitors.entrySet()) {
            Location loc = entry.getKey();

            SlimefunBlockData data = StorageCacheUtils.getBlock(loc);
            if (data == null || data.isPendingRemove() || !data.isDataLoaded()) {
                continue;
            }

            EnergyNetComponent component = entry.getValue();

            if (remainingEnergy > 0) {
                int capacity = component.getCapacity();

                if (remainingEnergy > capacity) {
                    component.setCharge(loc, capacity);
                    remainingEnergy -= capacity;
                } else {
                    component.setCharge(loc, (int) remainingEnergy);
                    remainingEnergy = 0;
                }
            } else {
                component.setCharge(loc, 0);
            }
        }

        for (Map.Entry<Location, EnergyNetProvider> entry : generators.entrySet()) {
            Location loc = entry.getKey();

            SlimefunBlockData data = StorageCacheUtils.getBlock(loc);
            if (data == null || data.isPendingRemove() || !data.isDataLoaded()) {
                continue;
            }

            EnergyNetProvider component = entry.getValue();
            int capacity = component.getCapacity();

            if (remainingEnergy > 0) {
                if (remainingEnergy > capacity) {
                    component.setCharge(loc, capacity);
                    remainingEnergy -= capacity;
                } else {
                    component.setCharge(loc, (int) remainingEnergy);
                    remainingEnergy = 0;
                }
            } else {
                component.setCharge(loc, 0);
            }
        }
    }

    private long tickAllGenerators(LongConsumer timings) {
        Set<Location> explodedBlocks = new HashSet<>();
        long supply = 0;

        for (Map.Entry<Location, EnergyNetProvider> entry : generators.entrySet()) {
            long timestamp = System.nanoTime();
            Location loc = entry.getKey();
            EnergyNetProvider provider = entry.getValue();
            SlimefunItem item = (SlimefunItem) provider;

            try {
                SlimefunBlockData data = StorageCacheUtils.getBlock(loc);
                if (data == null || data.isPendingRemove()) {
                    continue;
                }

                if (!item.getId().equals(data.getSfId())) {
                    SlimefunItem newItem = SlimefunItem.getById(data.getSfId());
                    if (!(newItem instanceof EnergyNetProvider newProvider)) {
                        continue;
                    }
                    generators.put(loc, newProvider);
                    provider = newProvider;
                }

                if (!data.isDataLoaded()) {
                    StorageCacheUtils.requestLoad(data);
                    continue;
                }

                int energy = provider.getGeneratedOutput(loc, data);

                if (provider.isChargeable()) {
                    energy = MathUtil.saturatedAdd(energy, provider.getCharge(loc));
                }

                if (provider.willExplode(loc, data)) {
                    explodedBlocks.add(loc);
                    Slimefun.getDatabaseManager().getBlockDataController().removeBlock(loc);

                    Slimefun.runSync(() -> {
                        loc.getBlock().setType(Material.LAVA);
                        loc.getWorld().createExplosion(loc, 0.0F, false);
                    });
                } else {
                    supply = supply + energy; // = NumberUtils.flowSafeAddition(supply, energy);
                }
            } catch (RuntimeException | LinkageError throwable) {
                explodedBlocks.add(loc);
                new ErrorReport<>(throwable, loc, item);
            }

            long time = System.nanoTime() - timestamp;
            timings.accept(time);
        }

        // Remove all generators which have exploded
        if (!explodedBlocks.isEmpty()) {
            generators.keySet().removeAll(explodedBlocks);
        }

        return supply;
    }

    private long tickAllCapacitors() {
        long supply = 0;

        for (Map.Entry<Location, EnergyNetComponent> entry : capacitors.entrySet()) {
            supply = supply + entry.getValue().getCharge(entry.getKey()); // NumberUtils.flowSafeAddition(supply,
            // entry.getValue().getCharge(entry.getKey()));
        }

        return supply;
    }

    private void updateHologram(SlimefunBlockData data, double supply, double demand) {
        if (demand > supply) {
            String netLoss = NumberUtils.getCompactDouble(demand - supply);
            updateHologram(
                    data.getLocation().getBlock(), "§4§l- §c" + netLoss + " §7J §e\u26A1", data::isPendingRemove);
        } else {
            String netGain = NumberUtils.getCompactDouble(supply - demand);
            updateHologram(
                    data.getLocation().getBlock(), "§2§l+ §a" + netGain + " §7J §e\u26A1", data::isPendingRemove);
        }
    }

    private static EnergyNetComponent getComponent(Location l) {
        SlimefunItem item = StorageCacheUtils.getSfItem(l);

        if (item instanceof EnergyNetComponent component) {
            return component;
        }

        return null;
    }

    /**
     * This attempts to get an {@link EnergyNet} from a given {@link Location}.
     * If no suitable {@link EnergyNet} could be found, {@code null} will be returned.
     *
     * @param l The target {@link Location}
     * @return The {@link EnergyNet} at that {@link Location}, or {@code null}
     */
    public static EnergyNet getNetworkFromLocation(Location l) {
        return Slimefun.getNetworkManager()
                .getNetworkFromLocation(l, EnergyNet.class)
                .orElse(null);
    }

    /**
     * This attempts to get an {@link EnergyNet} from a given {@link Location}.
     * If no suitable {@link EnergyNet} could be found, a new one will be created.
     *
     * @param l The target {@link Location}
     * @return The {@link EnergyNet} at that {@link Location}, or a new one
     */

    public static EnergyNet getNetworkFromLocationOrCreate(Location l) {
        Optional<EnergyNet> energyNetwork = Slimefun.getNetworkManager().getNetworkFromLocation(l, EnergyNet.class);

        if (energyNetwork.isPresent()) {
            return energyNetwork.get();
        } else {
            EnergyNet network = new EnergyNet(l);
            Slimefun.getNetworkManager().registerNetwork(network);
            return network;
        }
    }
}
