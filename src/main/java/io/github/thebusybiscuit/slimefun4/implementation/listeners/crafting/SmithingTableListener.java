package io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;

/**
 * This {@link Listener} prevents any {@link SlimefunItem} from being used in a
 * smithing table.
 *
 * @author Sefiraat
 * @author iTwins
 */
public class SmithingTableListener implements SlimefunCraftingListener {
    public SmithingTableListener(Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmith(SmithItemEvent e) {
        SlimefunItem sfItem = SlimefunItem.getByItem(e.getInventory().getContents()[materialSlot()]);
        if (sfItem != null && !sfItem.isUseableInWorkbench()) {
            e.setResult(Result.DENY);
            Slimefun.getLocalization().sendMessage(e.getWhoClicked(), "smithing_table.not-working", true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareSmith(PrepareSmithingEvent e) {
        if (e.getInventory().getResult() != null) {
            SlimefunItem sfItem = SlimefunItem.getByItem(e.getInventory().getContents()[materialSlot()]);
            if (sfItem != null && !sfItem.isUseableInWorkbench()) {
                e.setResult(null);
            }
        }
    }

    public static int materialSlot() {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20)) {
            return 2;
        }
        return 1;
    }
}
