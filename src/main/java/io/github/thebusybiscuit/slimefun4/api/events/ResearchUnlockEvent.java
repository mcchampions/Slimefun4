package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This {@link Event} is called whenever a {@link Player} unlocks a {@link Research}.
 *
 * @author TheBusyBiscuit
 *
 * @see Research
 *
 */
public class ResearchUnlockEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Player player;
    @Getter
    private final Research research;
    private boolean cancelled;

    public ResearchUnlockEvent(Player p, Research research) {
        super(!Bukkit.isPrimaryThread());

        Validate.notNull(p, "The Player cannot be null");
        Validate.notNull(research, "Research cannot be null");

        this.player = p;
        this.research = research;
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
