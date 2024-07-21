package io.github.thebusybiscuit.slimefun4.implementation.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

/**
 * This is basically a quickstart class for your very first {@link SlimefunItem}.
 * This class easily allows you to add one {@link ItemHandler} to your {@link SlimefunItem}.
 * <p>
 * You could use an {@link ItemUseHandler} for example to give your {@link SlimefunItem}
 * very basic right-click functionalities.
 *
 * @author TheBusyBiscuit
 *
 * @see ItemHandler
 * @see ItemUseHandler
 * @see SlimefunItem
 *
 * @param <T>
 *            The Type of {@link ItemHandler} to add to this {@link SlimefunItem}
 */
public abstract class SimpleSlimefunItem<T extends ItemHandler> extends SlimefunItem {
    protected SimpleSlimefunItem(
            ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    protected SimpleSlimefunItem(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public void preRegister() {
        addItemHandler(getItemHandler());
    }

    /**
     * This returns the {@link ItemHandler} that will be added to this {@link SlimefunItem}.
     *
     * @return The {@link ItemHandler} that should be added to this {@link SlimefunItem}
     */
    public abstract T getItemHandler();
}
