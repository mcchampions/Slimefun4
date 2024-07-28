package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.VanillaInventoryDropHandler;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link OutputChest} can be used to capture the output items from a {@link MultiBlockMachine}.
 *
 * @author TheBusyBiscuit
 *
 * @see MultiBlockMachine
 *
 */
public class OutputChest extends SlimefunItem {
    private static final BlockFace[] possibleFaces = {
        BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };

    public OutputChest(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(new VanillaInventoryDropHandler<>(Chest.class));
    }

    public static Optional<Inventory> findOutputChestFor(Block b, ItemStack item) {
        for (BlockFace face : possibleFaces) {
            Block potentialOutput = b.getRelative(face);

            // Check if the target block is a Chest
            if (potentialOutput.getType() == Material.CHEST) {
                SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(potentialOutput.getLocation());

                // Fixes #3012 - Check if the OutputChest is not disabled here.
                if (slimefunItem instanceof OutputChest && !slimefunItem.isDisabledIn(b.getWorld())) {
                    // Found the output chest! Now, let's check if we can fit the product in it.
                    BlockState state =
                            potentialOutput.getState(false);

                    if (state instanceof Chest chest) {
                        Inventory inv = chest.getInventory();

                        // Check if the Item fits into that inventory.
                        if (InvUtils.fits(inv, item)) {
                            return Optional.of(inv);
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }
}
