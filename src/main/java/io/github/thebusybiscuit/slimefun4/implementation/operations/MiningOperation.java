package io.github.thebusybiscuit.slimefun4.implementation.operations;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link MachineOperation} represents an operation
 * with no inputs, only a result.
 *
 * @author TheBusyBiscuit
 *
 */
public class MiningOperation implements MachineOperation {
    @Getter
    private final ItemStack result;

    private final int totalTicks;
    private int currentTicks;

    public MiningOperation(ItemStack result, int totalTicks) {
        this.result = result;
        this.totalTicks = totalTicks;
    }

    @Override
    public void addProgress(int num) {

        currentTicks += num;
    }

    @Override
    public int getProgress() {
        return currentTicks;
    }

    @Override
    public int getTotalTicks() {
        return totalTicks;
    }
}
