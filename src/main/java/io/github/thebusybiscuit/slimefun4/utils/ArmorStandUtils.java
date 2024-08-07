package io.github.thebusybiscuit.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.core.services.holograms.HologramsService;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.HologramProjector;
import me.qscbm.slimefun4.utils.VersionUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

/**
 * This class holds utilities for {@link ArmorStand}, useful for classes
 * dealing with {@link ArmorStand}s that are not from {@link HologramsService}
 *
 * @author JustAHuman
 * @see HologramProjector
 * @see AncientPedestal
 */
public class ArmorStandUtils {
    private ArmorStandUtils() {
    }

    /**
     * Spawns an {@link ArmorStand} at the given {@link Location} with the given custom name
     * <br>
     * Set Properties: Invisible, Silent, Marker, No-Gravity, No Base Plate, Don't Remove When Far Away
     *
     * @param location   The {@link Location} to spawn the {@link ArmorStand}
     * @param customName The {@link String} custom name the {@link ArmorStand} should display
     * @return The spawned {@link ArmorStand}
     */
    public static ArmorStand spawnArmorStand(Location location, String customName) {
        ArmorStand armorStand = spawnArmorStand(location);
        armorStand.setCustomName(customName);
        armorStand.setCustomNameVisible(true);
        return armorStand;
    }

    /**
     * Spawns an {@link ArmorStand} at the given {@link Location}
     * <br>
     * Set Properties: Invisible, Silent, Marker, No-Gravity, No Base Plate, Don't Remove When Far Away
     *
     * @param location The {@link Location} to spawn the {@link ArmorStand}
     * @return The spawned {@link ArmorStand}
     */
    public static ArmorStand spawnArmorStand(Location location) {
        // The consumer method was moved from World to RegionAccessor in 1.20.2
        // Due to this, we need to use a rubbish workaround to support 1.20.1 and below
        // This causes flicker on these versions which sucks but not sure a better way around this right now.
        if (VersionUtils.getMinecraftVersion() < 20
            || (VersionUtils.getMinecraftVersion() == 20 && VersionUtils.getMinecraftPatchVersion() < 2)) {
            ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
            setupArmorStand(armorStand);
            return armorStand;
        }

        return location.getWorld().spawn(location, ArmorStand.class, ArmorStandUtils::setupArmorStand);
    }

    private static void setupArmorStand(ArmorStand armorStand) {
        armorStand.setVisible(false);
        armorStand.setSilent(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setRemoveWhenFarAway(false);
    }
}
