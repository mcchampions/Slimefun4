package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedParticle;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import javax.annotation.Nullable;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link TreeGrowthAccelerator} is an electrical machine that works similar to
 * the {@link CropGrowthAccelerator} but boosts the growth of nearby trees.
 *
 * @author TheBusyBiscuit
 *
 * @see CropGrowthAccelerator
 * @see AnimalGrowthAccelerator
 *
 */
public class TreeGrowthAccelerator extends AbstractGrowthAccelerator {
    private static final int ENERGY_CONSUMPTION = 24;
    private static final int RADIUS = 9;

    // We wanna strip the Slimefun Item id here
    private static final ItemStack organicFertilizer = ItemStackWrapper.wrap(SlimefunItems.FERTILIZER);

    public TreeGrowthAccelerator(
            ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public int getCapacity() {
        return 1024;
    }

    @Override
    protected void tick(Block b) {
        BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());

        if (getCharge(b.getLocation()) >= ENERGY_CONSUMPTION) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    Block block = b.getRelative(x, 0, z);

                    if (Tag.SAPLINGS.isTagged(block.getType())) {
                        boolean isGrowthBoosted = tryToBoostGrowth(b, inv, block);

                        if (isGrowthBoosted) {
                            // Finish this tick and wait for the next.
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean tryToBoostGrowth(Block machine, BlockMenu inv, Block sapling) {
        // On 1.17+ we can actually simulate bonemeal :O
        return applyBoneMeal(machine, sapling, inv);
    }

    private boolean applyBoneMeal(Block machine, Block sapling, BlockMenu inv) {
        for (int slot : getInputSlots()) {
            if (isFertilizer(inv.getItemInSlot(slot))) {
                removeCharge(machine.getLocation(), ENERGY_CONSUMPTION);

                sapling.applyBoneMeal(BlockFace.UP);

                inv.consumeItem(slot);
                sapling.getWorld()
                        .spawnParticle(
                                VersionedParticle.HAPPY_VILLAGER,
                                sapling.getLocation().add(0.5D, 0.5D, 0.5D),
                                4,
                                0.1F,
                                0.1F,
                                0.1F);
                return true;
            }
        }

        return false;
    }

    protected static boolean isFertilizer(@Nullable ItemStack item) {
        return SlimefunUtils.isItemSimilar(item, organicFertilizer, false, false);
    }
}
