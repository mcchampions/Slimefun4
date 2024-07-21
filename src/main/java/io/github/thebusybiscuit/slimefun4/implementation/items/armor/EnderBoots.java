package io.github.thebusybiscuit.slimefun4.implementation.items.armor;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.entity.EnderPearl;
import org.bukkit.inventory.ItemStack;

/**
 * {@link EnderBoots} are a pair of boots which negate damage caused
 * by throwing an {@link EnderPearl}.
 *
 * @author TheBusyBiscuit
 *
 */
public class EnderBoots extends SlimefunItem {
    public EnderBoots(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }
}
