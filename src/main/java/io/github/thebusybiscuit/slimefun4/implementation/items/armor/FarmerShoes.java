package io.github.thebusybiscuit.slimefun4.implementation.items.armor;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link FarmerShoes} are just a simple pair of boots which allows a {@link Player} to walk
 * on {@link Farmland} without breaking it.
 *
 * @author TheBusyBiscuit
 *
 */
public class FarmerShoes extends SlimefunItem {
    public FarmerShoes(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }
}
