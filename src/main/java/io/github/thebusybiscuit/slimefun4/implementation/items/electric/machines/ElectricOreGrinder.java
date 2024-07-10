package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotHopperable;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.qscbm.slimefun4.slimefunitems.ASpeedableContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ElectricOreGrinder extends ASpeedableContainer implements RecipeDisplayItem, NotHopperable {

    @ParametersAreNonnullByDefault
    public ElectricOreGrinder(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public String getMachineIdentifier() {
        return "ELECTRIC_ORE_GRINDER";
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.IRON_PICKAXE);
    }
}
