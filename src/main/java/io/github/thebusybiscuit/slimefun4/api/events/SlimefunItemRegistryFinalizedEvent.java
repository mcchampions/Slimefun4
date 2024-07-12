package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This {@link Event} is fired after {@link Slimefun} finishes loading the
 * {@link SlimefunItem} registry. We recommend listening to this event if you
 * want to register recipes using items from other addons.
 *
 * @author ProfElements
 */
public class SlimefunItemRegistryFinalizedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public SlimefunItemRegistryFinalizedEvent() {}

    
    public static HandlerList getHandlerList() {
        return handlers;
    }

    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
