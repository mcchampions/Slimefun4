package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Compressor extends MultiBlockMachine {
    public Compressor(ItemGroup itemGroup, SlimefunItemStack item) {
        super(
                itemGroup,
                item,
                new ItemStack[] {
                    null,
                    null,
                    null,
                    null,
                    new ItemStack(Material.NETHER_BRICK_FENCE),
                    null,
                    new ItemStack(Material.PISTON),
                    new CustomItemStack(Material.DISPENSER, "发射器 (朝上)"),
                    new ItemStack(Material.PISTON)
                },
                BlockFace.SELF);
    }

    @Override
    protected void registerDefaultRecipes(List<ItemStack> recipes) {
        recipes.add(new SlimefunItemStack(SlimefunItems.STONE_CHUNK, 4));
        recipes.add(new ItemStack(Material.COBBLESTONE));

        recipes.add(new ItemStack(Material.FLINT, 8));
        recipes.add(new ItemStack(Material.COBBLESTONE));

        recipes.add(new ItemStack(Material.COAL_BLOCK, 8));
        recipes.add(new SlimefunItemStack(SlimefunItems.CARBON, 9));

        recipes.add(new ItemStack(Material.CHARCOAL, 4));
        recipes.add(new ItemStack(Material.COAL));
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        return recipes.stream().map(items -> items[0]).collect(Collectors.toList());
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispBlock = b.getRelative(BlockFace.DOWN);
        BlockState state = dispBlock.getState(false);

        if (state instanceof Dispenser dispenser) {
            Inventory inv = dispenser.getInventory();

            for (ItemStack item : inv.getContents()) {
                for (ItemStack recipeInput : RecipeType.getRecipeInputs(this)) {
                    if (recipeInput != null && SlimefunUtils.isItemSimilar(item, recipeInput, true)) {
                        ItemStack output = RecipeType.getRecipeOutput(this, recipeInput);
                        Inventory outputInv = findOutputInventory(output, dispBlock, inv);
                        MultiBlockCraftEvent event = new MultiBlockCraftEvent(p, this, item, output);

                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }

                        if (outputInv != null) {
                            ItemStack removing = item.clone();
                            removing.setAmount(recipeInput.getAmount());
                            inv.removeItem(removing);

                            craft(p, event.getOutput(), dispBlock, inv);
                        } else {
                            Slimefun.getLocalization().sendMessage(p, "machines.full-inventory", true);
                        }

                        return;
                    }
                }
            }

            Slimefun.getLocalization().sendMessage(p, "machines.unknown-material", true);
        }
    }

    private static void craft(Player p, ItemStack output, Block dispenser, Inventory dispInv) {
        for (int i = 0; i < 4; i++) {
            int j = i;

            Slimefun.runSync(
                    () -> {
                        if (j < 3) {
                            if (j == 1) {
                                SoundEffect.COMPRESSOR_CRAFT_CONTRACT_SOUND.playFor(p);
                            } else {
                                SoundEffect.COMPRESSOR_CRAFT_EXTEND_SOUND.playFor(p);
                            }
                        } else {
                            handleCraftedItem(output, dispenser, dispInv);
                        }
                    },
                    i * 20L);
        }
    }
}
