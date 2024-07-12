package io.github.thebusybiscuit.slimefun4.core.services.localization;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * This enum holds info about a {@link Language} that is embedded in our resources folder.
 * Every enum constant holds the key of that {@link Language} as well as a texture hash
 * for the {@link ItemStack} to display.
 *
 * @author TheBusyBiscuit
 *
 * @see Language
 *
 */
public enum LanguagePreset {
    CHINESE_CHINA("zh-CN", "7f9bc035cdc80f1ab5e1198f29f3ad3fdd2b42d9a69aeb64de990681800b98dc");

    private final String id;
    private final boolean releaseReady;
    private final String textureHash;
    @Getter
    private final TextDirection textDirection;

    @ParametersAreNonnullByDefault
    LanguagePreset(String id, boolean releaseReady, TextDirection direction, String textureHash) {
        this.id = id;
        this.releaseReady = releaseReady;
        this.textureHash = textureHash;
        this.textDirection = direction;
    }

    @ParametersAreNonnullByDefault
    LanguagePreset(String id, boolean releaseReady, String textureHash) {
        this(id, releaseReady, TextDirection.LEFT_TO_RIGHT, textureHash);
    }

    @ParametersAreNonnullByDefault
    LanguagePreset(String id, TextDirection direction, String textureHash) {
        this(id, true, direction, textureHash);
    }

    @ParametersAreNonnullByDefault
    LanguagePreset(String id, String textureHash) {
        this(id, true, textureHash);
    }

    /**
     * This returns the id of this {@link Language}.
     *
     * @return The language code
     */
    public String getLanguageCode() {
        return id;
    }

    /**
     * This returns whether this {@link LanguagePreset} is "release-ready".
     * A release-ready {@link Language} will be available in RC builds of Slimefun.
     *
     * @return Whether this {@link Language} is "release-ready"
     */
    boolean isReadyForRelease() {
        return releaseReady;
    }

    /**
     * This returns the texture hash for this language.
     * This will be the flag of the corresponding country.
     * (Not accurate I know, but better than having all languages
     * look the same by using the same items)
     *
     * @return The texture hash of this language
     */
    public String getTexture() {
        return textureHash;
    }

}
