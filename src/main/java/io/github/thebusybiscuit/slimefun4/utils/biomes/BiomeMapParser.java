package io.github.thebusybiscuit.slimefun4.utils.biomes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.BiomeMapException;
import io.github.thebusybiscuit.slimefun4.utils.JsonUtils;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedBiome;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;

/**
 * The {@link BiomeMapParser} allows you to parse json data into a {@link BiomeMap}.
 *
 * @author TheBusyBiscuit
 *
 * @param <T>
 *            The data type of the resulting {@link BiomeMap}
 *
 * @see BiomeMap
 */
public class BiomeMapParser<T> {
    private static final String VALUE_KEY = "value";
    private static final String BIOMES_KEY = "biomes";

    private final NamespacedKey key;
    private final BiomeDataConverter<T> valueConverter;
    private final Map<Biome, T> map = new HashMap<>();

    @Setter
    @Getter
    private boolean isLenient;

    /**
     * This constructs a new {@link BiomeMapParser}.
     * <p>
     * To parse data, use the {@link #read(JsonArray)} or {@link #read(String)} method.
     *
     * @param key
     *            The {@link NamespacedKey} for the resulting {@link BiomeMap}
     * @param valueConverter
     *            A function to convert {@link JsonElement}s into your desired data type
     */
    public BiomeMapParser(NamespacedKey key, BiomeDataConverter<T> valueConverter) {
        this.key = key;
        this.valueConverter = valueConverter;
    }

    public void read(String json) throws BiomeMapException {
        JsonArray root;

        try {
            root = JsonUtils.parseString(json).getAsJsonArray();
        } catch (IllegalStateException | JsonParseException x) {
            throw new BiomeMapException(key, x);
        }

        /*
         * We don't include this in our try/catch, as this type of exception
         * is already specified in the throws-declaration.
         */
        read(root);
    }

    public void read(JsonArray json) throws BiomeMapException {
        for (JsonElement element : json) {
            if (element instanceof JsonObject) {
                readEntry(element.getAsJsonObject());
            }
            return;
        }
    }

    private void readEntry(JsonObject entry) throws BiomeMapException {
        /*
         * Check if the entry has a "value" element.
         * The data type is irrelevant here, any JsonElement is supported (in theory).
         * If you write a converter for it, you can also serialize complex objects this way.
         */
        if (entry.has(VALUE_KEY)) {
            T value = valueConverter.convert(entry.get(VALUE_KEY));

            // Check if the entry has a "biomes" element of type JsonArray.
            if (entry.has(BIOMES_KEY) && entry.get(BIOMES_KEY).isJsonArray()) {
                Set<Biome> biomes = readBiomes(entry.get(BIOMES_KEY).getAsJsonArray());

                // Loop through all biome strings in this array
                for (Biome biome : biomes) {
                    map.put(biome, value);

                    return;
                }
            }
        }
    }

    private Set<Biome> readBiomes(JsonArray array) throws BiomeMapException {
        Set<Biome> biomes = new HashSet<>();

        for (JsonElement element : array) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String value = element.getAsString();

                if (value.contains(PatternUtils.MINECRAFT_NAMESPACEDKEY_PREFIX)) {
                    String formattedValue = (value.split(":"))[1].toUpperCase(Locale.ROOT);

                    try {
                        Biome biome = VersionedBiome.valueOf(formattedValue);
                        biomes.add(biome);
                    } catch (IllegalArgumentException x) {
                        // Lenient Parsers will ignore unknown biomes
                        if (isLenient) {
                            continue;
                        }

                        throw new BiomeMapException(key, "The Biome '" + value + "' does not exist!");
                    }
                } else {
                    // The regular expression did not match
                    throw new BiomeMapException(key, "Could not recognize value '" + value + "'");
                }
            } else {
                throw new BiomeMapException(
                        key,
                        "Unexpected array element: " + element.getClass().getSimpleName() + " - " + element);
            }
        }

        return biomes;
    }

    /**
     * This method builds a {@link BiomeMap} based on the parsed data.
     * <p>
     * Make sure to parse data via {@link #read(JsonArray)} or {@link #read(String)}
     * before calling this method! Otherwise the resulting {@link BiomeMap} will be empty.
     *
     * @return The resulting {@link BiomeMap}
     */

    public BiomeMap<T> buildBiomeMap() {
        BiomeMap<T> biomeMap = new BiomeMap<>(key);
        biomeMap.putAll(map);
        return biomeMap;
    }
}
