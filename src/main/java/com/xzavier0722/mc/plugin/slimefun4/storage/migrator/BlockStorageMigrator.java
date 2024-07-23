package com.xzavier0722.mc.plugin.slimefun4.storage.migrator;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

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
