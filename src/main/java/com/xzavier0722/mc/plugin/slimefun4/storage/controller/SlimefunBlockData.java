package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.LocationUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import javax.annotation.Nullable;

import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class SlimefunBlockData extends ASlimefunDataContainer {
    @Getter
    private final Location location;

    private volatile BlockMenu menu;

    SlimefunBlockData(Location location, String sfId) {
        super(LocationUtils.getLocKey(location), sfId);
        this.location = location;
    }

    SlimefunBlockData(Location location, SlimefunBlockData other) {
        super(LocationUtils.getLocKey(location), other, other.getSfId());
        this.location = location;
    }

    public String getSfId() {
        return super.getSfId();
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

    @Nullable
    public BlockMenu getBlockMenu() {
        return menu;
    }

    @Nullable
    public ItemStack[] getMenuContents() {
        if (menu == null) {
            return null;
        }
        ItemStack[] re = new ItemStack[54];
        var presetSlots = menu.getPreset().getPresetSlots();
        var inv = menu.toInventory().getContents();
        for (int i = 0; i < inv.length; i++) {
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
               + getSfId()
               + ", location="
               + location
               + ", isPendingRemove="
               + isPendingRemove()
               + "]";
    }
}
