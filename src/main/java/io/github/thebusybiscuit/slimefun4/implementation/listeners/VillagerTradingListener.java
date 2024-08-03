package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.SyntheticEmerald;
import me.qscbm.slimefun4.utils.VersionEventsUtils;
import me.qscbm.slimefun4.utils.VersionUtils;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

/**
 * This {@link Listener} prevents any {@link SlimefunItem} from being used to trade with
 * Villagers, with one exception being {@link SyntheticEmerald}.
 *
 * @author TheBusyBiscuit
 *
 */
public class VillagerTradingListener implements Listener {
    public VillagerTradingListener(Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPreTrade(InventoryClickEvent e) {
        Inventory clickedInventory = VersionEventsUtils.getClickedInventory(e);
        Inventory topInventory = VersionEventsUtils.getTopInventory(e);

        if (clickedInventory != null && topInventory.getType() == InventoryType.MERCHANT) {
            if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
                e.setCancelled(true);
                return;
            }

            if (clickedInventory.getType() == InventoryType.MERCHANT) {
                e.setCancelled(isUnallowed(SlimefunItem.getByItem(e.getCursor())));
            } else {
                e.setCancelled(isUnallowed(SlimefunItem.getByItem(e.getCurrentItem())));
            }

            if (e.getResult() == Result.DENY) {
                Slimefun.getLocalization().sendMessage(e.getWhoClicked(), "villagers.no-trading", true);
            }
        }
    }

    private boolean isUnallowed(@Nullable SlimefunItem item) {
        return item != null
                && !(item instanceof VanillaItem)
                && !(item instanceof SyntheticEmerald)
                && !item.isDisabled();
    }
}
