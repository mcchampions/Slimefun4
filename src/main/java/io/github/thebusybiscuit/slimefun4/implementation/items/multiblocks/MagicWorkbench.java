package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MagicWorkbench extends AbstractCraftingTable {
    public MagicWorkbench(ItemGroup itemGroup, SlimefunItemStack item) {
        super(
                itemGroup,
                item,
                new ItemStack[] {
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new ItemStack(Material.BOOKSHELF),
                    new ItemStack(Material.CRAFTING_TABLE),
                    new ItemStack(Material.DISPENSER)
                },
                BlockFace.UP);
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block possibleDispener = locateDispenser(b);

        if (possibleDispener == null) {
            // How even...
            return;
        }

        BlockState state = possibleDispener.getState(false);

        if (state instanceof Dispenser dispenser) {
            Inventory inv = dispenser.getInventory();
            List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);

            for (ItemStack[] input : inputs) {
                if (isCraftable(inv, input)) {
                    ItemStack output =
                            RecipeType.getRecipeOutputList(this, input).clone();
                    MultiBlockCraftEvent event = new MultiBlockCraftEvent(p, this, input, output);

                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled() && SlimefunUtils.canPlayerUseItem(p, output, true)) {
                        craft(inv, possibleDispener, p, b, event.getOutput());
                    }

                    return;
                }
            }

            if (inv.isEmpty()) {
                Slimefun.getLocalization().sendMessage(p, "machines.inventory-empty", true);
            } else {
                Slimefun.getLocalization().sendMessage(p, "machines.pattern-not-found", true);
            }
        }
    }

    public static void craft(Inventory inv, Block dispenser, Player p, Block b, ItemStack output) {
        Inventory fakeInv = createVirtualInventory(inv);
        Inventory outputInv = findOutputInventory(output, dispenser, inv, fakeInv);

        if (outputInv != null) {
            SlimefunItem sfItem = SlimefunItem.getByItem(output);

            boolean waitCallback = false;
            if (sfItem instanceof SlimefunBackpack backpack) {
                waitCallback =
                        upgradeBackpack(p, inv, backpack, output, () -> startAnimation(p, b, inv, dispenser, output));
            }

            for (int j = 0; j < 9; j++) {
                if (inv.getContents()[j] != null && inv.getContents()[j].getType() != Material.AIR) {
                    if (inv.getContents()[j].getAmount() > 1) {
                        inv.setItem(j, new CustomItemStack(inv.getContents()[j], inv.getContents()[j].getAmount() - 1));
                    } else {
                        inv.setItem(j, null);
                    }
                }
            }

            if (!waitCallback) {
                startAnimation(p, b, inv, dispenser, output);
            }
        } else {
            Slimefun.getLocalization().sendMessage(p, "machines.full-inventory", true);
        }
    }

    private static void startAnimation(Player p, Block b, Inventory dispInv, Block dispenser, ItemStack output) {
        for (int j = 0; j < 4; j++) {
            int current = j;
            Slimefun.runSync(
                    () -> {
                        p.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
                        p.getWorld().playEffect(b.getLocation(), Effect.ENDER_SIGNAL, 1);

                        if (current < 3) {
                            SoundEffect.MAGIC_WORKBENCH_START_ANIMATION_SOUND.playAt(b);
                        } else {
                            SoundEffect.MAGIC_WORKBENCH_FINISH_SOUND.playAt(b);
                            handleCraftedItem(output, dispenser, dispInv);
                        }
                    },
                    j * 20L);
        }
    }

    private static Block locateDispenser(Block b) {
        Block block = null;

        if (b.getRelative(1, 0, 0).getType() == Material.DISPENSER) {
            block = b.getRelative(1, 0, 0);
        } else if (b.getRelative(0, 0, 1).getType() == Material.DISPENSER) {
            block = b.getRelative(0, 0, 1);
        } else if (b.getRelative(-1, 0, 0).getType() == Material.DISPENSER) {
            block = b.getRelative(-1, 0, 0);
        } else if (b.getRelative(0, 0, -1).getType() == Material.DISPENSER) {
            block = b.getRelative(0, 0, -1);
        }

        return block;
    }

    private static boolean isCraftable(Inventory inv, ItemStack[] recipe) {
        for (int j = 0; j < inv.getContents().length; j++) {
            if (!SlimefunUtils.isItemSimilar(inv.getContents()[j], recipe[j], true, true, false)) {
                if (SlimefunItem.getByItem(recipe[j]) instanceof SlimefunBackpack) {
                    if (!SlimefunUtils.isItemSimilar(inv.getContents()[j], recipe[j], false, true, false)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }
}
