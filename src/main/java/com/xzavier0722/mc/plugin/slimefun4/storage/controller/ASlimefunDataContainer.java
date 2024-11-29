package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class ASlimefunDataContainer extends ADataContainer {
    private final String sfId;

    @Setter
    private volatile boolean pendingRemove;

    public ASlimefunDataContainer(String key, String sfId) {
        super(key);
        this.sfId = sfId;
    }

    public ASlimefunDataContainer(String key, ADataContainer other, String sfId) {
        super(key, other);
        this.sfId = sfId;
    }
}
