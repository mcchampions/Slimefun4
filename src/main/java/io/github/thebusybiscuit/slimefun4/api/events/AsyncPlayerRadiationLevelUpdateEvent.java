package io.github.thebusybiscuit.slimefun4.api.events;

import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} is fired while ticking the radiation level of a player, the event will determine how the player's radiation level update
 * Radiation level utils can be found in {@link io.github.thebusybiscuit.slimefun4.utils.RadiationUtils}
 *
 * @author m1919810
 *
 */
@Getter
public class AsyncPlayerRadiationLevelUpdateEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final int previousLevel;

    @Setter
    private int deltaLevel;


    private final boolean fullProtection;

    public AsyncPlayerRadiationLevelUpdateEvent(Player player, int previousLevel, int delta, boolean hasProtection) {
        super(player, !Bukkit.isPrimaryThread());

        this.previousLevel = previousLevel;
        this.deltaLevel = delta;
        this.fullProtection = hasProtection;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
