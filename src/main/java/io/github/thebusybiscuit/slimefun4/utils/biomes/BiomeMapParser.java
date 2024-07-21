package io.github.thebusybiscuit.slimefun4.utils.biomes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.api.exceptions.BiomeMapException;
import io.github.thebusybiscuit.slimefun4.utils.JsonUtils;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
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
    private final Map<Biome, T> map = new EnumMap<>(Biome.class);

    /**
     * This flag specifies whether the parsing is "lenient" or not.
     * A lenient parser will not throw a {@link BiomeMapException} if the {@link Biome}
     * could not be found.
     * The default value is false.
     */
    private boolean isLenient = false;

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

    /**
     * This method sets the "lenient" flag for this parser.
     * <p>
     * A lenient parser will not throw a {@link BiomeMapException} if the {@link Biome}
     * could not be found.
     * The default value is false.
     *
     * @param isLenient
     *            Whether this parser should be lenient or not.
     */
    public void setLenient(boolean isLenient) {
        this.isLenient = isLenient;
    }

    /**
     * This method returns whether this parser is flagged as "lenient".
     * <p>
     * A lenient parser will not throw a {@link BiomeMapException} if the {@link Biome}
     * could not be found.
     * The default value is false.
     *
     * @return Whether this parser is lenient or not.
     */
    public boolean isLenient() {
        return isLenient;
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
            } else {
                throw new BiomeMapException(
                        key,
                        "Unexpected array element: " + element.getClass().getSimpleName() + " - " + element);
            }
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
                    T prev = map.put(biome, value);

                    // Check for duplicates
                    if (prev != null) {
                        throw new BiomeMapException(key, "Biome '" + biome.getKey() + "' is registered twice");
                    }
                }
            } else {
                throw new BiomeMapException(key, "Entry is missing a 'biomes' child of type array.");
            }
        } else {
            throw new BiomeMapException(key, "Entry is missing a 'value' child.");
        }
    }

    private Set<Biome> readBiomes(JsonArray array) throws BiomeMapException {
        Set<Biome> biomes = EnumSet.noneOf(Biome.class);

        for (JsonElement element : array) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String value = element.getAsString();

                if (PatternUtils.MINECRAFT_NAMESPACEDKEY.matcher(value).matches()) {
                    String formattedValue = CommonPatterns.COLON.split(value)[1].toUpperCase(Locale.ROOT);

                    try {
                        Biome biome = Biome.valueOf(formattedValue);
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
