package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import com.xzavier0722.mc.plugin.slimefun4.storage.callback.IAsyncReadCallback;
import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.Optional;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This abstract super class is responsible for some utility methods for machines which
 * are capable of upgrading backpacks.
 *
 * @author TheBusyBiscuit
 *
 * @see EnhancedCraftingTable
 * @see MagicWorkbench
 * @see ArmorForge
 *
 */
abstract class AbstractCraftingTable extends MultiBlockMachine {
    AbstractCraftingTable(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, BlockFace trigger) {
        super(itemGroup, item, recipe, trigger);
    }

    protected Inventory createVirtualInventory(Inventory inv) {
        Inventory fakeInv = Bukkit.createInventory(null, 9, "Fake Inventory");

        for (int j = 0; j < inv.getContents().length; j++) {
            ItemStack stack = inv.getContents()[j];

            /*
             * Fixes #2103 - Properly simulating the consumption
             * (which may leave behind empty buckets or glass bottles)
             */
            if (stack != null) {
                stack = stack.clone();
                ItemUtils.consumeItem(stack, true);
            }

            fakeInv.setItem(j, stack);
        }

        return fakeInv;
    }

    // Return: true if upgrade from existing backpack, else false
    protected boolean upgradeBackpack(
            Player p, Inventory inv, SlimefunBackpack backpack, ItemStack output, Runnable onReadyCb) {
        ItemStack input = null;

        var contents = inv.getContents();
        for (int j = 0; j < 9; j++) {
            var item = contents[j];
            if (item != null
                    && item.getType() != Material.AIR
                    && SlimefunItem.getByItem(item) instanceof SlimefunBackpack) {
                input = inv.getContents()[j];
                break;
            }
        }

        // Fixes #2574 - Carry over the Soulbound status
        if (SlimefunUtils.isSoulbound(input)) {
            SlimefunUtils.setSoulbound(output, true);
        }

        int size = backpack.getSize();
        Optional<String> id = retrieveUuid(input);

        if (id.isPresent()) {
            Slimefun.getDatabaseManager()
                    .getProfileDataController()
                    .getBackpackAsync(id.get(), new IAsyncReadCallback<>() {
                        @Override
                        public boolean runOnMainThread() {
                            return true;
                        }

                        @Override
                        public void onResult(PlayerBackpack result) {
                            result.setSize(size);
                            PlayerBackpack.bindItem(output, result);
                            onReadyCb.run();
                        }
                    });
            return true;
        } else {
            id = retrieveID(input);
            if (id.isPresent()) {
                Slimefun.getDatabaseManager()
                        .getProfileDataController()
                        .getBackpackAsync(p, Integer.parseInt(id.get()), new IAsyncReadCallback<>() {
                            @Override
                            public boolean runOnMainThread() {
                                return true;
                            }

                            @Override
                            public void onResult(PlayerBackpack result) {
                                result.setSize(size);
                                PlayerBackpack.bindItem(output, result);
                                onReadyCb.run();
                            }
                        });
                return true;
            }
        }
        return false;
    }

    private Optional<String> retrieveID(@Nullable ItemStack backpack) {
        if (backpack != null) {
            for (String line : backpack.getItemMeta().getLore()) {
                if (line.startsWith("§7ID: ") && line.contains("#")) {
                    return Optional.of(CommonPatterns.HASH.split(line.replace("§7ID: ", ""))[1]);
                }
            }
        }

        return Optional.empty();
    }

    private Optional<String> retrieveUuid(@Nullable ItemStack backpack) {
        if (backpack == null) {
            return Optional.empty();
        }

        return PlayerBackpack.getBackpackUUID(backpack.getItemMeta());
    }
}
