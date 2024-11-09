package com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes;

import city.norain.slimefun4.api.menu.UniversalMenu;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import lombok.Getter;

/**
 * 这个枚举类用于声明 {@link SlimefunUniversalData} 的特征.
 * 一个通用数据可以有单个或多个特征.
 * <p>
 * 对于一个通用数据, 它默认拥有作为 k-v 容器的特征.
 *
 * @see SlimefunUniversalData
 * @see SlimefunUniversalBlockData
 */
@Getter
public enum UniversalDataTrait {
    /**
     * BLOCK 特征标明该通用数据属于 {@link SlimefunUniversalBlockData}
     */
    BLOCK("location"),

    /**
     * INVENTORY 特征标明该通用数据拥有一个 {@link UniversalMenu}
     */
    INVENTORY("");

    private final String reservedKey;

    UniversalDataTrait(String reservedKey) {
        this.reservedKey = reservedKey;
    }

    public static boolean isReservedKey(String key) {
        for (UniversalDataTrait trait : UniversalDataTrait.values()) {
            if (trait.getReservedKey().equals(key)) {
                return true;
            }
        }

        return false;
    }
}
