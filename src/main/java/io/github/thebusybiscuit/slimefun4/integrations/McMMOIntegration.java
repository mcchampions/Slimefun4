package io.github.thebusybiscuit.slimefun4.integrations;

import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.skills.SkillUtils;
import io.github.thebusybiscuit.slimefun4.api.events.AutoDisenchantEvent;
import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * This handles all integrations with {@link mcMMO}.
 *
 * @author TheBusyBiscuit
 *
 */
class McMMOIntegration implements Listener {
    private final Slimefun plugin;

    McMMOIntegration(Slimefun plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onBlockPlacerPlace(BlockPlacerPlaceEvent e) {
        // This registers blocks placed by the BlockPlacer as "player-placed"
        try {
            mcMMO.getPlaceStore().setTrue(e.getBlock());
        } catch (RuntimeException | LinkageError x) {
            IntegrationsManager.logError("mcMMO", x);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onItemSalvage(McMMOPlayerSalvageCheckEvent e) {
        // Prevent Slimefun items from being salvaged
        if (!isSalvageable(e.getSalvageItem())) {
            e.setCancelled(true);
            Slimefun.getLocalization().sendMessage(e.getPlayer(), "anvil.mcmmo-salvaging");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public static void onAutoDisenchant(AutoDisenchantEvent e) {
        try {
            SkillUtils.removeAbilityBuff(e.getItem());
        } catch (RuntimeException | LinkageError x) {
            IntegrationsManager.logError("mcMMO", x);
        }
    }

    /**
     * This method checks if an {@link ItemStack} can be salvaged or not.
     * We basically don't want players to salvage any {@link SlimefunItem} unless
     * it is a {@link VanillaItem}.
     *
     * @param item
     *            The {@link ItemStack} to check
     *
     * @return Whether this item can be safely salvaged
     */
    private static boolean isSalvageable(ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        return sfItem == null || sfItem instanceof VanillaItem;
    }
}
