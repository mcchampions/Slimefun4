package io.github.thebusybiscuit.slimefun4.implementation.settings;

import io.github.thebusybiscuit.slimefun4.api.events.ClimbingPickLaunchEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.settings.DoubleRangeSetting;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ClimbingPick;
import lombok.Getter;
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
@Getter
public class ClimbableSurface extends DoubleRangeSetting {

    /**
     * -- GETTER --
     *  This returns the
     *  of this surface.
     */
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

}
