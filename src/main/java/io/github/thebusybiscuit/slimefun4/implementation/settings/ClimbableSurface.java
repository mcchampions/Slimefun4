package io.github.thebusybiscuit.slimefun4.implementation.settings;

import io.github.thebusybiscuit.slimefun4.api.events.ClimbingPickLaunchEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.settings.DoubleRangeSetting;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ClimbingPick;
import org.bukkit.Material;

/**
 * This is an {@link ItemSetting} that manages the efficiency of climbing
 * a certain {@link Material} with the {@link ClimbingPick}.
 *
 * @author TheBusyBiscuit
 *
 * @see ClimbingPick
 * @see ClimbingPickLaunchEvent
 *
 */
public class ClimbableSurface extends DoubleRangeSetting {

    private final Material type;

    /**
     * This creates a new {@link ClimbableSurface} for the given {@link Material}.
     *
     * @param surface
     *            The {@link Material} of this surface
     * @param defaultValue
     *            The default launch amount
     */
    public ClimbableSurface(ClimbingPick climbingPick, Material surface, double defaultValue) {
        super(climbingPick, "launch-amounts." + surface.name(), 0, defaultValue, Double.MAX_VALUE);

        this.type = surface;
    }

    /**
     * This returns the {@link Material} of this surface.
     *
     * @return The {@link Material} of this surface
     */
    
    public Material getType() {
        return type;
    }
}
