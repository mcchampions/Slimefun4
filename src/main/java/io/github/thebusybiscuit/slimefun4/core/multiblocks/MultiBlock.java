package io.github.thebusybiscuit.slimefun4.core.multiblocks;

import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockInteractEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.MultiBlockInteractionHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

/**
 * A {@link MultiBlock} represents a structure build in a {@link World}.
 * A {@link MultiBlock} is often linked to a {@link MultiBlockMachine} and is used
 * to recognize that machine in a {@link MultiBlockInteractEvent}.
 *
 * @author TheBusyBiscuit
 * @author Liruxo
 *
 * @see MultiBlockMachine
 * @see MultiBlockInteractionHandler
 * @see MultiBlockInteractEvent
 *
 */
public class MultiBlock {
    private static final Set<Tag<Material>> SUPPORTED_TAGS = new HashSet<>();

    static {
        // Allow variations of different types of wood to be used
        SUPPORTED_TAGS.add(Tag.LOGS);
        SUPPORTED_TAGS.add(Tag.WOODEN_TRAPDOORS);
        SUPPORTED_TAGS.add(Tag.WOODEN_SLABS);
        SUPPORTED_TAGS.add(Tag.WOODEN_FENCES);
        SUPPORTED_TAGS.add(Tag.FIRE);
    }


    public static Set<Tag<Material>> getSupportedTags() {
        return SUPPORTED_TAGS;
    }

    private final SlimefunItem item;
    private final Material[] blocks;
    private final BlockFace trigger;
    private final boolean isSymmetric;

    public MultiBlock(SlimefunItem item, Material[] build, BlockFace trigger) {
        this.item = item;
        this.blocks = build;
        this.trigger = trigger;
        this.isSymmetric = isSymmetric(build);
    }


    public SlimefunItem getSlimefunItem() {
        return item;
    }

    private static boolean isSymmetric(Material[] blocks) {
        return blocks[0] == blocks[2] && blocks[3] == blocks[5] && blocks[6] == blocks[8];
    }


    public Material[] getStructure() {
        return blocks;
    }


    public BlockFace getTriggerBlock() {
        return trigger;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MultiBlock mb)) {
            return false;
        }

        if (trigger == mb.trigger && isSymmetric == mb.isSymmetric) {
            for (int i = 0; i < mb.getStructure().length; i++) {
                if (!compareBlocks(blocks[i], mb.getStructure()[i])) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item.getId(), Arrays.hashCode(blocks), trigger, isSymmetric);
    }

    private boolean compareBlocks(Material a, @Nullable Material b) {
        if (b != null) {
            for (Tag<Material> tag : SUPPORTED_TAGS) {
                if (tag.isTagged(b)) {
                    return tag.isTagged(a);
                }
            }

            // This ensures that the Industrial Miner is still recognized while operating
            if (a == Material.PISTON) {
                return b == Material.PISTON || b == Material.MOVING_PISTON;
            } else if (b == Material.PISTON) {
                return a == Material.MOVING_PISTON;
            }

            return b == a;
        }

        return true;
    }

    /**
     * This returns whether this {@link MultiBlock} is a symmetric structure or whether
     * the left and right side differ.
     *
     * @return Whether this {@link MultiBlock} is a symmetric structure
     */
    public boolean isSymmetric() {
        return isSymmetric;
    }

    @Override
    public String toString() {
        return "MultiBlock (" + item.getId() + ") {" + Arrays.toString(blocks) + "}";
    }
}
