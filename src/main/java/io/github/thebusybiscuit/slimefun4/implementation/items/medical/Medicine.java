package io.github.thebusybiscuit.slimefun4.implementation.items.medical;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.utils.RadiationUtils;

import org.bukkit.inventory.ItemStack;

public class Medicine extends MedicalSupply<ItemConsumptionHandler> {
    public Medicine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, 8, item, recipeType, recipe);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> {
            p.setFireTicks(0);
            clearNegativeEffects(p);
            RadiationUtils.clearExposure(p);
            heal(p);
        };
    }
}
