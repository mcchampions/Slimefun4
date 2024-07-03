package io.github.thebusybiscuit.slimefun4.implementation.operations;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link MachineOperation} represents the process of burning fuel.
 *
 * @author TheBusyBiscuit
 *
 */
public class FuelOperation implements MachineOperation {

    private final ItemStack ingredient;
    private final ItemStack result;

    private final int totalTicks;
    private int currentTicks = 0;

    public FuelOperation(MachineFuel recipe) {
        this(recipe.getInput(), recipe.getOutput(), recipe.getTicks());
    }

    public FuelOperation(ItemStack ingredient, @Nullable ItemStack result, int totalTicks) {
        Validate.notNull(ingredient, "The Ingredient cannot be null");
        Validate.isTrue(totalTicks > 0, "The amount of total ticks must be a positive integer");

        this.ingredient = ingredient;
        this.result = result;
        this.totalTicks = totalTicks;
    }

    @Override
    public void addProgress(int num) {
        Validate.isTrue(num > 0, "Progress must be positive.");
        currentTicks += num;
    }

    
    public ItemStack getIngredient() {
        return ingredient;
    }

    @Nullable public ItemStack getResult() {
        return result;
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
