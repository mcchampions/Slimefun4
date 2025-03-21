package io.github.thebusybiscuit.slimefun4.implementation.tasks.armor;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.SolarHelmet;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link SolarHelmetTask} is responsible for handling {@link SolarHelmet} for generating power
 * and adding checks for if it has sunlight and if the player is actually wearing it.
 *
 * @author TheBusyBiscuit
 * @author martinbrom
 * @author Semisol
 */
public class SolarHelmetTask extends AbstractArmorTask {
    @Override
    protected void onPlayerTick(Player p, PlayerProfile profile) {
        if (hasSunlight(p)) {
            checkForSolarHelmet(p);
        }
    }

    private static void checkForSolarHelmet(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();
        SlimefunItem item = SlimefunItem.getByItem(helmet);

        if (item instanceof SolarHelmet solarHelmet && item.canUse(p, true)) {
            solarHelmet.rechargeItems(p);
        }
    }

    private static boolean hasSunlight(Player p) {
        World world = p.getWorld();

        if (world.getEnvironment() != Environment.NORMAL) {
            // The End and Nether have no sunlight
            return false;
        }

        return (world.getTime() < 12300 || world.getTime() > 23850)
                && p.getEyeLocation().getBlock().getLightFromSky() == 15;
    }
}
