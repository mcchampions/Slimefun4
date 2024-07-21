package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.androids.AndroidInstance;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.MinerAndroid;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This {@link Event} is fired before a {@link MinerAndroid} mines a {@link Block}.
 * If this {@link Event} is cancelled, the {@link Block} will not be mined.
 *
 * @author poma123
 */
public class AndroidMineEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /**
     * -- GETTER --
     *  This method returns the mined
     *
     */
    @Getter
    private final Block block;
    /**
     * -- GETTER --
     *  This method returns the
     *  who
     *  triggered this
     *
     */
    @Getter
    private final AndroidInstance android;
    private boolean cancelled;

    /**
     * @param block
     *            The mined {@link Block}
     * @param android
     *            The {@link AndroidInstance} that triggered this {@link Event}
     */
    public AndroidMineEvent(Block block, AndroidInstance android) {
        this.block = block;
        this.android = android;
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
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
