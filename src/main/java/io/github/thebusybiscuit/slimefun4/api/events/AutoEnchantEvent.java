package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link Event} that is called whenever an {@link AutoEnchanter} is trying to enchant
 * an {@link ItemStack}.
 *
 * @author WalshyDev
 *
 * @see AutoDisenchantEvent
 */
public class AutoEnchantEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final ItemStack item;
    private boolean cancelled;

    public AutoEnchantEvent(ItemStack item) {
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
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
