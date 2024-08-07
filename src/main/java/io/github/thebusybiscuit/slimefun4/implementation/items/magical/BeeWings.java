package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.BeeWingsListener;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.player.BeeWingsTask;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link BeeWings} are a special form of the elytra which gives you a slow falling effect
 * when you approach the ground.
 *
 * @author beSnow
 * @author TheBusyBiscuit
 *
 * @see BeeWingsListener
 * @see BeeWingsTask
 *
 */
public class BeeWings extends SlimefunItem {
    public BeeWings(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }
}
