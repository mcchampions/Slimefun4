package io.github.thebusybiscuit.slimefun4.api.events;

import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import javax.annotation.Nullable;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This {@link Event} is called when a {@link Player} crafts an item using a {@link MultiBlockMachine}.
 * Unlike the {@link MultiBlockInteractEvent}, this event only fires if an output to a craft is expected.
 * If this event is cancelled, ingredients will not be consumed and no output item results.
 *
 * @author char321
 * @author JustAHuman
 */
public class MultiBlockCraftEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final MultiBlockMachine machine;
    @Getter
    private final ItemStack[] input;
    @Getter
    private ItemStack output;
    private boolean cancelled;

    /**
     * Creates a new {@link MultiBlockCraftEvent}.
     *
     * @param p The player that crafts using a multiblock
     * @param machine The multiblock machine used to craft
     * @param input The input items of the craft
     * @param output The resulting item of the craft
     */
    public MultiBlockCraftEvent(Player p, MultiBlockMachine machine, ItemStack[] input, ItemStack output) {
        super(p);
        this.machine = machine;
        this.input = input;
        this.output = output;
    }

    /**
     * Creates a new {@link MultiBlockCraftEvent}.
     *
     * @param p The player that crafts using a multiblock
     * @param machine The multiblock machine used to craft
     * @param input The input item of the craft
     * @param output The resulting item of the craft
     */
    public MultiBlockCraftEvent(Player p, MultiBlockMachine machine, ItemStack input, ItemStack output) {
        this(p, machine, new ItemStack[] {input}, output);
    }

    /**
     * Sets the output of the craft. Keep in mind that this overwrites any existing output.
     *
     * @param output
     *            The new item for the event to produce.
     *
     * @return The previous {@link ItemStack} output that was replaced.
     */
    public @Nullable ItemStack setOutput(@Nullable ItemStack output) {
        ItemStack oldOutput = this.output;
        this.output = output;
        return oldOutput;
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
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }
}
