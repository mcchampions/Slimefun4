package io.github.thebusybiscuit.slimefun4.implementation.items.medical;

import city.norain.slimefun4.compatibillty.VersionedAttribute;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedPotionEffectType;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * A {@link Bandage} or Rag is a medical supply which heals the {@link Player} and extinguishes
 * fire.
 *
 * @author TheBusyBiscuit
 */
public class Bandage extends SimpleSlimefunItem<ItemUseHandler> {
    private final int healingLevel;

    public Bandage(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput,
            int healingLevel) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);

        this.healingLevel = healingLevel;
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();

            // Player is neither burning nor injured
            if (p.getFireTicks() <= 0
                && p.getHealth()
                   >= p.getAttribute(VersionedAttribute.getMaxHealth()).getValue()) {
                return;
            }

            if (p.getGameMode() != GameMode.CREATIVE) {
                ItemUtils.consumeItem(e.getItem(), false);
            }

            p.getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.WHITE_WOOL);
            p.addPotionEffect(new PotionEffect(VersionedPotionEffectType.INSTANT_HEALTH, 1, healingLevel));
            p.setFireTicks(0);

            e.cancel();
        };
    }
}
