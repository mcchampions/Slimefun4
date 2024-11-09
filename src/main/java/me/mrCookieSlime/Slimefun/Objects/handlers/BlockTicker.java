package me.mrCookieSlime.Slimefun.Objects.handlers;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataConfigWrapper;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import java.util.Optional;

import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.block.Block;

public abstract class BlockTicker implements ItemHandler {
    @Getter
    private final boolean universal;

    protected boolean unique = true;

    public BlockTicker() {
        this.universal = false;
    }

    public BlockTicker(boolean universal) {
        this.universal = universal;
    }

    public void update() {
        if (unique) {
            uniqueTick();
            unique = false;
        }
    }

    @Override
    public Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        return ItemHandler.super.validate(item);
    }

    /**
     * This method must be overridden to define whether a Block
     * needs to be run on the main server thread (World Manipulation requires that)
     *
     * @return Whether this task should run on the main server thread
     */
    public abstract boolean isSynchronized();

    /**
     * This method is called every tick for every block
     *
     * @param b    The {@link Block} that was ticked
     * @param item The corresponding {@link SlimefunItem}
     * @param data The data stored in this {@link Block}
     */
    public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
        tick(b, item, new BlockDataConfigWrapper(data));
    }

    /**
     * This method is called every tick for every block
     *
     * @param b    The {@link Block} that was ticked
     * @param item The corresponding {@link SlimefunItem}
     * @param data The data stored in this {@link Block}
     */
    public void tick(Block b, SlimefunItem item, SlimefunUniversalData data) {
        // Override this method and fill it with content
    }

    @Deprecated
    public void tick(Block b, SlimefunItem item, Config data) {
    }

    /**
     * This method is called every tick but not per-block and only once.
     */
    public void uniqueTick() {
        // Override this method and fill it with content
    }

    @Override
    public Class<? extends ItemHandler> getIdentifier() {
        return BlockTicker.class;
    }

    /**
     * This method resets the 'unique' flag for {@link BlockTicker#uniqueTick()}
     */
    public void startNewTick() {
        unique = true;
    }
}
