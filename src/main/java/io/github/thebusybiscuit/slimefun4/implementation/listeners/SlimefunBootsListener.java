package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.EnderBoots;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.FarmerShoes;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.LongFallBoots;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.StomperBoots;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This {@link Listener} is responsible for handling all boots provided by
 * Slimefun, such as the {@link StomperBoots} or any {@link SlimefunArmorPiece} that
 * is a pair of boots and needs to listen to an {@link EntityDamageEvent}.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 *
 */
public class SlimefunBootsListener implements Listener {
    public SlimefunBootsListener(Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.FALL) {
            onFallDamage(e);
        }
    }

    @EventHandler
    public void onEnderPearlDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof EnderPearl && e.getEntity() instanceof Player p) {
            SlimefunItem boots = SlimefunItem.getByItem(p.getInventory().getBoots());

            if (boots instanceof EnderBoots && boots.canUse(p, true)) {
                e.setCancelled(true);
            }
        }
    }

    private void onFallDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            SlimefunItem boots = SlimefunItem.getByItem(p.getInventory().getBoots());

            if (boots != null) {
                // Check if the boots were researched
                if (!boots.canUse(p, true)) {
                    return;
                }

                if (boots instanceof StomperBoots) {
                    e.setCancelled(true);
                    StomperBoots.stomp(e);
                } else if (boots instanceof LongFallBoots longFallBoots) {
                    e.setCancelled(true);
                    longFallBoots.getSoundEffect().playAt(p.getLocation(), SoundCategory.PLAYERS);
                }
            }
        }
    }

    @EventHandler
    public void onTrample(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) {
            Block b = e.getClickedBlock();

            if (b != null && b.getType() == Material.FARMLAND) {
                Player p = e.getPlayer();
                SlimefunItem boots = SlimefunItem.getByItem(p.getInventory().getBoots());

                if (boots instanceof FarmerShoes && boots.canUse(p, true)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
