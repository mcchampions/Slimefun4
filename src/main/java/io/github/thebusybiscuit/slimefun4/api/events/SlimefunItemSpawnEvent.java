package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

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

    @Getter
    private Location location;
    @Getter
    private ItemStack itemStack;
    private boolean cancelled;
    @Getter
    private final ItemSpawnReason itemSpawnReason;
    private final Player player;

    @ParametersAreNonnullByDefault
    public SlimefunItemSpawnEvent(
            @Nullable Player player, Location location, ItemStack itemStack, ItemSpawnReason itemSpawnReason) {
        this.location = location;
        this.itemStack = itemStack;
        this.itemSpawnReason = itemSpawnReason;
        this.cancelled = false;
        this.player = player;
    }

    @ParametersAreNonnullByDefault
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


    /**
     * This sets the {@link Location} on where to drop this item.
     *
     * @param location
     *            The {@link Location} where to drop the {@link ItemStack}
     */
    public void setLocation(Location location) {
        Validate.notNull(location, "The Location cannot be null!");

        this.location = location;
    }

    /**
     * This method sets the {@link ItemStack} that should be dropped.
     *
     * @param itemStack
     *            The {@link ItemStack} to drop
     */
    public void setItemStack(ItemStack itemStack) {
        Validate.notNull(itemStack, "Cannot drop null.");
        Validate.isTrue(!itemStack.getType().isAir(), "Cannot drop air.");

        this.itemStack = itemStack;
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
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
