package io.github.thebusybiscuit.slimefun4.implementation.resources;

import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.biomes.BiomeMap;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

/**
 * A {@link GEOResource} which consists of small chunks of Uranium.
 *
 * @author TheBusyBiscuit
 *
 */
class UraniumResource extends AbstractResource {
    private static final int DEFAULT_OVERWORLD_VALUE = 4;

    private final BiomeMap<Integer> biomes;

    UraniumResource() {
        super("uranium", "小块铀", SlimefunItems.SMALL_URANIUM, 2, true);

        // 1.18+ renamed most biomes
        biomes = getBiomeMap(this, "/biome-maps/uranium_v1.18.json");
    }

    @Override
    public int getDefaultSupply(Environment environment, Biome biome) {
        if (environment != Environment.NORMAL) {
            return 0;
        } else {
            return biomes.getOrDefault(biome, DEFAULT_OVERWORLD_VALUE);
        }
    }
}
