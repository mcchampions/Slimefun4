package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes.UniversalDataTrait;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.LocationUtils;
import io.github.bakedlibs.dough.blocks.BlockPosition;

import java.util.UUID;

import org.bukkit.Location;

public class SlimefunUniversalBlockData extends SlimefunUniversalData {
    private volatile BlockPosition lastPresent;

    public SlimefunUniversalBlockData(UUID uuid, String sfId) {
        super(uuid, sfId);
    }

    public SlimefunUniversalBlockData(UUID uuid, String sfId, BlockPosition present) {
        super(uuid, sfId);

        this.lastPresent = present;
    }

    public SlimefunUniversalBlockData(UUID uuid, String sfId, Location present) {
        this(uuid, sfId, new BlockPosition(present));
    }

    public void setLastPresent(BlockPosition lastPresent) {
        setTraitData(UniversalDataTrait.BLOCK, LocationUtils.locationToString(lastPresent.toLocation()));
        this.lastPresent = lastPresent;
    }

    public BlockPosition getLastPresent() {
        if (lastPresent != null) {
            return lastPresent;
        }

        var data = getData("location");

        if (data == null) {
            throw new IllegalArgumentException("UniversalBlockData missing location data");
        }

        lastPresent = new BlockPosition(LocationUtils.toLocation(data));

        return lastPresent;
    }
}