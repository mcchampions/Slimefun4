package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.apache.commons.lang.Validate;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * An {@link Event} that is called whenever an {@link AutoEnchanter} is
 * enchanting an {@link ItemStack}.
 *
 * @author StarWishsama
 */
public class AsyncAutoEnchanterProcessEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    /**
     * -- GETTER --
     *  This returns the
     *  that is being enchanted.
     *
     */
    @Getter
    private final ItemStack item;
    /**
     * -- GETTER --
     *  This returns the
     *  that is being used enchanted book
     *
     */
    @Getter
    private final ItemStack enchantedBook;
    /**
     * -- GETTER --
     *  This returns the
     * 's
     *
     */
    @Getter
    private final BlockMenu menu;

    private boolean cancelled;

    public AsyncAutoEnchanterProcessEvent(
            ItemStack item, ItemStack enchantedBook, BlockMenu menu) {
        super(true);

        Validate.notNull(item, "The item to enchant cannot be null!");
        Validate.notNull(enchantedBook, "The enchanted book to enchant cannot be null!");
        Validate.notNull(menu, "The menu of auto-enchanter cannot be null!");

        this.item = item;
        this.enchantedBook = enchantedBook;
        this.menu = menu;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }

    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
