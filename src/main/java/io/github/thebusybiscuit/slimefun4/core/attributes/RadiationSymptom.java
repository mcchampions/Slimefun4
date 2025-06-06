package io.github.thebusybiscuit.slimefun4.core.attributes;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.RadiationUtils;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedPotionEffectType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * An enum of potential radiation symptoms.
 * A symptom will be applied when the minExposure
 * threshold is reached on the {@link Player}'s
 * exposure level.
 * When the {@link Player} gets above the minExposure threshold
 * the {@link PotionEffect} will be applied.
 *
 * @author Semisol
 *
 * @see RadiationUtils
 */
public enum RadiationSymptom {
    SLOW(10, VersionedPotionEffectType.SLOWNESS, 3),
    WITHER_LOW(25, PotionEffectType.WITHER, 0),
    BLINDNESS(50, PotionEffectType.BLINDNESS, 4),
    WITHER_HIGH(75, PotionEffectType.WITHER, 3),
    IMMINENT_DEATH(100, VersionedPotionEffectType.INSTANT_DAMAGE, 49);

    private final int minExposure;
    private final PotionEffect potionEffect;

    RadiationSymptom(int minExposure, PotionEffectType type, int level) {
        this.minExposure = minExposure;
        this.potionEffect = new PotionEffect(
                type, Slimefun.getCfg().getOrSetDefault("options.radiation-update-interval", 1) * 20 + 20, level);
    }

    /**
     * This method applies the symptom to a player.
     *
     * @param p
     *            The player
     */
    public void apply(Player p) {
        p.addPotionEffect(potionEffect);
    }

    /**
     * This method returns if this symptom
     * should be applied.
     *
     * @param exposure
     *            Exposure level
     *
     * @return If the symptom should be applied
     */
    public boolean shouldApply(int exposure) {
        return exposure >= minExposure;
    }
}
