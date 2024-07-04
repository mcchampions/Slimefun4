package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
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

    private final ItemStack item;
    private final ItemStack enchantedBook;
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

    /**
     * This returns the {@link ItemStack} that is being enchanted.
     *
     * @return The {@link ItemStack} that is being enchanted
     */
    
    public ItemStack getItem() {
        return item;
    }

    /**
     * This returns the {@link ItemStack} that is being used enchanted book
     *
     * @return The {@link ItemStack} that is being used enchanted book
     */
    
    public ItemStack getEnchantedBook() {
        return enchantedBook;
    }

    /**
     * This returns the {@link AutoEnchanter}'s {@link BlockMenu}
     *
     * @return The {@link BlockMenu} of {@link AutoEnchanter} that is enchanting item
     */
    
    public BlockMenu getMenu() {
        return menu;
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
