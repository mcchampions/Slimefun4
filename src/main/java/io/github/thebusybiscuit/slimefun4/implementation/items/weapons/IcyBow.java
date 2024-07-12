package io.github.thebusybiscuit.slimefun4.implementation.items.weapons;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BowShootHandler;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedPotionEffectType;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * The {@link IcyBow} is a special kind of bow which slows down any
 * {@link LivingEntity} it hits.
 *
 * @author TheBusyBiscuit
 * @author martinbrom
 *
 */
public class IcyBow extends SlimefunBow {
    @ParametersAreNonnullByDefault
    public IcyBow(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe) {
        super(itemGroup, item, recipe);
    }

    
    @Override
    public BowShootHandler onShoot() {
        return (e, n) -> {
            if (n instanceof Player player) {
                // Fixes #3060 - Don't apply effects if the arrow was successfully blocked.
                if (player.isBlocking() && e.getFinalDamage() <= 0) {
                    return;
                }

                player.setFreezeTicks(60);
            }
            n.getWorld().playEffect(n.getLocation(), Effect.STEP_SOUND, Material.ICE);
            n.getWorld().playEffect(n.getEyeLocation(), Effect.STEP_SOUND, Material.ICE);
            n.addPotionEffect(new PotionEffect(VersionedPotionEffectType.SLOWNESS, 20 * 2, 10));
            n.addPotionEffect(new PotionEffect(VersionedPotionEffectType.JUMP_BOOST, 20 * 2, -10));
        };
    }
}
