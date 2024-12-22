package io.github.thebusybiscuit.slimefun4.implementation.listeners.entity;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.WitherProof;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link Listener} is responsible for implementing the functionality of blocks that
 * were marked as {@link WitherProof} to not be destroyed by a {@link Wither}.
 *
 * @author TheBusyBiscuit
 *
 * @see WitherProof
 *
 */
public class WitherListener implements Listener {
    public WitherListener(Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWitherDestroy(EntityChangeBlockEvent e) {
        if (e.getEntity().getType() == EntityType.WITHER) {
            BlockDataController controller = Slimefun.getDatabaseManager().getBlockDataController();
            Block block = e.getBlock();
            SlimefunBlockData blockData = controller.getBlockDataFromCache(block.getLocation());
            SlimefunItem item = blockData == null ? null : SlimefunItem.getById(blockData.getSfId());

            // Hardened Glass is excluded from here
            if (item instanceof WitherProof witherProofBlock
                    && !item.getId().equals(SlimefunItems.HARDENED_GLASS.getItemId())) {
                e.setCancelled(true);
                witherProofBlock.onAttack(block, (Wither) e.getEntity());
                return;
            }

            if (item != null) {
                controller.removeBlock(blockData.getLocation());
                block.setType(Material.AIR);

                for (ItemStack drop : item.getDrops()) {
                    if (drop != null && !drop.getType().isAir()) {
                        block.getWorld().dropItemNaturally(block.getLocation(), drop);
                    }
                }
            }
        }
    }
}
