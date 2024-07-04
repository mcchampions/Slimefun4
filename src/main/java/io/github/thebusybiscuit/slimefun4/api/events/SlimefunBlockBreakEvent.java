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
 * This {@link Event} is fired whenever a {@link SlimefunItem} placed as a {@link Block} in the world is broken.
 *
 * @author J3fftw1
 */
public class SlimefunBlockBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Block blockBroken;
    @Getter
    private final SlimefunItem slimefunItem;
    @Getter
    private final ItemStack heldItem;
    @Getter
    private final Player player;

    private boolean cancelled = false;

    /**
     * @param player       The {@link Player} who broke this {@link SlimefunItem}
     * @param heldItem     The {@link ItemStack} held by the {@link Player}
     * @param blockBroken  The {@link Block} broken by the {@link Player}
     * @param slimefunItem The {@link SlimefunItem} within the {@link ItemStack}
     */
    @ParametersAreNonnullByDefault
    public SlimefunBlockBreakEvent(Player player, ItemStack heldItem, Block blockBroken, SlimefunItem slimefunItem) {
        super();

        this.player = player;
        this.heldItem = heldItem;
        this.blockBroken = blockBroken;
        this.slimefunItem = slimefunItem;
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
