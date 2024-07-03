package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.configuration.file.FileConfiguration;

@Deprecated
public class BlockDataConfigWrapper extends Config {
    private final SlimefunBlockData blockData;

    public BlockDataConfigWrapper(SlimefunBlockData blockData) {
        super(new File(""));
        this.blockData = blockData;
    }

    @Override
    public void save() {}

    @Override
    public void createFile() {}

    @Override
    public String getString(String path) {
        return blockData.getData(path);
    }

    
    @Override
    public Set<String> getKeys() {
        return new HashSet<>(blockData.getAllData().keySet());
    }

    
    @Override
    public Set<String> getKeys(String path) {
        return getKeys();
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public boolean contains(String path) {
        return getString(path) != null;
    }

    @Nullable @Override
    public Object getValue(String path) {
        return getString(path);
    }

    @Override
    public FileConfiguration getConfiguration() {
        return null;
    }

    @Override
    public void setDefaultValue(String path, @Nullable Object value) {
        if (!(value instanceof String str)) {
            throw new NotImplementedException();
        }
        if (getString(path) == null) {
            blockData.setData(path, str);
        }
    }

    @Override
    public void setValue(String path, Object value) {
        if (!(value instanceof String str)) {
            throw new NotImplementedException();
        }
        blockData.setData(path, str);
    }

    @Override
    public void save(File file) {}

    @Override
    public void reload() {}
}
