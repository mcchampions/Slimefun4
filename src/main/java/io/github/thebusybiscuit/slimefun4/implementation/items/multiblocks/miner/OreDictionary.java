package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;
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

    static OreDictionary forVersion(MinecraftVersion version) {
        // MC 1.17 - 1.18
        return new OreDictionary17();
    }

    static OreDictionary getInstance() {
        return new OreDictionary17();
    }
}
