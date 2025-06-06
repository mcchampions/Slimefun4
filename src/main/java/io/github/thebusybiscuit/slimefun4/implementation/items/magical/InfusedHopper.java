package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.DoubleRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.VanillaInventoryDropHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * The {@link InfusedHopper} is a special kind of {@link Hopper} which teleports any
 * nearby {@link Item} to itself.
 * The radius can be configured in the config.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 * @see InfusedMagnet
 */
public class InfusedHopper extends SimpleSlimefunItem<BlockTicker> {
    private final ItemSetting<Boolean> silent = new ItemSetting<>(this, "silent", false);
    private final ItemSetting<Boolean> toggleable = new ItemSetting<>(this, "toggleable-with-redstone", false);
    private final ItemSetting<Double> radius = new DoubleRangeSetting(this, "radius", 0.1, 3.5, Double.MAX_VALUE);

    public InfusedHopper(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(silent, radius, toggleable);

        // Fixes #2895 - Make sure we drop all inventory contents
        addItemHandler(new VanillaInventoryDropHandler<>(org.bukkit.block.Hopper.class));
    }

    @Override
    public BlockTicker getItemHandler() {
        return new BlockTicker() {
            @Override
            public void tick(Block b, SlimefunItem sfItem, SlimefunBlockData data) {
                if (b.getType() != Material.HOPPER) {
                    // we're no longer a hopper, we were probably destroyed. skipping this tick.
                    Slimefun.getDatabaseManager().getBlockDataController().removeBlock(b.getLocation());
                    return;
                }

                // Check if this was enabled in the config
                if (toggleable.getValue()) {
                    Hopper hopper = (Hopper) b.getBlockData();

                    /*
                     * If the Hopper was disabled by a redstone signal,
                     * we just don't do anything.
                     */
                    if (!hopper.isEnabled()) {
                        return;
                    }
                }

                Location l = b.getLocation().add(0.5, 1.2, 0.5);
                double range = radius.getValue();
                boolean playSound = false;

                // Check for any nearby Items that can be picked up
                for (Entity item : b.getWorld().getNearbyEntities(l, range, range, range, n -> isValidItem(l, n))) {
                    item.setVelocity(new Vector(0, 0.1, 0));
                    item.teleportAsync(l);
                    playSound = true;
                }

                /*
                 * Play a sound if at least one item was teleported and
                 * the "silent" setting is set to false.
                 */
                if (playSound && !silent.getValue()) {
                    SoundEffect.INFUSED_HOPPER_TELEPORT_SOUND.playAt(b);
                }
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        };
    }

    private static boolean isValidItem(Location l, Entity entity) {
        if (entity instanceof Item item && entity.isValid()) {
            // Check if the item cannot be picked up or has the "no pickup" metadata
            return item.getPickupDelay() <= 0
                    && !SlimefunUtils.hasNoPickupFlag(item)
                    && item.getLocation().distanceSquared(l) > 0.25;
        }

        return false;
    }
}
