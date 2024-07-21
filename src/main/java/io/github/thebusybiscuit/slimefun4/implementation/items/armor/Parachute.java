package io.github.thebusybiscuit.slimefun4.implementation.items.armor;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.Jetpack;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.player.ParachuteTask;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link Parachute} is a {@link SlimefunItem} that can be equipped as a chestplate.
 * It allows you slowly glide to the ground while holding shift.
 * <p>
 * This class does not contain much code to see, check our the {@link ParachuteTask} class
 * for the actual logic behind this.
 *
 * @author TheBusyBiscuit
 *
 * @see ParachuteTask
 * @see Jetpack
 *
 */
public class Parachute extends SlimefunItem {
    public Parachute(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }
}
