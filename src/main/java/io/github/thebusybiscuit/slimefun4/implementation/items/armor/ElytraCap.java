package io.github.thebusybiscuit.slimefun4.implementation.items.armor;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectiveArmor;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.ElytraImpactListener;
import io.github.thebusybiscuit.slimefun4.utils.UnbreakingAlgorithm;
import javax.annotation.Nullable;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link ElytraCap} negates damage taken when crashing into a wall using an elytra.
 *
 * @author Seggan
 *
 * @see ElytraImpactListener
 */
public class ElytraCap extends SlimefunArmorPiece implements DamageableItem, ProtectiveArmor {
    private final NamespacedKey key;

    public ElytraCap(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, null);

        key = new NamespacedKey(Slimefun.instance(), "elytra_armor");
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public void damageItem(Player p, @Nullable ItemStack item) {
        if (p.getGameMode() != GameMode.CREATIVE) {
            DamageableItem.super.damageItem(p, item);
        }
    }

    @Override
    public boolean evaluateUnbreakingEnchantment(int unbreakingLevel) {
        return UnbreakingAlgorithm.ARMOR.evaluate(unbreakingLevel);
    }

    @Override
    public ProtectionType[] getProtectionTypes() {
        return new ProtectionType[] {ProtectionType.FLYING_INTO_WALL};
    }

    @Override
    public boolean isFullSetRequired() {
        return false;
    }

    @Override
    public NamespacedKey getArmorSetId() {
        return key;
    }
}
