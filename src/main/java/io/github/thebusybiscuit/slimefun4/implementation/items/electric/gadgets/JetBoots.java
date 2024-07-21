package io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.player.JetBootsTask;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * {@link JetBoots} allow you to hover for a bit.
 * You can find the actual behaviour in the {@link JetBootsTask} class.
 *
 * @author TheBusyBiscuit
 *
 * @see Jetpack
 * @see JetBootsTask
 *
 */
public class JetBoots extends SlimefunItem implements Rechargeable {
    @Getter
    private final double speed;
    private final float capacity;

    public JetBoots(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, double speed, float capacity) {
        super(itemGroup, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);

        this.speed = speed;
        this.capacity = capacity;
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }
}
