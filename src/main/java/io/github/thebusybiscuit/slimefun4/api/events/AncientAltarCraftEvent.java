package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.AncientAltarListener;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AncientAltarTask;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} is fired before an item is dropped by an {@link AncientAltar}.
 * Cancelling this event will make the {@link AncientAltar} drop no item after the recipe is finished.
 *
 * @author Tweep
 *
 * @see AncientAltar
 * @see AncientAltarTask
 * @see AncientAltarListener
 */
public class AncientAltarCraftEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Block block;
    private ItemStack output;
    private boolean cancelled;

    /**
     * @param block  The altar {@link Block}
     * @param output The {@link ItemStack} that would be dropped by the ritual
     * @param player The {@link Player} that started the ritual.
     */
    public AncientAltarCraftEvent(ItemStack output, Block block, Player player) {
        super(player);

        this.block = block;
        this.output = output;
    }

    /**
     * This method returns the main altar's block {@link Block}
     *
     * @return the main altar's block {@link Block}
     */

    public Block getAltarBlock() {
        return block;
    }

    /**
     * This method returns the {@link ItemStack} that would be dropped by the {@link AncientAltar }
     *
     * @return the {@link ItemStack} that would be dropped by the {@link AncientAltar}
     */

    public ItemStack getItem() {
        return output;
    }

    /**
     * This method will change the item that would be dropped by the {@link AncientAltar}
     *
     * @param output
     *            being the {@link ItemStack} you want to change the item to.
     */
    public void setItem(ItemStack output) {
        this.output = output;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
