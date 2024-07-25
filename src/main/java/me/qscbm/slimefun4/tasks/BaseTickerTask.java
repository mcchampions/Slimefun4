package me.qscbm.slimefun4.tasks;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Location;

public abstract class BaseTickerTask implements Runnable {
    public abstract void start(Slimefun plugin);

    public abstract void halt();

    public abstract void enableTicker(Location l);

    public abstract void disableTicker(Location l);
}
