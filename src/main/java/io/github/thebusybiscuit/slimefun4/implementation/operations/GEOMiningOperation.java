package io.github.thebusybiscuit.slimefun4.implementation.operations;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.geo.ResourceManager;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOMiner;
import java.util.OptionalInt;

/**
 * This {@link MachineOperation} represents a {@link GEOMiner}
 * mining a {@link GEOResource}.
 *
 * @author iTwins
 *
 * @see GEOMiner
 */
public class GEOMiningOperation extends MiningOperation {

    private final GEOResource resource;

    public GEOMiningOperation(GEOResource resource, int totalTicks) {
        super(resource.getItem().clone(), totalTicks);
        this.resource = resource;
    }

    /**
     * This returns the {@link GEOResource} back to the chunk
     * when the {@link GEOMiningOperation} gets cancelled
     */
    @Override
    public void onCancel(BlockPosition position) {
        ResourceManager resourceManager = Slimefun.getGPSNetwork().getResourceManager();
        OptionalInt supplies =
                resourceManager.getSupplies(resource, position.getWorld(), position.getChunkX(), position.getChunkZ());
        supplies.ifPresent(s -> resourceManager.setSupplies(
                resource, position.getWorld(), position.getChunkX(), position.getChunkZ(), s + 1));
    }
}
