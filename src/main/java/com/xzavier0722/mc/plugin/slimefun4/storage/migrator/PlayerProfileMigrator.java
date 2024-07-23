package com.xzavier0722.mc.plugin.slimefun4.storage.migrator;

import lombok.Getter;

@Deprecated
public class PlayerProfileMigrator implements IMigrator {
    @Getter
    private static final PlayerProfileMigrator instance = new PlayerProfileMigrator();

    private PlayerProfileMigrator() {}

    @Override
    public String getName() {
        return "PlayerProfile";
    }

    @Override
    public boolean hasOldData() {
        return false;
    }

    /**
     * To check the existence of old player data stored as yml
     * and try to migrate them to database
     */
    @Override
    public MigrateStatus migrateData() {
        return MigrateStatus.MIGRATED;
    }
}
