package io.github.thebusybiscuit.slimefun4.core.services.localization;

import io.github.thebusybiscuit.slimefun4.core.services.LocalizationService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * This Class represents a {@link Language} that Slimefun can recognize and use.
 *
 * @author TheBusyBiscuit
 *
 * @see LocalizationService
 *
 */
public final class Language {

    private final Map<LanguageFile, FileConfiguration> files = new EnumMap<>(LanguageFile.class);

    /**
     * -- GETTER --
     *  This returns the identifier of this
     * .
     *
     */
    @Getter
    private final String id;
    /**
     * -- GETTER --
     *  This method returns the
     *  that is used to display this
     *  in the
     * .
     *
     */
    @Getter
    private final ItemStack item;
    private double progress = -1;

    /**
     * This instantiates a new {@link Language} with the given language code
     * and skull texture.
     *
     * @param id
     *            The language code of this {@link Language}
     * @param hash
     *            The hash of the skull texture to use
     */
    public Language(String id, String hash) {
        Validate.notNull(id, "A Language must have an id that is not null!");
        Validate.notNull(hash, "A Language must have a texture that is not null!");

        this.id = id;
        this.item = SlimefunUtils.getCustomHead(hash);

        Slimefun.getItemTextureService().setTexture(item, "_UI_LANGUAGE_" + id.toUpperCase(Locale.ROOT));
    }

    /**
     * This method returns the progress of translation for this {@link Language}.
     * The progress is determined by the amount of translated strings divided by the amount
     * of strings in the english {@link Language} file and multiplied by 100.0
     *
     * @return A percentage {@code (0.0 - 100.0)} for the progress of translation of this {@link Language}
     */
    public double getTranslationProgress() {
        if (id.equals("en")) {
            return 100.0;
        } else {
            if (progress < 0) {
                progress = Slimefun.getLocalization().calculateProgress(this);
            }

            return progress;
        }
    }

    @Nullable FileConfiguration getFile(LanguageFile file) {
        return files.get(file);
    }

    public void setFile(LanguageFile file, FileConfiguration config) {
        Validate.notNull(file, "The provided file should not be null.");
        Validate.notNull(config, "The provided config should not be null.");

        files.put(file, config);
    }

    /**
     * This method localizes the name of this {@link Language} in the selected {@link Language}
     * of the given {@link Player}.
     *
     * @param p
     *            The {@link Player} to localize the name for
     * @return The localized name of this {@link Language}
     */
    public String getName(Player p) {
        return Slimefun.getLocalization().getMessage(p, "languages." + id);
    }

    /**
     * This method returns whether this {@link Language} is also the default
     * {@link Language} of this {@link Server}.
     *
     * @return Whether this is the default {@link Language} of this {@link Server}
     */
    public boolean isDefault() {
        return this == Slimefun.getLocalization().getDefaultLanguage();
    }

    @Override
    public String toString() {
        return "Language {id= " + id + ", default=" + isDefault() + " }";
    }

    
    FileConfiguration[] getFiles() {
        // @formatter:off
        return Arrays.stream(LanguageFile.valuesCached).map(this::getFile).toArray(FileConfiguration[]::new);
        // @formatter:on
    }
}
