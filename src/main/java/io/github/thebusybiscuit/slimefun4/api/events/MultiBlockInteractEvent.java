package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * This {@link Event} is called when a {@link Player} interacts with a {@link MultiBlock}.
 *
 * @author TheBusyBiscuit
 */
public class MultiBlockInteractEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final MultiBlock multiBlock;
    @Getter
    private final Block clickedBlock;
    @Getter
    private final BlockFace clickedFace;
    private boolean cancelled;

    public MultiBlockInteractEvent(Player p, MultiBlock mb, Block clicked, BlockFace face) {
        super(p);
        this.multiBlock = mb;
        this.clickedBlock = clicked;
        this.clickedFace = face;
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
