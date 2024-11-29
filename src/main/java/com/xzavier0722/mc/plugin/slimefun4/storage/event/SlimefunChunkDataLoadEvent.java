package com.xzavier0722.mc.plugin.slimefun4.storage.event;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunChunkData;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class SlimefunChunkDataLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final SlimefunChunkData chunkData;

    public SlimefunChunkDataLoadEvent(SlimefunChunkData chunkData) {
        this.chunkData = chunkData;
    }

    public World getWorld() {
        return getChunk().getWorld();
    }

    public Chunk getChunk() {
        return chunkData.getChunk();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
