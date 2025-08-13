package io.github.thebusybiscuit.slimefun4.implementation.items.medical;

import city.norain.slimefun4.compatibillty.CompatibilityUtil;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedPotionEffectType;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class MedicalSupply<T extends ItemHandler> extends SimpleSlimefunItem<T> {
    @Getter
    private final Set<PotionEffectType> curedEffects = new HashSet<>();
    private final int healAmount;

    protected MedicalSupply(
            ItemGroup itemGroup, int healAmount, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.healAmount = healAmount;

        curedEffects.add(PotionEffectType.POISON);
        curedEffects.add(PotionEffectType.WITHER);
        curedEffects.add(VersionedPotionEffectType.SLOWNESS);
        curedEffects.add(VersionedPotionEffectType.MINING_FATIGUE);
        curedEffects.add(PotionEffectType.WEAKNESS);
        curedEffects.add(VersionedPotionEffectType.NAUSEA);
        curedEffects.add(PotionEffectType.BLINDNESS);
        curedEffects.add(PotionEffectType.BAD_OMEN);
    }

    /**
     * This method clears any negative {@link PotionEffect} from the given {@link LivingEntity}.
     *
     * @param n The {@link LivingEntity} to clear the effects from.
     */
    public void clearNegativeEffects(LivingEntity n) {
        for (PotionEffectType effect : curedEffects) {
            if (n.hasPotionEffect(effect)) {
                n.removePotionEffect(effect);
            }
        }
    }

    /**
     * This method heals the given {@link LivingEntity} by the amount provided via the constructor.
     *
     * @param n The {@link LivingEntity} to heal
     */
    public void heal(LivingEntity n) {
        double health = n.getHealth() + healAmount;
        double maxHealth = n.getAttribute(CompatibilityUtil.getMaxHealth()).getValue();
        n.setHealth(Math.min(health, maxHealth));
    }
}
