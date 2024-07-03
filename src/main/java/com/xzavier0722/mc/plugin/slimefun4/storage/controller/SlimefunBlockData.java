package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.LocationUtils;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class SlimefunBlockData extends ASlimefunDataContainer {
    @Getter
    private final Location location;
    @Getter
    private final SlimefunItemStack sfItemStack;
    private volatile BlockMenu menu;
    @Getter
    private volatile boolean pendingRemove = false;

    @ParametersAreNonnullByDefault
    SlimefunBlockData(Location location, String nbtStr) {
        super(LocationUtils.getLocKey(location));
        this.location = location;
        // TODO: 适配1.20.5及更高版本
        ReadWriteNBT nbt = NBT.parseNBT(nbtStr);
        String sfId = nbt.getString("slimefun:slimefun_id");
        SlimefunItemStack itemStack = (SlimefunItemStack) SlimefunItem.getById(sfId).getItem().clone();
        NBT.modify(itemStack, nbtSnapshot -> {
            nbtSnapshot.mergeCompound(nbt);
        });
        this.sfItemStack = itemStack;
    }

    @ParametersAreNonnullByDefault
    SlimefunBlockData(Location location, SlimefunItem slimefunItem) {
        super(LocationUtils.getLocKey(location));
        this.location = location;
        this.sfItemStack = (SlimefunItemStack) slimefunItem.getItem().clone();
    }

    @ParametersAreNonnullByDefault
    SlimefunBlockData(Location location, SlimefunBlockData other) {
        super(LocationUtils.getLocKey(location), other);
        this.location = location;
        this.sfItemStack = other.sfItemStack;
    }

<<<<<<< Updated upstream
    
=======
<<<<<<< Updated upstream
    @Nonnull
>>>>>>> Stashed changes
    public Location getLocation() {
        return location;
    }

<<<<<<< Updated upstream
    
=======
    @Nonnull
=======

    public String getSfNbt() {
        return NBT.readNbt(sfItemStack).toString();
    }

>>>>>>> Stashed changes
>>>>>>> Stashed changes
    public String getSfId() {
        return sfItemStack.getItemId();
    }

    @ParametersAreNonnullByDefault
    public void setData(String key, String val) {
        checkData();
        setCacheInternal(key, val, true);
        Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedBlockDataUpdate(this, key);
    }

    @ParametersAreNonnullByDefault
    public void removeData(String key) {
        if (removeCacheInternal(key) != null || !isDataLoaded()) {
            Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedBlockDataUpdate(this, key);
        }
    }

    @ParametersAreNullableByDefault
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

    public void setPendingRemove(boolean pendingRemove) {
        this.pendingRemove = pendingRemove;
    }

    @Override
    public String toString() {
        return "SlimefunBlockData [sfId="
                + getSfId()
                + ", location="
                + location
                + ", isPendingRemove="
                + pendingRemove
                + "]";
    }
}
