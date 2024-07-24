package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.VanillaInventoryDropHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Smeltery;
import io.papermc.lib.PaperLib;
import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dropper;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The {@link IgnitionChamber} is used to re-ignite a {@link Smeltery}.
 *
 * @author AtomicScience
 * @author TheBusyBiscuit
 *
 * @see Smeltery
 *
 */
public class IgnitionChamber extends SlimefunItem {
    private static final BlockFace[] ADJACENT_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };

    public IgnitionChamber(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(new VanillaInventoryDropHandler<>(Dropper.class));
    }

    /**
     * This triggers an {@link IgnitionChamber} to be used from the given {@link Smeltery}
     * block and {@link Player}.
     *
     * @param p
     *            The {@link Player} who triggered this action
     * @param smelteryBlock
     *            The {@link Dispenser} block of our {@link Smeltery}
     *
     * @return Whether the operation completed successfully.
     *         This will return <code>false</code> when there is no
     *         chamber or no flint and steel present
     */
    public static boolean useFlintAndSteel(Player p, Block smelteryBlock) {
        Inventory inv = findIgnitionChamber(smelteryBlock);

        // Check if there even is a chamber nearby
        if (inv == null) {
            return false;
        }

        // Check if the chamber contains a Flint and Steel
        if (inv.contains(Material.FLINT_AND_STEEL)) {
            ItemStack item = inv.getItem(inv.first(Material.FLINT_AND_STEEL));
            ItemMeta meta = item.getItemMeta();

            // Only damage the Flint and Steel if it isn't unbreakable.
            if (!meta.isUnbreakable()) {
                // Update the damage value
                ((Damageable) meta).setDamage(((Damageable) meta).getDamage() + 1);

                if (((Damageable) meta).getDamage() >= item.getType().getMaxDurability()) {
                    // The Flint and Steel broke
                    item.setAmount(0);
                    SoundEffect.IGNITION_CHAMBER_USE_FLINT_AND_STEEL_SOUND.playAt(smelteryBlock);
                } else {
                    item.setItemMeta(meta);
                }
            }

            SoundEffect.IGNITION_CHAMBER_USE_FLINT_AND_STEEL_SOUND.playAt(smelteryBlock);
            return true;
        } else {
            // Notify the Player there is a chamber but without any Flint and Steel
            Slimefun.getLocalization().sendMessage(p, "machines.ignition-chamber-no-flint", true);
            return false;
        }
    }

    private static @Nullable Inventory findIgnitionChamber(Block b) {
        for (BlockFace face : ADJACENT_FACES) {
            Block block = b.getRelative(face);

            if (block.getType() == Material.DROPPER
                    && StorageCacheUtils.getSfItem(block.getLocation()) instanceof IgnitionChamber) {
                BlockState state =
                        PaperLib.getBlockState(b.getRelative(face), false).getState();

                if (state instanceof Dropper dropper) {
                    return dropper.getInventory();
                }
            }
        }

        return null;
    }
}
