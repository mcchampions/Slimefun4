package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import javax.annotation.Nullable;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} is fired whenever an {@link MachineProcessor} has completed a {@link MachineOperation}.
 *
 * @author poma123
 * @author TheBusyBiscuit
 *
 */
public class AsyncMachineOperationFinishEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final BlockPosition position;
    private final MachineProcessor<?> machineProcessor;
    private final MachineOperation machineOperation;

    public <T extends MachineOperation> AsyncMachineOperationFinishEvent(
            BlockPosition pos, MachineProcessor<T> processor, T operation) {
        super(!Bukkit.isPrimaryThread());

        this.position = pos;
        this.machineProcessor = processor;
        this.machineOperation = operation;
    }

    /**
     * The {@link MachineProcessor} instance of the machine.
     *
     * @return The {@link MachineProcessor} instance of the machine
     */
    @Nullable public MachineProcessor<?> getProcessor() {
        return machineProcessor;
    }

    /**
     * This returns the used {@link MachineOperation} in the process.
     *
     * @return The {@link MachineOperation} of the process
     */
    @Nullable public MachineOperation getOperation() {
        return machineOperation;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
