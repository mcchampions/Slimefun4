package io.github.thebusybiscuit.slimefun4.utils.biomes;

import com.google.gson.JsonElement;
import io.github.thebusybiscuit.slimefun4.api.exceptions.BiomeMapException;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * {@link BiomeMap}s are used to map data values to {@link Biome} constants.
 * <p>
 * We heavily utilise this method of data mapping for {@link GEOResource}s, especially
 * when supporting multiple versions of Minecraft. This way, we can have different {@link BiomeMap}s
 * for different versions of Minecraft, in case {@link Biome} names change in-between versions.
 * <p>
 * The data type can be any type of {@link Object}.
 * The most common type is {@link Integer}, if you are using complex objects and try to read
 * your {@link BiomeMap} from a {@link JsonElement}, make sure to provide an adequate
 * {@link BiomeDataConverter} to convert the raw json data.
 *
 * @author TheBusyBiscuit
 *
 * @param <T>
 *            The stored data type
 */
public class BiomeMap<T> implements Keyed {
    /**
     * Our internal {@link EnumMap} holding all the data.
     */
    private final Map<Biome, T> dataMap = new EnumMap<>(Biome.class);

    /**
     * The {@link NamespacedKey} to identify this {@link BiomeMap}.
     */
    private final NamespacedKey namespacedKey;

    /**
     * This constructs a new {@link BiomeMap} with the given {@link NamespacedKey}.
     *
     * @param namespacedKey
     *            The {@link NamespacedKey} for this {@link BiomeMap}
     */
    public BiomeMap(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public @Nullable T get(Biome biome) {
        return dataMap.get(biome);
    }

    public T getOrDefault(Biome biome, T defaultValue) {
        return dataMap.getOrDefault(biome, defaultValue);
    }

    public boolean containsKey(Biome biome) {
        return dataMap.containsKey(biome);
    }

    public boolean containsValue(T value) {
        return dataMap.containsValue(value);
    }

    /**
     * This returns whether this {@link BiomeMap} is empty.
     * An empty {@link BiomeMap} contains no biomes or values.
     *
     * @return Whether this {@link BiomeMap} is empty.
     */
    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    public boolean put(Biome biome, T value) {
        return dataMap.put(biome, value) == null;
    }

    public void putAll(Map<Biome, T> map) {
        dataMap.putAll(map);
    }

    public void putAll(BiomeMap<T> map) {
        dataMap.putAll(map.dataMap);
    }

    public boolean remove(Biome biome) {
        return dataMap.remove(biome) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BiomeMap " + dataMap;
    }

    public static <T> BiomeMap<T> fromJson(
            NamespacedKey key, String json, BiomeDataConverter<T> valueConverter) throws BiomeMapException {
        // All parameters are validated by the Parser.
        BiomeMapParser<T> parser = new BiomeMapParser<>(key, valueConverter);
        parser.read(json);
        return parser.buildBiomeMap();
    }

    public static <T> BiomeMap<T> fromJson(
            NamespacedKey key, String json, BiomeDataConverter<T> valueConverter, boolean isLenient)
            throws BiomeMapException {
        // All parameters are validated by the Parser.
        BiomeMapParser<T> parser = new BiomeMapParser<>(key, valueConverter);
        parser.setLenient(isLenient);
        parser.read(json);
        return parser.buildBiomeMap();
    }

    public static <T> BiomeMap<T> fromResource(
            NamespacedKey key, JavaPlugin plugin, String path, BiomeDataConverter<T> valueConverter)
            throws BiomeMapException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(plugin.getClass().getResourceAsStream(path), StandardCharsets.UTF_8))) {
            return fromJson(key, reader.lines().collect(Collectors.joining("")), valueConverter);
        } catch (IOException x) {
            throw new BiomeMapException(key, x);
        }
    }

    public static BiomeMap<Integer> getIntMapFromResource(NamespacedKey key, JavaPlugin plugin, String path)
            throws BiomeMapException {
        return fromResource(key, plugin, path, JsonElement::getAsInt);
    }

    public static BiomeMap<Long> getLongMapFromResource(NamespacedKey key, JavaPlugin plugin, String path)
            throws BiomeMapException {
        return fromResource(key, plugin, path, JsonElement::getAsLong);
    }

    public static BiomeMap<String> getStringMapFromResource(NamespacedKey key, JavaPlugin plugin, String path)
            throws BiomeMapException {
        return fromResource(key, plugin, path, JsonElement::getAsString);
    }
}
