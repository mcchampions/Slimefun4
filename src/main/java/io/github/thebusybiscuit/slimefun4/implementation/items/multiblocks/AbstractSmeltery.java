package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * An abstract super class for the {@link Smeltery} and {@link MakeshiftSmeltery}.
 *
 * @author TheBusyBiscuit
 *
 */
abstract class AbstractSmeltery extends MultiBlockMachine {
    protected AbstractSmeltery(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, BlockFace trigger) {
        super(itemGroup, item, recipe, trigger);
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block possibleDispenser = b.getRelative(BlockFace.DOWN);
        BlockState state = possibleDispenser.getState(false);

        if (state instanceof Dispenser dispenser) {
            Inventory inv = dispenser.getInventory();
            List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);

            for (int i = 0; i < inputs.size(); i++) {
                if (canCraft(inv, inputs, i)) {
                    ItemStack output =
                            RecipeType.getRecipeOutputList(this, inputs.get(i)).clone();
                    MultiBlockCraftEvent event = new MultiBlockCraftEvent(p, this, inputs.get(i), output);

                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled() && SlimefunUtils.canPlayerUseItem(p, output, true)) {
                        Inventory outputInv = findOutputInventory(output, possibleDispenser, inv);

                        if (outputInv != null) {
                            craft(p, b, inv, inputs.get(i), event.getOutput(), outputInv);
                        } else {
                            Slimefun.getLocalization().sendMessage(p, "machines.full-inventory", true);
                        }
                    }

                    return;
                }
            }

            Slimefun.getLocalization().sendMessage(p, "machines.unknown-material", true);
        }
    }

    private static boolean canCraft(Inventory inv, List<ItemStack[]> inputs, int i) {
        for (ItemStack expectedInput : inputs.get(i)) {
            if (expectedInput != null) {
                for (int j = 0; j < inv.getContents().length; j++) {
                    if (j == (inv.getContents().length - 1)
                            && !SlimefunUtils.isItemSimilar(inv.getContents()[j], expectedInput, true)) {
                        return false;
                    } else if (SlimefunUtils.isItemSimilar(inv.getContents()[j], expectedInput, true)) {
                        break;
                    }
                }
            }
        }

        return true;
    }

    protected void craft(Player p, Block b, Inventory inv, ItemStack[] recipe, ItemStack output, Inventory outputInv) {
        for (ItemStack removing : recipe) {
            if (removing != null) {
                InvUtils.removeItem(
                        inv, removing.getAmount(), true, stack -> SlimefunUtils.isItemSimilar(stack, removing, true));
            }
        }

        outputInv.addItem(output);
        SoundEffect.SMELTERY_CRAFT_SOUND.playAt(b);
        p.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
    }
}
