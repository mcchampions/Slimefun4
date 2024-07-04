package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link Event} is called whenever a {@link Player} tries to open the Slimefun Guide book.
 *
 * @author Linox
 *
 * @see SlimefunGuideMode
 */
public class SlimefunGuideOpenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Player player;
    @Getter
    private final ItemStack guide;
    private SlimefunGuideMode layout;
    private boolean cancelled;

    public SlimefunGuideOpenEvent(Player p, ItemStack guide, SlimefunGuideMode layout) {
        Validate.notNull(p, "The Player cannot be null");
        Validate.notNull(guide, "Guide cannot be null");
        Validate.notNull(layout, "Layout cannot be null");
        this.player = p;
        this.guide = guide;
        this.layout = layout;
    }

    /**
     * This returns the {@link SlimefunGuideMode} of the Slimefun Guide
     * that {@link Player} tries to open.
     *
     * @return The {@link SlimefunGuideMode}
     */
    
    public SlimefunGuideMode getGuideLayout() {
        return layout;
    }

    /**
     * Changes the {@link SlimefunGuideMode} that was tried to be opened with.
     *
     * @param layout
     *            The new {@link SlimefunGuideMode}
     */
    public void setGuideLayout(SlimefunGuideMode layout) {
        Validate.notNull(layout, "You must specify a layout that is not-null!");
        this.layout = layout;
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
