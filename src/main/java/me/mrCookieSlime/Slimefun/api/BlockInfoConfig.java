package me.mrCookieSlime.Slimefun.api;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * This class is used to speed up parsing of a {@link JsonObject} that is stored at
 * a given {@link Location}.
 * <p>
 * This simply utilises a {@link HashMap} to cache the data and then provides the same getters
 * as a normal {@link Config}.
 *
 * @author creator3
 * @see BlockStorage
 */
public class BlockInfoConfig extends Config {
    private final Map<String, String> data;

    public BlockInfoConfig() {
        this(new HashMap<>());
    }

    public BlockInfoConfig(Map<String, String> data) {
        super(null, null);
        this.data = data;
    }

    public Map<String, String> getMap() {
        return data;
    }

    @Override
    public void setValue(String path, Object value) {
        if (value == null) {
            data.remove(path);
        } else {
            data.put(path, (String) value);
        }
    }

    @Override
    public boolean contains(String path) {
        return data.containsKey(path);
    }

    @Override
    public Object getValue(String path) {
        return getString(path);
    }

    @Override
    public String getString(String path) {
        return data.get(path);
    }

    @Override
    public Set<String> getKeys() {
        return data.keySet();
    }

    @Override
    public Set<String> getKeys(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileConfiguration getConfiguration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(File file) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException();
    }

    public String toJSON() {
        return new GsonBuilder().create().toJson(data);
    }
}
