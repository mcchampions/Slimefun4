package io.github.thebusybiscuit.slimefun4.core.attributes;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.core.services.holograms.HologramsService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.HologramProjector;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * This {@link ItemAttribute} manages holograms.
 *
 * @author TheBusyBiscuit
 *
 * @see HologramProjector
 * @see HologramsService
 *
 */
public interface HologramOwner extends ItemAttribute {

    /**
     * This will update the hologram text for the given {@link Block}.
     *
     * @param b
     *            The {@link Block} to which the hologram belongs
     *
     * @param text
     *            The nametag for the hologram
     */
    default void updateHologram(Block b, String text) {
        Location loc = b.getLocation().add(getHologramOffset(b));
        Slimefun.getHologramsService().setHologramLabel(loc, ChatColors.color(text));
    }

    default void updateHologram(Block b, String text, Supplier<Boolean> abort) {
        if (Bukkit.isPrimaryThread()) {
            if (abort.get()) {
                return;
            }
            updateHologram(b, text);
            return;
        }

        Slimefun.runSync(() -> {
            if (abort.get()) {
                return;
            }
            updateHologram(b, text);
        });
    }

    /**
     * This will remove the hologram for the given {@link Block}.
     *
     * @param b
     *            The {@link Block} to which the hologram blocks
     */
    default void removeHologram(Block b) {
        Location loc = b.getLocation().add(getHologramOffset(b));
        Slimefun.getHologramsService().removeHologram(loc);
    }

    /**
     * This returns the offset of the hologram as a {@link Vector}.
     * This offset is applied to {@link Block#getLocation()} when spawning
     * the hologram.
     *
     * @param block
     *            The {@link Block} which serves as the origin point
     *
     * @return The hologram offset
     */
    
    default Vector getHologramOffset(Block block) {
        return Slimefun.getHologramsService().getDefaultOffset();
    }
}
