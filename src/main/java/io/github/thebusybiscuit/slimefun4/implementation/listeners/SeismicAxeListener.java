package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.SeismicAxe;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * This {@link Listener} is responsible for removing every {@link FallingBlock} that was
 * created using a {@link SeismicAxe}.
 *
 * @author TheBusyBiscuit
 *
 * @see SeismicAxe
 *
 */
public class SeismicAxeListener implements Listener {
    private final SeismicAxe seismicAxe;

    public SeismicAxeListener(Slimefun plugin, SeismicAxe seismicAxe) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.seismicAxe = seismicAxe;
    }

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent e) {
        if (seismicAxe == null || seismicAxe.isDisabled()) {
            return;
        }

        if (e.getEntity().getType() == EntityType.FALLING_BLOCK && e.getEntity().hasMetadata("seismic_axe")) {
            e.setCancelled(true);
            e.getEntity().removeMetadata("seismic_axe", Slimefun.instance());
            e.getEntity().remove();
        }
    }
}
