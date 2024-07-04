package io.github.thebusybiscuit.slimefun4.implementation.operations;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import lombok.Getter;
import org.apache.commons.lang.Validate;
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
    private int currentTicks = 0;

    public MiningOperation(ItemStack result, int totalTicks) {
        Validate.notNull(result, "The result cannot be null");
        Validate.isTrue(
                totalTicks >= 0,
                "The amount of total ticks must be a positive integer or zero, received: " + totalTicks);

        this.result = result;
        this.totalTicks = totalTicks;
    }

    @Override
    public void addProgress(int num) {
        Validate.isTrue(num > 0, "Progress must be positive.");
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
