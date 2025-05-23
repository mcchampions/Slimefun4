package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosiveTool;
import java.util.List;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} is called when an {@link ExplosiveTool} is used to break blocks.
 *
 * @author GallowsDove
 *
 * @see ExplosiveTool
 *
 */
public class ExplosiveToolBreakBlocksEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final ItemStack itemInHand;
    @Getter
    private final ExplosiveTool explosiveTool;
    private final Block mainBlock;
    @Getter
    private final List<Block> additionalBlocks;
    private boolean cancelled;

    public ExplosiveToolBreakBlocksEvent(
            Player player, Block block, List<Block> blocks, ItemStack item, ExplosiveTool explosiveTool) {
        super(player);
        this.mainBlock = block;
        this.additionalBlocks = blocks;
        this.itemInHand = item;
        this.explosiveTool = explosiveTool;
    }

    /**
     * This returns the primary {@link Block} that was broken.
     * This {@link Block} triggered this {@link Event} and is not included
     * in {@link #getAdditionalBlocks()}.
     *
     * @return The primary broken {@link Block}
     */

    public Block getPrimaryBlock() {
        return this.mainBlock;
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
