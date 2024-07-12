package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ClimbingPick;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * An {@link Event} that is called whenever a {@link Player} has
 * used a {@link ClimbingPick} on a climbable surface.
 *
 * @author Linox
 * @see ClimbingPick
 */
public class ClimbingPickLaunchEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private Vector velocity;
    @Getter
    private final ClimbingPick pick;
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final Block block;

    private boolean cancelled;

    @ParametersAreNonnullByDefault
    public ClimbingPickLaunchEvent(
            Player player, Vector velocity, ClimbingPick pick, ItemStack itemStack, Block block) {
        super(player);

        this.velocity = velocity;
        this.pick = pick;
        this.itemStack = itemStack;
        this.block = block;
    }

    /**
     * Use this to change the velocity {@link Vector} applied to the {@link Player}.
     *
     * @param velocity The {@link Vector} velocity to apply
     */
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
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
