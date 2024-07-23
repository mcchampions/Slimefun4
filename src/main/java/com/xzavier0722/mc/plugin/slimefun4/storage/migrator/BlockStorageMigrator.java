package com.xzavier0722.mc.plugin.slimefun4.storage.migrator;

import lombok.Getter;

@Deprecated
public class BlockStorageMigrator implements IMigrator {
    @Getter
    private static final BlockStorageMigrator instance = new BlockStorageMigrator();

    private BlockStorageMigrator() {}

    @Override
    public String getName() {
        return "BlockStorage";
    }

    @Override
    public boolean hasOldData() {
        return false;
    }

    @Override
    public MigrateStatus migrateData() {
        return MigrateStatus.MIGRATED;
    }
}
