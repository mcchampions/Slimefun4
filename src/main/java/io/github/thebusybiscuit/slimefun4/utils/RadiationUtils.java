package io.github.thebusybiscuit.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.api.player.StatusEffect;
import io.github.thebusybiscuit.slimefun4.core.attributes.RadiationSymptom;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

/**
 * This class is a basic wrapper around the
 * status effect.
 *
 * @author Semisol
 *
 * @see RadiationSymptom
 */
public final class RadiationUtils {
    private static final StatusEffect RADIATION_EFFECT =
            new StatusEffect(new NamespacedKey(Slimefun.instance(), "radiation"));
    private static final int MAX_EXPOSURE_LEVEL = 100;

    public static void clearExposure(Player p) {
        RADIATION_EFFECT.clear(p);
    }

    public static int getExposure(Player p) {
        return RADIATION_EFFECT.getLevel(p).orElse(0);
    }

    public static void addExposure(Player p, int exposure) {
        int level = Math.min(RADIATION_EFFECT.getLevel(p).orElse(0) + exposure, MAX_EXPOSURE_LEVEL);
        RADIATION_EFFECT.addPermanent(p, level);
    }

    public static void removeExposure(Player p, int exposure) {
        int level = Math.max(RADIATION_EFFECT.getLevel(p).orElse(0) - exposure, 0);
        RADIATION_EFFECT.addPermanent(p, level);
    }
}
