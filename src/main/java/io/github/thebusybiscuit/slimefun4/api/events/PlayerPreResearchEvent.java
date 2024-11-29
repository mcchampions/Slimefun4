package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

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

    @Getter
    private final Player player;
    @Getter
    private final Research research;
    @Getter
    private final SlimefunItem slimefunItem;
    private boolean cancelled;

    public PlayerPreResearchEvent(Player p, Research research, SlimefunItem slimefunItem) {
        this.player = p;
        this.research = research;
        this.slimefunItem = slimefunItem;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
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
