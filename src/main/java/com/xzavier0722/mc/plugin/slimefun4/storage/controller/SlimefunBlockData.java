package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.LocationUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class SlimefunBlockData extends ASlimefunDataContainer {
    @Getter
    private final Location location;
    @Getter
    private final String sfId;
    private volatile BlockMenu menu;
    @Setter
    @Getter
    private volatile boolean pendingRemove = false;

    SlimefunBlockData(Location location, String sfId) {
        super(LocationUtils.getLocKey(location));
        this.location = location;
        this.sfId = sfId;
    }

    SlimefunBlockData(Location location, SlimefunBlockData other) {
        super(LocationUtils.getLocKey(location), other);
        this.location = location;
        this.sfId = other.sfId;
    }

    public void setData(String key, String val) {
        checkData();
        setCacheInternal(key, val, true);
        Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedBlockDataUpdate(this, key);
    }

    public void removeData(String key) {
        if (removeCacheInternal(key) != null || !isDataLoaded()) {
            Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedBlockDataUpdate(this, key);
        }
    }

    void setBlockMenu(BlockMenu blockMenu) {
        menu = blockMenu;
    }

    @Nullable public BlockMenu getBlockMenu() {
        return menu;
    }

    @Nullable public ItemStack[] getMenuContents() {
        if (menu == null) {
            return null;
        }
        var re = new ItemStack[54];
        var presetSlots = menu.getPreset().getPresetSlots();
        var inv = menu.toInventory().getContents();
        for (var i = 0; i < inv.length; i++) {
            if (presetSlots.contains(i)) {
                continue;
            }
            re[i] = inv[i];
        }

        return re;
    }

    @Override
    public String toString() {
        return "SlimefunBlockData [sfId="
                + sfId
                + ", location="
                + location
                + ", isPendingRemove="
                + pendingRemove
                + "]";
    }
}
