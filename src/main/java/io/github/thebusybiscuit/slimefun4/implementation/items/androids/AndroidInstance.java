package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import lombok.Getter;
import org.bukkit.block.Block;

public class AndroidInstance {

    @Getter
    private final ProgrammableAndroid android;
    private final Block b;

    public AndroidInstance(ProgrammableAndroid android, Block b) {
        this.android = android;
        this.b = b;
    }

    public Block getBlock() {
        return b;
    }
}
