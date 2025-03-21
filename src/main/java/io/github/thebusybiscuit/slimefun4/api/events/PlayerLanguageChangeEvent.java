package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.core.services.LocalizationService;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} gets called when a {@link Player} has switched their {@link Language}.
 *
 * @author TheBusyBiscuit
 * @see Language
 * @see LocalizationService
 */
public class PlayerLanguageChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final Player player;
    private final Language from;
    private final Language to;

    public PlayerLanguageChangeEvent(Player p, Language from, Language to) {
        player = p;
        this.from = from;
        this.to = to;
    }

    /**
     * This returns the {@link Language} that this {@link Player} was using before.
     *
     * @return The previous {@link Language} of our {@link Player}
     */

    public Language getPreviousLanguage() {
        return from;
    }

    /**
     * This returns the {@link Language} that this {@link Player} wants to switch to.
     *
     * @return The new {@link Language}
     */

    public Language getNewLanguage() {
        return to;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
