package me.qscbm.slimefun4.handlers;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.block.Block;

import java.util.Optional;

public abstract class CargoTicker implements ItemHandler {
    protected boolean unique = true;

    public void update() {
        if (unique) {
            unique = false;
        }
    }

    @Override
    public Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        return ItemHandler.super.validate(item);
    }

    public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {}

    @Override
    public Class<? extends ItemHandler> getIdentifier() {
        return CargoTicker.class;
    }

    public void startNewTick() {
        unique = true;
    }
}
