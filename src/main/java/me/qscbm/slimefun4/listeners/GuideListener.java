package me.qscbm.slimefun4.listeners;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunGuideOpenEvent;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuideListener implements Listener {
    public GuideListener(Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public static void onClick(InventoryClickEvent e) {
        if (!e.isRightClick() && !e.isShiftClick()) {
            return;
        }
        Player p = (Player) e.getWhoClicked();

        if (tryOpenGuide(p, e, SlimefunGuideMode.SURVIVAL_MODE) == Event.Result.ALLOW) {
            if (p.isSneaking()) {
                SlimefunGuideSettings.openSettings(p, e.getCurrentItem());
            } else {
                openGuide(p, e, SlimefunGuideMode.SURVIVAL_MODE);
            }
        } else if (tryOpenGuide(p, e, SlimefunGuideMode.CHEAT_MODE) == Event.Result.ALLOW) {
            if (p.isSneaking()) {
                SlimefunGuideSettings.openSettings(
                        p,
                        p.hasPermission("slimefun.cheat.items")
                                ? e.getCurrentItem()
                                : SlimefunGuide.getItem(SlimefunGuideMode.SURVIVAL_MODE));
            } else {
                /*
                 * We rather just run the command here, all
                 * necessary permission checks will be handled there.
                 */
                p.chat("/sf cheat");
            }
        }
    }

    public static void openGuide(Player p, InventoryClickEvent e, SlimefunGuideMode layout) {
        SlimefunGuideOpenEvent event = new SlimefunGuideOpenEvent(p, e.getCurrentItem(), layout);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            e.setCancelled(true);
            SlimefunGuide.openGuide(p, event.getGuideLayout());
        }
    }

    public static Event.Result tryOpenGuide(Player p, InventoryClickEvent e, SlimefunGuideMode layout) {
        ItemStack item = e.getCurrentItem();
        if (SlimefunUtils.isItemSimilar(item, SlimefunGuide.getItem(layout), false, false)) {
            if (!Slimefun.getWorldSettingsService().isWorldEnabled(p.getWorld())) {
                Slimefun.getLocalization().sendMessage(p, "messages.disabled-item", true, msg -> {
                    if (item.hasItemMeta()) {
                        return msg.replace(
                                "%item_name%",
                                TextUtils.toPlainText(item.getItemMeta().getDisplayName()));
                    } else {
                        return msg;
                    }
                });
                return Event.Result.DENY;
            }

            return Event.Result.ALLOW;
        }

        return Event.Result.DEFAULT;
    }
}
