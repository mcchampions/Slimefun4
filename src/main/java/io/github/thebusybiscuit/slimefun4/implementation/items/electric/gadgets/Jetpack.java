package io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.player.JetpackTask;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * {@link JetBoots} allow you to fly up into the air.
 * You can find the actual behaviour in the {@link JetpackTask} class.
 *
 * @author TheBusyBiscuit
 *
 * @see JetBoots
 * @see JetpackTask
 *
 */
public class Jetpack extends SlimefunItem implements Rechargeable {

    @Getter
    private final double thrust;
    private final float capacity;

    @ParametersAreNonnullByDefault
    public Jetpack(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, double thrust, float capacity) {
        super(itemGroup, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);

        this.thrust = thrust;
        this.capacity = capacity;
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }
}
