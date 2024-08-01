package io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.utils.VersionUtils;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * This {@link Listener} prevents any {@link SlimefunItem} from being used in a
 * brewing stand.
 *
 * @author VoidAngel
 * @author SoSeDiK
 * @author CURVX
 *
 */
public class BrewingStandListener implements SlimefunCraftingListener {
    public BrewingStandListener(Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPreBrew(InventoryClickEvent e) {
        Inventory clickedInventory = e.getClickedInventory();
        Inventory topInventory = VersionUtils.getTopInventory(e);

        if (clickedInventory != null
                && topInventory.getType() == InventoryType.BREWING
                && topInventory.getHolder() instanceof BrewingStand) {
            if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
                e.setCancelled(true);
                return;
            }

            if (clickedInventory.getType() == InventoryType.BREWING) {
                e.setCancelled(isUnallowed(SlimefunItem.getByItem(e.getCursor())));
            } else {
                e.setCancelled(isUnallowed(SlimefunItem.getByItem(e.getCurrentItem())));
            }

            if (e.getResult() == Result.DENY) {
                Slimefun.getLocalization().sendMessage(e.getWhoClicked(), "brewing_stand.not-working", true);
            }
        }
    }

    @EventHandler
    public void hopperOnBrew(InventoryMoveItemEvent e) {
        if (e.getDestination().getType() == InventoryType.BREWING && isUnallowed(e.getItem())) {
            e.setCancelled(true);
        }
    }
}
