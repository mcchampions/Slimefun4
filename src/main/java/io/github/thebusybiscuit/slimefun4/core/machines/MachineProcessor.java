package io.github.thebusybiscuit.slimefun4.core.machines;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.api.events.AsyncMachineOperationFinishEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link MachineProcessor} manages different {@link MachineOperation}s and handles
 * their progress.
 *
 * @author TheBusyBiscuit
 *
 * @param <T>
 *            The type of {@link MachineOperation} this processor can hold.
 *
 * @see MachineOperation
 * @see MachineProcessHolder
 */
public class MachineProcessor<T extends MachineOperation> {

    private final Map<BlockPosition, T> machines = new ConcurrentHashMap<>();
    @Getter
    private final MachineProcessHolder<T> owner;

    private ItemStack progressBar;

    /**
     * This creates a new {@link MachineProcessor}.
     *
     * @param owner
     *            The owner of this {@link MachineProcessor}.
     */
    public MachineProcessor(MachineProcessHolder<T> owner) {

        this.owner = owner;
    }

    /**
     * This returns the progress bar icon for this {@link MachineProcessor}
     * or null if no progress bar was set.
     *
     * @return The progress bar icon or null
     */
    @Nullable public ItemStack getProgressBar() {
        return progressBar;
    }

    /**
     * This sets the progress bar icon for this {@link MachineProcessor}.
     * You can also set it to null to clear the progress bar.
     *
     * @param progressBar
     *            An {@link ItemStack} or null
     */
    public void setProgressBar(@Nullable ItemStack progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * This method will start a {@link MachineOperation} at the given {@link Location}.
     *
     * @param loc
     *            The {@link Location} at which our machine is located.
     * @param operation
     *            The {@link MachineOperation} to start
     *
     * @return Whether the {@link MachineOperation} was successfully started. This will return false if another
     *         {@link MachineOperation} has already been started at that {@link Location}.
     */
    public boolean startOperation(Location loc, T operation) {

        return startOperation(new BlockPosition(loc), operation);
    }

    /**
     * This method will start a {@link MachineOperation} at the given {@link Block}.
     *
     * @param b
     *            The {@link Block} at which our machine is located.
     * @param operation
     *            The {@link MachineOperation} to start
     *
     * @return Whether the {@link MachineOperation} was successfully started. This will return false if another
     *         {@link MachineOperation} has already been started at that {@link Block}.
     */
    public boolean startOperation(Block b, T operation) {

        return startOperation(new BlockPosition(b), operation);
    }

    /**
     * This method will actually start the {@link MachineOperation}.
     *
     * @param pos
     *            The {@link BlockPosition} of our machine
     * @param operation
     *            The {@link MachineOperation} to start
     *
     * @return Whether the {@link MachineOperation} was successfully started. This will return false if another
     *         {@link MachineOperation} has already been started at that {@link BlockPosition}.
     */
    public boolean startOperation(BlockPosition pos, T operation) {

        return machines.putIfAbsent(pos, operation) == null;
    }

    /**
     * This returns the current {@link MachineOperation} at that given {@link Location}.
     *
     * @param loc
     *            The {@link Location} at which our machine is located.
     *
     * @return The current {@link MachineOperation} or null.
     */
    @Nullable public T getOperation(Location loc) {

        return getOperation(new BlockPosition(loc));
    }

    /**
     * This returns the current {@link MachineOperation} at that given {@link Block}.
     *
     * @param b
     *            The {@link Block} at which our machine is located.
     *
     * @return The current {@link MachineOperation} or null.
     */
    @Nullable public T getOperation(Block b) {

        return getOperation(new BlockPosition(b));
    }

    /**
     * This returns the current {@link MachineOperation} at that given {@link BlockPosition}.
     *
     * @param pos
     *            The {@link BlockPosition} at which our machine is located.
     *
     * @return The current {@link MachineOperation} or null.
     */
    @Nullable public T getOperation(BlockPosition pos) {

        return machines.get(pos);
    }

    /**
     * This will end the {@link MachineOperation} at the given {@link Location}.
     *
     * @param loc
     *            The {@link Location} at which our machine is located.
     *
     * @return Whether the {@link MachineOperation} was successfully ended. This will return false if there was no
     *         {@link MachineOperation} to begin with.
     */
    public boolean endOperation(Location loc) {

        return endOperation(new BlockPosition(loc));
    }

    /**
     * This will end the {@link MachineOperation} at the given {@link Block}.
     *
     * @param b
     *            The {@link Block} at which our machine is located.
     *
     * @return Whether the {@link MachineOperation} was successfully ended. This will return false if there was no
     *         {@link MachineOperation} to begin with.
     */
    public boolean endOperation(Block b) {

        return endOperation(new BlockPosition(b));
    }

    /**
     * This will end the {@link MachineOperation} at the given {@link BlockPosition}.
     *
     * @param pos
     *            The {@link BlockPosition} at which our machine is located.
     *
     * @return Whether the {@link MachineOperation} was successfully ended. This will return false if there was no
     *         {@link MachineOperation} to begin with.
     */
    public boolean endOperation(BlockPosition pos) {

        T operation = machines.remove(pos);

        if (operation != null) {
            /*
             * Only call an event if the operation actually finished.
             * If it was ended prematurely (aka aborted), then we don't call any event.
             */
            if (operation.isFinished()) {
                Event event = new AsyncMachineOperationFinishEvent(pos, this, operation);
                Bukkit.getPluginManager().callEvent(event);
            } else {
                operation.onCancel(pos);
            }

            return true;
        } else {
            return false;
        }
    }

    public void updateProgressBar(BlockMenu inv, int slot, T operation) {

        if (getProgressBar() == null) {
            // No progress bar, no need to update anything.
            return;
        }

        // Update the progress bar in our inventory (if anyone is watching)
        int remainingTicks = operation.getRemainingTicks();
        int totalTicks = operation.getTotalTicks();

        // Fixes #3538 - If the operation is finished, we don't need to update the progress bar.
        if (remainingTicks > 0 || totalTicks > 0) {
            ChestMenuUtils.updateProgressbar(inv, slot, remainingTicks, totalTicks, getProgressBar());
        }
    }
}
