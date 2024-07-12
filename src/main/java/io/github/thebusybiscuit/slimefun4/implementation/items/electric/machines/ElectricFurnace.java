package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotHopperable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import javax.annotation.ParametersAreNonnullByDefault;

import me.qscbm.slimefun4.slimefunitems.machines.ASpeedableContainer;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

/**
 * The {@link ElectricFurnace} is an electric version of the {@link Furnace}.
 * As the name would probably suggest.
 *
 * @author TheBusyBiscuit
 *
 */
public class ElectricFurnace extends ASpeedableContainer implements NotHopperable {
    @ParametersAreNonnullByDefault
    public ElectricFurnace(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void registerDefaultRecipes() {
        Slimefun.getMinecraftRecipeService().subscribe(snapshot -> {
            for (FurnaceRecipe recipe : snapshot.getRecipes(FurnaceRecipe.class)) {
                RecipeChoice choice = recipe.getInputChoice();

                if (choice instanceof MaterialChoice materialChoice) {
                    for (Material input : materialChoice.getChoices()) {
                        registerRecipe(4, new ItemStack[] {new ItemStack(input)}, new ItemStack[] {recipe.getResult()});
                    }
                }
            }
        });
    }

    @Override
    public String getMachineIdentifier() {
        return "ELECTRIC_FURNACE";
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.FLINT_AND_STEEL);
    }
}
