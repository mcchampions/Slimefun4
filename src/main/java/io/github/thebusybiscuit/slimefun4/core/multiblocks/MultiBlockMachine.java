package io.github.thebusybiscuit.slimefun4.core.multiblocks;

import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.MultiBlockInteractionHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.OutputChest;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import lombok.Getter;
import me.qscbm.slimefun4.utils.QsConstants;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link MultiBlockMachine} is a {@link SlimefunItem} that is built in the {@link World}.
 * It holds recipes and a {@link MultiBlock} object which represents its structure.
 *
 * @author TheBusyBiscuit
 *
 * @see MultiBlock
 *
 */
public abstract class MultiBlockMachine extends SlimefunItem implements NotPlaceable, RecipeDisplayItem {
    @Getter
    protected final List<ItemStack[]> recipes;
    protected final List<ItemStack> displayRecipes;
    protected final MultiBlock multiblock;

    protected MultiBlockMachine(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            ItemStack[] recipe,
            ItemStack[] machineRecipes,
            BlockFace trigger) {
        super(itemGroup, item, RecipeType.MULTIBLOCK, recipe);
        this.recipes = new ArrayList<>();
        this.displayRecipes = new ArrayList<>();
        this.displayRecipes.addAll(Arrays.asList(machineRecipes));
        this.multiblock = new MultiBlock(this, convertItemStacksToMaterial(recipe), trigger);

        registerDefaultRecipes(displayRecipes);
    }

    protected MultiBlockMachine(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, BlockFace trigger) {
        this(itemGroup, item, recipe, QsConstants.EMPTY_ITEM_STACKS, trigger);
    }

    protected void registerDefaultRecipes(List<ItemStack> recipes) {
        // Override this method to register some default recipes
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        return displayRecipes;
    }

    public MultiBlock getMultiBlock() {
        return multiblock;
    }

    public void addRecipe(ItemStack[] input, ItemStack output) {
        recipes.add(input);
        recipes.add(new ItemStack[] {output});
    }

    public void clearRecipe() {
        recipes.clear();
    }

    @Override
    public void register(SlimefunAddon addon) {
        addItemHandler(getInteractionHandler());
        super.register(addon);
    }

    @Override
    public void postRegister() {
        Slimefun.getRegistry().getMultiBlocks().add(multiblock);
    }

    @Override
    public void load() {
        super.load();

        for (int i = 0; i < displayRecipes.size(); i += 2) {
            ItemStack inputStack = displayRecipes.get(i);
            ItemStack outputStack = null;
            if (displayRecipes.size() >= i + 2) {
                outputStack = displayRecipes.get(i + 1);
            }

            SlimefunItem inputItem = SlimefunItem.getByItem(inputStack);
            SlimefunItem outputItem = SlimefunItem.getByItem(outputStack);
            // If the input/output is not a Slimefun item or it's not disabled then it's valid.
            if ((inputItem == null || !inputItem.isDisabled()) && (outputItem == null || !outputItem.isDisabled())) {
                recipes.add(new ItemStack[] {inputStack});
                recipes.add(new ItemStack[] {outputStack});
            }
        }
    }

    protected MultiBlockInteractionHandler getInteractionHandler() {
        return (p, mb, b) -> {
            if (mb.equals(multiblock)) {
                if (canUse(p, true)
                        && Slimefun.getProtectionManager()
                                .hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK)) {
                    onInteract(p, b);
                }

                return true;
            } else {
                return false;
            }
        };
    }

    public abstract void onInteract(Player p, Block b);

    /**
     * Overloaded method for finding a potential output chest.
     * Fallbacks to the old system of putting the adding back into the dispenser.
     * Optional last argument Inventory placeCheckerInv is for a {@link MultiBlockMachine} that create
     * a dummy inventory to check if there's a space for the adding, i.e. Enhanced crafting table
     *
     * @param adding
     *            The {@link ItemStack} that should be added
     * @param dispBlock
     *            The {@link Block} of our {@link Dispenser}
     * @param dispInv
     *            The {@link Inventory} of our {@link Dispenser}
     *
     * @return The target {@link Inventory}
     */
    protected static @Nullable Inventory findOutputInventory(ItemStack adding, Block dispBlock, Inventory dispInv) {
        return findOutputInventory(adding, dispBlock, dispInv, dispInv);
    }

    protected static @Nullable Inventory findOutputInventory(
            ItemStack product, Block dispBlock, Inventory dispInv, Inventory placeCheckerInv) {
        Optional<Inventory> outputChest = OutputChest.findOutputChestFor(dispBlock, product);

        /*
         * This if-clause will trigger if no suitable output chest was found.
         * It's functionally the same as the old fit check for the dispenser,
         * only refactored.
         */
        if (outputChest.isEmpty() && InvUtils.fits(placeCheckerInv, product)) {
            return dispInv;
        } else {
            return outputChest.orElse(null);
        }
    }

    /**
     * This method handles an output {@link ItemStack} from the {@link MultiBlockMachine} which has a crafting delay
     *
     * @param outputItem
     *            A crafted {@link ItemStack} from {@link MultiBlockMachine}
     * @param block
     *            Main {@link Block} of our {@link Container} from {@link MultiBlockMachine}
     * @param blockInv
     *            The {@link Inventory} of our {@link Container}
     *
     */
    public static void handleCraftedItem(ItemStack outputItem, Block block, Inventory blockInv) {
        Inventory outputInv = findOutputInventory(outputItem, block, blockInv);

        if (outputInv != null) {
            outputInv.addItem(outputItem);
        } else {
            ItemStack rest = blockInv.addItem(outputItem).get(0);

            // fallback: drop item
            if (rest != null) {
                SlimefunUtils.spawnItem(block.getLocation(), rest, ItemSpawnReason.MULTIBLOCK_MACHINE_OVERFLOW, true);
            }
        }
    }

    private static Material[] convertItemStacksToMaterial(ItemStack[] items) {
        List<Material> materials = new ArrayList<>();

        for (ItemStack item : items) {
            if (item == null) {
                materials.add(null);
            } else if (item.getType() == Material.FLINT_AND_STEEL) {
                materials.add(Material.FIRE);
            } else {
                materials.add(item.getType());
            }
        }

        return materials.toArray(QsConstants.EMPTY_MATERIALS);
    }
}
