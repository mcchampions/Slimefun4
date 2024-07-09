package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

// This class will be rewritten in the "Recipe Rewrite"
@Getter
public class MachineRecipe {

    private int ticks;
    private final ItemStack[] input;
    private final ItemStack[] output;

    public MachineRecipe(int seconds, ItemStack[] input, ItemStack[] output) {
        this.ticks = seconds * 2;
        this.input = input;
        this.output = output;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public MachineRecipe clone() {
        return new MachineRecipe(ticks / 2, input, output);
    }
}
