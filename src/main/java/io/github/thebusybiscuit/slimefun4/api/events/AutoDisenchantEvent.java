package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * An {@link Event} that is called whenever an {@link AutoDisenchanter} has
 * disenchanted an {@link ItemStack}.
 *
 * @author poma123
 */
public class AutoDisenchantEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    /**
     * -- GETTER --
     *  This returns the
     *  that is being disenchanted.
     *
     */
    @Getter
    private final ItemStack item;
    private boolean cancelled;

    public AutoDisenchantEvent(ItemStack item) {
        super(true);

        this.item = item;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
