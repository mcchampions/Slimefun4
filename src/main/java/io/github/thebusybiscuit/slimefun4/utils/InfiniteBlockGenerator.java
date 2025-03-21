package io.github.thebusybiscuit.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.MinerAndroid;

import java.util.function.Predicate;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockFormEvent;

/**
 * This enum holds various ways of infinite block generators.
 * The most prominent member of these is the standard Cobblestone Generator.
 * We use this enum for performance optimizations for the {@link MinerAndroid}.
 *
 * @author TheBusyBiscuit
 *
 */
public enum InfiniteBlockGenerator implements Predicate<Block> {
    /**
     * Your standard Cobblestone Generator with flowing lava and water.
     */
    COBBLESTONE_GENERATOR("COBBLESTONE"),

    /**
     * When lava flows onto a stationary water block it generates normal stone.
     */
    STONE_GENERATOR("STONE"),

    /**
     * The Basalt Generator (1.16+ only) allows you to generate infinite Basalt!
     */
    BASALT_GENERATOR("BASALT");

    private static final InfiniteBlockGenerator[] valuesCached = values();
    private static final BlockFace[] sameLevelFaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
    };

    private final Material material;

    InfiniteBlockGenerator(String type) {
        this.material = Material.matchMaterial(type);
    }

    /**
     * This returns the generated {@link Material} of this {@link InfiniteBlockGenerator}.
     * This method can return null if the associated {@link Material} is not available in the current
     * {@link MinecraftVersion}.
     *
     * @return The generated {@link Material} or null
     */
    @Nullable public Material getGeneratedMaterial() {
        return material;
    }

    /**
     * Similar to test block this tests whether this {@link InfiniteBlockGenerator}
     * exists at the given {@link Block}.
     *
     * @param b
     *            The {@link Block}
     *
     * @return Whether this {@link InfiniteBlockGenerator} exists at the given {@link Block}
     */
    @Override
    public boolean test(Block b) {
        /*
         * This will eliminate non-matching base materials If we
         * are on a version without Basalt, it will be null here and not match.
         */
        if (b.getType() == material) {
            switch (this) {
                case COBBLESTONE_GENERATOR -> {
                    return hasSurroundingMaterials(b, Material.WATER, Material.LAVA);
                }
                case STONE_GENERATOR -> {
                    if (b.getRelative(BlockFace.UP).getType() == Material.LAVA) {
                        return hasSurroundingMaterials(b, Material.WATER);
                    } else {
                        return false;
                    }
                }
                case BASALT_GENERATOR -> {
                    if (b.getRelative(BlockFace.DOWN).getType() == Material.SOUL_SOIL) {
                        return hasSurroundingMaterials(b, Material.LAVA, Material.BLUE_ICE);
                    } else {
                        return false;
                    }
                }
                default -> {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private static boolean hasSurroundingMaterials(Block b, Material... materials) {
        boolean[] matches = new boolean[materials.length];
        int count = 0;

        for (BlockFace face : sameLevelFaces) {
            Block neighbour = b.getRelative(face);
            Material neighbourType = neighbour.getType();

            for (int i = 0; i < materials.length; i++) {
                if (neighbourType == materials[i] && !matches[i]) {
                    matches[i] = true;
                    count++;
                    break;
                }
            }

            if (count == materials.length) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method calls a {@link BlockFormEvent} for this {@link InfiniteBlockGenerator}.
     * There are a few plugins who catch these events to inject custom {@link Material Materials} into
     * Cobblestone Generators, so we wanna give them the oppurtunity to catch this as well.
     *
     * @param block
     *            The {@link Block} where the liquid has solidified
     *
     * @return Our called {@link BlockFormEvent}
     */

    public static BlockFormEvent callEvent(Block block) {
        BlockState state = block.getState(false);
        BlockFormEvent event = new BlockFormEvent(block, state);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * This will attempt to find an {@link InfiniteBlockGenerator} at the given {@link Block}.
     *
     * @param b
     *            The {@link Block}
     *
     * @return An {@link InfiniteBlockGenerator} or null if none was found.
     */
    @Nullable public static InfiniteBlockGenerator findAt(Block b) {
        for (InfiniteBlockGenerator generator : valuesCached) {
            if (generator.test(b)) {
                return generator;
            }
        }

        return null;
    }
}
