package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import java.util.Random;
import javax.annotation.Nonnull;
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

    
    @ParametersAreNonnullByDefault
    ItemStack getDrops(Material material, Random random);

    static OreDictionary forVersion(MinecraftVersion version) {
        if (version.isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
            // MC 1.17 - 1.18
            return new OreDictionary17();
        } else {
            // MC 1.16
            return new OreDictionary16();
        }
    }
}
