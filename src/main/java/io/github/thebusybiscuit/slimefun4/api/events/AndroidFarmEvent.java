package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.implementation.items.androids.AndroidInstance;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.FarmerAndroid;
import javax.annotation.Nullable;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} is fired before a {@link FarmerAndroid} harvests a {@link Block}.
 * If this {@link Event} is cancelled, the {@link Block} will not be harvested.
 * <p>
 * The {@link Event} will still be fired for non-harvestable blocks.
 *
 * @author TheBusyBiscuit
 */
public class AndroidFarmEvent extends Event implements Cancellable {
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
    /**
     * -- GETTER --
     *  Whether this was invoked via an advanced farming action
     *
     * @return Whether it is advanced
     */
    @Getter
    private final boolean isAdvanced;
    private ItemStack drop;
    private boolean cancelled;

    /**
     * @param block      The harvested {@link Block}
     * @param android    The {@link AndroidInstance} that triggered this {@link Event}
     * @param isAdvanced Whether this is an advanced farming action
     * @param drop       The item to be dropped or null
     */
    public AndroidFarmEvent(
            Block block, AndroidInstance android, boolean isAdvanced, @Nullable ItemStack drop) {
        this.block = block;
        this.android = android;
        this.isAdvanced = isAdvanced;
        this.drop = drop;
    }

    /**
     * This returns the harvested item or null.
     *
     * @return The harvested item or null
     */
    @Nullable public ItemStack getDrop() {
        return drop;
    }

    /**
     * This will set the {@link ItemStack} result.
     *
     * @param drop The result or null
     */
    public void setDrop(@Nullable ItemStack drop) {
        this.drop = drop;
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
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
