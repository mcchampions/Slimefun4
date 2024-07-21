package io.github.thebusybiscuit.slimefun4.core.services;

import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.core.services.localization.LanguageFile;
import io.github.thebusybiscuit.slimefun4.core.services.localization.SlimefunLocalization;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * As the name suggests, this Service is responsible for Localization.
 * It is used for managing the {@link Language} of a {@link Player} and the entire {@link Server}.
 *
 * @author TheBusyBiscuit
 *
 * @see Language
 *
 */
public class LocalizationService extends SlimefunLocalization {
    private static final String LANGUAGE_PATH = "language";

    // All supported languages are stored in this LinkedHashMap, it is Linked so we keep the order
    private final Map<String, Language> languages = new LinkedHashMap<>();
    private final boolean translationsEnabled;
    private final Slimefun plugin;
    private final String prefix;
    private final NamespacedKey languageKey;
    private final Language defaultLanguage;

    public LocalizationService(
            Slimefun plugin, @Nullable String prefix, @Nullable String serverDefaultLanguage) {
        super(plugin);

        this.plugin = plugin;
        this.prefix = prefix;
        languageKey = new NamespacedKey(plugin, LANGUAGE_PATH);

        if (serverDefaultLanguage != null) {
            translationsEnabled = Slimefun.getCfg().getBoolean("options.enable-translations");

            defaultLanguage = new Language(
                    serverDefaultLanguage, "11b3188fd44902f72602bd7c2141f5a70673a411adb3d81862c69e536166b");
            defaultLanguage.setFile(LanguageFile.MESSAGES, getConfig().getConfiguration());
            loadEmbeddedLanguages();
            initLanguage();
            save();
        } else {
            translationsEnabled = false;
            defaultLanguage = null;
        }
    }

    /**
     * This method returns whether translations are enabled on this {@link Server}.
     *
     * @return Whether translations are enabled
     */
    public boolean isEnabled() {
        return translationsEnabled;
    }

    @Override
    public String getChatPrefix() {
        return prefix;
    }

    @Override
    public NamespacedKey getKey() {
        return languageKey;
    }

    @Override
    @Nullable public Language getLanguage(String id) {
        return languages.get(id);
    }

    @Override
    
    public Collection<Language> getLanguages() {
        return languages.values();
    }

    @Override
    public boolean hasLanguage(String id) {
        // Checks if our jar files contains a messages.yml file for that language
        return Objects.equals(id, "zh-CN");
    }

    /**
     * This returns whether the given {@link Language} is loaded or not.
     *
     * @param id
     *            The id of that {@link Language}
     *
     * @return Whether or not this {@link Language} is loaded
     */
    public boolean isLanguageLoaded(String id) {
        return languages.containsKey(id);
    }

    @Override
    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public Language getLanguage(Player p) {
         return getLanguage("zh-CN");
    }

    private void initLanguage() {
        // Copy defaults
        for (LanguageFile file : LanguageFile.values()) {
            if (file != LanguageFile.MESSAGES) {
                copyToDefaultLanguage("zh-CN", file);
            }
        }

        Slimefun.logger().log(Level.INFO, "Loaded language \"{0}\"", "zh-CN");
        getConfig().setValue(LANGUAGE_PATH, "zh-CN");

        // Loading in the defaults from our resources folder
        String path = "/languages/" + "zh-CN" + "/messages.yml";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(plugin.getClass().getResourceAsStream(path), StandardCharsets.UTF_8))) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(reader);
            getConfig().getConfiguration().setDefaults(config);
        } catch (IOException e) {
            Slimefun.logger().log(Level.SEVERE, e, () -> "Failed to load language file: \"" + path + "\"");
        }

        save();
    }

    private void copyToDefaultLanguage(String language, LanguageFile file) {
        FileConfiguration config = getConfigurationFromStream(file.getFilePath(language), null);
        defaultLanguage.setFile(file, config);
    }

    @Override
    protected void addLanguage(String id, String texture) {
        if (hasLanguage(id)) {
            Language language = new Language(id, texture);

            for (LanguageFile file : LanguageFile.values()) {
                FileConfiguration defaults =
                        file == LanguageFile.MESSAGES ? getConfig().getConfiguration() : null;
                FileConfiguration config = getConfigurationFromStream(file.getFilePath(language), defaults);
                language.setFile(file, config);
            }

            languages.put(id, language);
        }
    }

    /**
     * This returns the progress of translation for any given {@link Language}.
     * The progress is determined by the amount of translated strings divided by the amount
     * of strings in the english {@link Language} file and multiplied by 100.0
     *
     * @param lang
     *            The {@link Language} to get the progress of
     *
     * @return A percentage {@code (0.0 - 100.0)} for the progress of translation of that {@link Language}
     */
    public double calculateProgress(Language lang) {
        return 100d;
    }

    private FileConfiguration getConfigurationFromStream(
            String file, @Nullable FileConfiguration defaults) {
        InputStream inputStream = plugin.getClass().getResourceAsStream(file);

        if (inputStream == null) {
            return new YamlConfiguration();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String content = reader.lines().collect(Collectors.joining("\n"));
            YamlConfiguration config = new YamlConfiguration();

            /*
             * Fixes #3400 - Only attempt to load yaml files that contain data.
             * This is not a perfect fix but should be sufficient to circumvent this issue.
             */
            if (PatternUtils.YAML_ENTRY.matcher(content).find()) {
                config.loadFromString(content);

                if (defaults != null) {
                    config.setDefaults(defaults);
                }
            }

            return config;
        } catch (IOException | InvalidConfigurationException e) {
            Slimefun.logger().log(Level.WARNING, e, () -> "Failed to load language file into memory: \"" + file + "\"");
            return new YamlConfiguration();
        }
    }
}
