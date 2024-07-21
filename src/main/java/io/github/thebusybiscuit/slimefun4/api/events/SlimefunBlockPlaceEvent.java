package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link Event} is fired whenever a {@link SlimefunItem} is placed as a {@link Block} in the world.
 *
 * @author J3fftw1
 */
public class SlimefunBlockPlaceEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Block blockPlaced;
    @Getter
    private final SlimefunItem slimefunItem;
    private final ItemStack placedItem;
    @Getter
    private final Player player;

    private boolean cancelled = false;

    /**
     * @param player       The {@link Player} who placed this {@link SlimefunItem}
     * @param placedItem   The {@link ItemStack} held by the {@link Player}
     * @param blockPlaced  The {@link Block} placed by the {@link Player}
     * @param slimefunItem The {@link SlimefunItem} within the {@link ItemStack}
     */
    public SlimefunBlockPlaceEvent(Player player, ItemStack placedItem, Block blockPlaced, SlimefunItem slimefunItem) {
        super();

        this.player = player;
        this.placedItem = placedItem;
        this.blockPlaced = blockPlaced;
        this.slimefunItem = slimefunItem;
    }

    /**
     * This gets the placed {@link ItemStack}.
     *
     * @return The placed {@link ItemStack}
     */
    public ItemStack getItemStack() {
        return placedItem;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
