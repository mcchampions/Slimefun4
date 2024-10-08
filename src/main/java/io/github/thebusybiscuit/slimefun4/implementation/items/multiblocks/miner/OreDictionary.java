package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Simple interface to map ore blocks to their respective item(s).
 *
 * @author TheBusyBiscuit
 *
 */
interface OreDictionary {
    ItemStack getDrops(Material material, Random random);

    static OreDictionary getInstance() {
        return new OreDictionary17();
    }
}
