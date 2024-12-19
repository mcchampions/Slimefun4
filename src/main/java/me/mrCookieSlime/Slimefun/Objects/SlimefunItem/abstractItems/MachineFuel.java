package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import java.util.function.Predicate;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

// This class will be rewritten in the "Recipe Rewrite"
public class MachineFuel implements Predicate<ItemStack> {
    /**
     * -- GETTER --
     *  This method returns how long this
     *  lasts.
     *  The result represents Slimefun ticks.
     *
     */
    @Getter
    private final int ticks;
    private final ItemStack fuel;
    @Getter
    private final ItemStack output;

    // For performance optimizations
    private final ItemStackWrapper wrapper;

    public MachineFuel(int seconds, ItemStack fuel) {
        this(seconds, fuel, null);
    }

    public MachineFuel(int seconds, ItemStack fuel, ItemStack output) {


        this.ticks = seconds << 1;
        this.fuel = fuel;
        this.wrapper = ItemStackWrapper.wrap(fuel);
        this.output = output;
    }

    public ItemStack getInput() {
        return fuel;
    }

    @Override
    public boolean test(ItemStack item) {
        return SlimefunUtils.isItemSimilar(item, wrapper, true);
    }
}
