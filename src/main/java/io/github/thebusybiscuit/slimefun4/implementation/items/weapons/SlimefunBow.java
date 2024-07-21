package io.github.thebusybiscuit.slimefun4.implementation.items.weapons;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BowShootHandler;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents a {@link SlimefunItem} that is a Bow.
 * It comes with a {@link BowShootHandler} to handle actions that shall be performed
 * whenever an {@link Arrow} fired from this {@link SlimefunBow} hits a target.
 *
 * @author TheBusyBiscuit
 *
 */
public abstract class SlimefunBow extends SlimefunItem {
    protected SlimefunBow(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe) {
        super(itemGroup, item, RecipeType.MAGIC_WORKBENCH, recipe);
    }

    @Override
    public void preRegister() {
        super.preRegister();
        addItemHandler(onShoot());
    }


    public abstract BowShootHandler onShoot();
}
