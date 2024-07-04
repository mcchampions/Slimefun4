package io.github.thebusybiscuit.slimefun4.implementation.operations;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link MachineOperation} represents a crafting process.
 *
 * @author TheBusyBiscuit
 *
 */
public class CraftingOperation implements MachineOperation {

    private final ItemStack[] ingredients;
    private final ItemStack[] results;

    private final int totalTicks;
    private int currentTicks = 0;

    public CraftingOperation(MachineRecipe recipe) {
        this(recipe.getInput(), recipe.getOutput(), recipe.getTicks());
    }

    public CraftingOperation(ItemStack[] ingredients, ItemStack[] results, int totalTicks) {
        Validate.notEmpty(ingredients, "The Ingredients array cannot be empty or null");
        Validate.notEmpty(results, "The results array cannot be empty or null");
        Validate.isTrue(
                totalTicks >= 0,
                "The amount of total ticks must be a positive integer or zero, received: " + totalTicks);

        this.ingredients = ingredients;
        this.results = results;
        this.totalTicks = totalTicks;
    }

    @Override
    public void addProgress(int num) {
        Validate.isTrue(num > 0, "Progress must be positive.");
        currentTicks += num;
    }

    
    public ItemStack[] getIngredients() {
        return ingredients;
    }

    
    public ItemStack[] getResults() {
        return results;
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
