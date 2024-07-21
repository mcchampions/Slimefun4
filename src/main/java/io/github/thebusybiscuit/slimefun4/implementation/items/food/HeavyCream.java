package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link SlimefunItem} can be obtained by crafting, it's
 * used for various foods and recipes
 *
 * @author TheSilentPro
 */
public class HeavyCream extends SimpleSlimefunItem<ItemUseHandler> {
    public HeavyCream(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> block = e.getClickedBlock();

            if (block.isEmpty() || !block.get().getType().isInteractable()) {
                e.cancel();
            }
        };
    }
}
