package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This {@link Event} is called whenever a {@link Player} clicks to unlock a {@link Research}.
 * This is called before {@link Research#canUnlock(Player)}.
 * The {@link Event} is not called for {@link CheatSheetSlimefunGuide}.
 *
 * @author uiytt
 *
 * @see SurvivalSlimefunGuide
 *
 */
public class PlayerPreResearchEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Research research;
    private final SlimefunItem slimefunItem;
    private boolean cancelled;

    @ParametersAreNonnullByDefault
    public PlayerPreResearchEvent(Player p, Research research, SlimefunItem slimefunItem) {
        Validate.notNull(p, "The Player cannot be null");
        Validate.notNull(research, "Research cannot be null");
        Validate.notNull(slimefunItem, "SlimefunItem cannot be null");

        this.player = p;
        this.research = research;
        this.slimefunItem = slimefunItem;
    }

    
    public Player getPlayer() {
        return player;
    }

    
    public Research getResearch() {
        return research;
    }

    
    public SlimefunItem getSlimefunItem() {
        return slimefunItem;
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
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
