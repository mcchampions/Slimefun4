package io.github.thebusybiscuit.slimefun4.core.handlers;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.bakedlibs.dough.collections.LoopIterator;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.RainbowBlock;
import io.github.thebusybiscuit.slimefun4.utils.ColoredMaterial;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.GlassPane;

import java.util.Arrays;
import java.util.List;

/**
 * This is a {@link BlockTicker} that is exclusively used for Rainbow blocks.
 * On every tick it cycles through the {@link LoopIterator} and chooses the next {@link Material}
 * and sets itself to that.
 *
 * @author TheBusyBiscuit
 *
 * @see RainbowBlock
 *
 */
public class RainbowTickHandler extends BlockTicker {
    private final LoopIterator<Material> iterator;
    private final boolean glassPanes;
    private Material material;

    public RainbowTickHandler(List<Material> materials) {
        glassPanes = containsGlassPanes(materials);
        iterator = new LoopIterator<>(materials);
        material = iterator.next();
    }

    public RainbowTickHandler(Material... materials) {
        this(Arrays.asList(materials));
    }

    public RainbowTickHandler(ColoredMaterial material) {
        this(material.asList());
    }

    /**
     * This method checks whether a given {@link Material} array contains any {@link Material}
     * that would result in a {@link GlassPane} {@link BlockData}.
     * This is done to save performance, so we don't have to validate {@link BlockData} at
     * runtime.
     *
     * @param materials
     *            The {@link Material} Array to check
     *
     * @return Whether the array contained any {@link GlassPane} materials
     */
    private static boolean containsGlassPanes(List<Material> materials) {
        for (Material type : materials) {
            /*
            This BlockData is purely virtual and only created on startup, it should have
            no impact on performance, in fact it should save performance as it preloads
            the data but also saves heavy calls for other Materials
            */
            if (type.createBlockData() instanceof GlassPane) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        if (b.getType().isAir()) {
            /*
            The block was broken, setting the Material now would result in a
            duplication glitch
            */
            return;
        }

        if (glassPanes) {
            BlockData blockData = b.getBlockData();

            if (blockData instanceof GlassPane previousData) {
                BlockData block = material.createBlockData(bd -> {
                    if (bd instanceof GlassPane nextData) {
                        nextData.setWaterlogged(previousData.isWaterlogged());

                        for (BlockFace face : previousData.getAllowedFaces()) {
                            nextData.setFace(face, previousData.hasFace(face));
                        }
                    }
                });

                b.setBlockData(block, false);
                return;
            }
        }

        b.setType(material, false);
    }

    @Override
    public void uniqueTick() {
        material = iterator.next();
    }

    @Override
    public boolean isSynchronized() {
        return true;
    }
}
