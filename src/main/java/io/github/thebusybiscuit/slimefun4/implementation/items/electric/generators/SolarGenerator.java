package io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNet;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link SolarGenerator} is a simple {@link EnergyNetProvider} which generates energy if
 * it has direct contact with sunlight.
 * <p>
 * Some versions of the {@link SolarGenerator} will even generate energy at night, this is determined by
 * {@link #getNightEnergy()}.
 *
 * @author TheBusyBiscuit
 *
 * @see EnergyNet
 * @see EnergyNetProvider
 *
 */
public class SolarGenerator extends SlimefunItem implements EnergyNetProvider {

    private final ItemSetting<Boolean> useNightEnergyInOtherDimensions =
            new ItemSetting<>(this, "other-dimensions-use-night-energy", false);
    /**
     * -- GETTER --
     *  This method returns the amount of energy that this
     *  produces during the day.
     *
     */
    @Getter
    private final int dayEnergy;
    /**
     * -- GETTER --
     *  This method returns the amount of energy that this
     *  produces during the night.
     *
     */
    @Getter
    private final int nightEnergy;
    private final int capacity;

    @ParametersAreNonnullByDefault
    public SolarGenerator(
            ItemGroup itemGroup,
            int dayEnergy,
            int nightEnergy,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            int capacity) {
        super(itemGroup, item, recipeType, recipe);

        this.dayEnergy = dayEnergy;
        this.nightEnergy = nightEnergy;
        this.capacity = capacity;

        addItemSetting(useNightEnergyInOtherDimensions);
    }

    @ParametersAreNonnullByDefault
    public SolarGenerator(
            ItemGroup itemGroup,
            int dayEnergy,
            int nightEnergy,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe) {
        this(itemGroup, dayEnergy, nightEnergy, item, recipeType, recipe, 0);
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getGeneratedOutput(Location l, SlimefunBlockData data) {
        World world = l.getWorld();

        if (world.getEnvironment() != Environment.NORMAL) {
            if (useNightEnergyInOtherDimensions.getValue()) {
                return getNightEnergy();
            }

            return 0;
        } else {
            boolean isDaytime = isDaytime(world);

            // Performance optimization for daytime-only solar generators
            if (!isDaytime && getNightEnergy() < 1) {
                return 0;
            } else if (!world.isChunkLoaded(l.getBlockX() >> 4, l.getBlockZ() >> 4)
                    || l.getBlock().getLightFromSky() < 15) {
                return 0;
            } else {
                return isDaytime ? getDayEnergy() : getNightEnergy();
            }
        }
    }

    /**
     * This method returns whether a given {@link World} has daytime.
     * It will also return false if a thunderstorm is active in this world.
     *
     * @param world
     *            The {@link World} to check
     *
     * @return Whether the given {@link World} has daytime and no active thunderstorm
     */
    private boolean isDaytime(World world) {
        long time = world.getTime();
        return !world.hasStorm() && !world.isThundering() && (time < 12300 || time > 23850);
    }

    @Override
    public void preRegister() {
        super.preRegister();

        // This prevents Players from toggling the Daylight sensor
        BlockUseHandler handler = PlayerRightClickEvent::cancel;
        addItemHandler(handler);
    }
}
