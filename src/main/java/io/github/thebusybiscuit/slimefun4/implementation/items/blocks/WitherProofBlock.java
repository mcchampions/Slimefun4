package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.WitherProof;
import org.bukkit.block.Block;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.ItemStack;

/**
 * A quick and easy implementation of {@link SlimefunItem} that also implements the
 * interface {@link WitherProof}.
 * Items created with this class cannot be destroyed by a {@link Wither}.
 *
 * @author TheBusyBiscuit
 *
 * @see WitherProof
 *
 */
public class WitherProofBlock extends SlimefunItem implements WitherProof {
    public WitherProofBlock(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public WitherProofBlock(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public void onAttack(Block block, Wither wither) {
        // In this implementation we simply do nothing.
    }
}
