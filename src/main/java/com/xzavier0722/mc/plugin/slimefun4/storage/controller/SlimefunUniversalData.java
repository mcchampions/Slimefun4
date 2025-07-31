package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import city.norain.slimefun4.api.menu.UniversalMenu;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes.UniversalDataTrait;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.inventory.ItemStack;

@Slf4j
@Getter
public class SlimefunUniversalData extends ASlimefunDataContainer {
    @Setter
    private volatile UniversalMenu menu;

    @Setter
    private volatile boolean pendingRemove;

    private final Set<UniversalDataTrait> traits = EnumSet.noneOf(UniversalDataTrait.class);

    SlimefunUniversalData(UUID uuid, String sfId) {
        super(uuid.toString(), sfId);
    }

    SlimefunUniversalData(UUID uuid, String sfId, Set<UniversalDataTrait> traits) {
        super(uuid.toString(), sfId);
        this.traits.addAll(traits);
    }

    public void setData(String key, String val) {
        if (UniversalDataTrait.isReservedKey(key)) {
            Slimefun.logger().log(Level.WARNING, "警告: 有附属正在尝试修改受保护的方块数据, 已取消更改");
            return;
        }

        setCacheInternal(key, val, true);
        Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedUniversalDataUpdate(this, key);
    }

    protected void setTraitData(UniversalDataTrait trait, String val) {
        if (!trait.getReservedKey().isEmpty()) {
            setCacheInternal(trait.getReservedKey(), val, true);

            Slimefun.getDatabaseManager()
                    .getBlockDataController()
                    .scheduleDelayedUniversalDataUpdate(this, trait.getReservedKey());
        }
    }

    protected void setTraitData(String val) {
        if (!UniversalDataTrait.BLOCK.getReservedKey().isEmpty()) {
            setCacheInternal(UniversalDataTrait.BLOCK.getReservedKey(), val, true);

            Slimefun.getDatabaseManager()
                    .getBlockDataController()
                    .scheduleDelayedUniversalDataUpdate(this, UniversalDataTrait.BLOCK.getReservedKey());
        }
    }

    public void removeData(String key) {
        if (removeCacheInternal(key) != null || !isDataLoaded()) {
            Slimefun.getDatabaseManager().getBlockDataController().scheduleDelayedUniversalDataUpdate(this, key);
        }
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

    public UUID getUUID() {
        return UUID.fromString(getKey());
    }

    public void addTrait(UniversalDataTrait... trait) {
        traits.addAll(List.of(trait));
    }

    public boolean hasTrait(UniversalDataTrait trait) {
        return traits.contains(trait);
    }

    @Override
    public String toString() {
        return "SlimefunUniversalData [uuid= " + getUUID() + ", sfId=" + getSfId() + ", isPendingRemove="
               + pendingRemove + "]";
    }
}
