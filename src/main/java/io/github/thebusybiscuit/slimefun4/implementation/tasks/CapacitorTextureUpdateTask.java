package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import io.github.bakedlibs.dough.skins.PlayerHead;
import io.github.bakedlibs.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;

import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;

/**
 * This task is run whenever a {@link Capacitor} needs to update their texture.
 * <strong>This must be executed on the main {@link Server} {@link Thread}!</strong>
 *
 * @author TheBusyBiscuit
 */
public class CapacitorTextureUpdateTask implements Runnable {
    /**
     * The {@link Location} of the {@link Capacitor}.
     */
    private final Location l;

    /**
     * The level of how "full" this {@link Capacitor} is.
     * From 0.0 to 1.0.
     */
    private final double filledPercentage;

    public CapacitorTextureUpdateTask(Location l, double percentage) {
        this.l = l;
        this.filledPercentage = NumberUtils.clamp(0.0D, percentage, 1.0D);
    }

    @Override
    public void run() {
        Block b = l.getBlock();
        Material type = b.getType();

        // Ensure that this Block is still a Player Head
        if (type == Material.PLAYER_HEAD || type == Material.PLAYER_WALL_HEAD) {
            if (filledPercentage <= 0.25) {
                // 0-25% capacity
                setTexture(b, HeadTexture.CAPACITOR_25);
            } else if (filledPercentage <= 0.5) {
                // 25-50% capacity
                setTexture(b, HeadTexture.CAPACITOR_50);
            } else if (filledPercentage <= 0.75) {
                // 50-75% capacity
                setTexture(b, HeadTexture.CAPACITOR_75);
            } else {
                // 75-100% capacity
                setTexture(b, HeadTexture.CAPACITOR_100);
            }
        }
    }

    private static void setTexture(Block b, HeadTexture texture) {
        PlayerSkin skin = PlayerSkin.fromHashCode(texture.getUniqueId(), texture.getTexture());
        PlayerHead.setSkin(b, skin, true);
    }
}
