package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import java.util.Optional;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} is fired whenever slimefun drops an {@link ItemStack}.
 * Creating a custom {@link Event} for this allows other plugins to provide
 * compatibility with auto-pickup options or similar.
 *
 * @author TheBusyBiscuit
 *
 * @see ItemSpawnReason
 */
public class SlimefunItemSpawnEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /**
     * -- SETTER --
     *  This sets the
     *  on where to drop this item.
     *
     */
    @Setter
    @Getter
    private Location location;
    /**
     * -- SETTER --
     *  This method sets the
     *  that should be dropped.
     *
     */
    @Setter
    @Getter
    private ItemStack itemStack;
    private boolean cancelled;
    @Getter
    private final ItemSpawnReason itemSpawnReason;
    private final Player player;

    public SlimefunItemSpawnEvent(
            @Nullable Player player, Location location, ItemStack itemStack, ItemSpawnReason itemSpawnReason) {
        this.location = location;
        this.itemStack = itemStack;
        this.itemSpawnReason = itemSpawnReason;
        this.cancelled = false;
        this.player = player;
    }

    public SlimefunItemSpawnEvent(Location location, ItemStack itemStack, ItemSpawnReason itemSpawnReason) {
        this(null, location, itemStack, itemSpawnReason);
    }

    /**
     * Optionally returns the {@link Player} responsible for this spawn reason.
     *
     * @return The player responsible if applicable.
     */
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(player);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }
}
