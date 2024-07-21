package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.magical.talismans.Talisman;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link PlayerEvent} is called when a {@link Player} activates a {@link Talisman}
 *
 * @author cworldstar
 */
public class TalismanActivateEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Talisman talisman;
    private final ItemStack talismanItemStack;
    /**
     * -- SETTER --
     *  Only applies if
     *  is true.
     *
     * @param preventConsumption
     * 		Whether the {@link ItemStack} should not be consumed.
     */
    @Setter
    private boolean preventConsumption = false;
    private boolean cancelled = false;

    /**
     * @param player
     * 		The {@link Player} who activated the talisman.
     *
     * @param talisman
     * 		The {@link Talisman} that was activated.
     *
     * @param talismanItem
     * 		The {@link ItemStack} corresponding to the Talisman.
     */
    public TalismanActivateEvent(Player player, Talisman talisman, ItemStack talismanItem) {
        super(player);
        this.talisman = talisman;
        this.talismanItemStack = talismanItem;
    }

    /**
     * @return The {@link ItemStack} of the used {@link Talisman}.
     */
    public ItemStack getTalismanItem() {
        return this.talismanItemStack;
    }

    /**
     * Only applies if {@link Talisman#isConsumable()} is true.
     * Defaults to false.
     *
     * @return Whether the {@link ItemStack} should not be consumed.
     */
    public boolean preventsConsumption() {
        return this.preventConsumption;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
