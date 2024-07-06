package io.github.thebusybiscuit.slimefun4.api;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.papermc.lib.PaperLib;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;

/**
 * This enum holds all versions of Minecraft that we currently support.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 * @see Slimefun
 */
public enum MinecraftVersion {

    /**
     * This constant represents Minecraft (Java Edition) Version 1.18
     * (The "Caves and Cliffs: Part II" Update)
     */
    MINECRAFT_1_18(18, "1.18.x"),

    /**
     * This constant represents Minecraft (Java Edition) Version 1.19
     * ("The Wild Update")
     */
    MINECRAFT_1_19(19, "1.19.x"),

    /**
     * This constant represents Minecraft (Java Edition) Version 1.20
     * ("The Trails &amp; Tales Update")
     */
    MINECRAFT_1_20(20, "1.20.x"),

    /**
     * This constant represents Minecraft (Java Edition) Version 1.20.5
     * ("The Armored Paws Update")
     */
    MINECRAFT_1_20_5(20, 5, "1.20.5+"),

    /**
     * This constant represents an exceptional state in which we were unable
     * to identify the Minecraft Version we are using
     */
    UNKNOWN("Unknown", true);

    @Getter
    private final String name;
    @Getter
    private final boolean virtual;
    private final int majorVersion;
    private final int minorVersion;

    /**
     * This constructs a new {@link MinecraftVersion} with the given name.
     * This constructor forces the {@link MinecraftVersion} to be real.
     * It must be a real version of Minecraft.
     *
     * @param majorVersion The major version of minecraft as an {@link Integer}
     * @param name         The display name of this {@link MinecraftVersion}
     */
    MinecraftVersion(int majorVersion, String name) {
        this.name = name;
        this.majorVersion = majorVersion;
        this.minorVersion = -1;
        this.virtual = false;
    }

    /**
     * This constructs a new {@link MinecraftVersion} with the given name.
     * This constructor forces the {@link MinecraftVersion} to be real.
     * It must be a real version of Minecraft.
     *
     * @param majorVersion The major (minor in semver, major in MC land) version of minecraft as an {@link Integer}
     * @param minor        The minor (patch in semver, minor in MC land) version of minecraft as an {@link Integer}
     * @param name         The display name of this {@link MinecraftVersion}
     */
    MinecraftVersion(int majorVersion, int minor, String name) {
        this.name = name;
        this.majorVersion = majorVersion;
        this.minorVersion = minor;
        this.virtual = false;
    }

    /**
     * This constructs a new {@link MinecraftVersion} with the given name.
     * A virtual {@link MinecraftVersion} (unknown or unit test) is not an actual
     * version of Minecraft but rather a state of the {@link Server} software.
     *
     * @param name    The display name of this {@link MinecraftVersion}
     * @param virtual Whether this {@link MinecraftVersion} is virtual
     */
    MinecraftVersion(String name, boolean virtual) {
        this.name = name;
        this.majorVersion = 0;
        this.minorVersion = -1;
        this.virtual = virtual;
    }

    /**
     * This tests if the given minecraft version number matches with this
     * {@link MinecraftVersion}.
     * <p>
     * You can obtain the version number by doing {@link PaperLib#getMinecraftVersion()}.
     * It is equivalent to the "major" version
     * <p>
     * Example: {@literal "1.13"} returns {@literal 13}
     *
     * @param minecraftVersion The {@link Integer} version to match
     * @return Whether this {@link MinecraftVersion} matches the specified version id
     */
    public boolean isMinecraftVersion(int minecraftVersion) {
        return this.isMinecraftVersion(minecraftVersion, -1);
    }

    /**
     * This tests if the given minecraft version matches with this
     * {@link MinecraftVersion}.
     * <p>
     * You can obtain the version number by doing {@link PaperLib#getMinecraftVersion()}.
     * It is equivalent to the "major" version<br />
     * You can obtain the patch version by doing {@link PaperLib#getMinecraftPatchVersion()}.
     * It is equivalent to the "minor" version
     * <p>
     * Example: {@literal "1.13"} returns {@literal 13}<br />
     * Exampe: {@literal "1.13.2"} returns {@literal 13_2}
     *
     * @param minecraftVersion The {@link Integer} version to match
     * @return Whether this {@link MinecraftVersion} matches the specified version id
     */
    public boolean isMinecraftVersion(int minecraftVersion, int patchVersion) {
        if (isVirtual()) {
            return false;
        }

        if (this.majorVersion != 20) {
            return this.majorVersion == minecraftVersion && this.minorVersion >= patchVersion;
        } else {
            return this.majorVersion == minecraftVersion && this.minorVersion == -1
                    ? patchVersion < 5
                    : patchVersion >= minorVersion;
        }
    }

    /**
     * This method checks whether this {@link MinecraftVersion} is newer or equal to
     * the given {@link MinecraftVersion},
     *
     * An unknown version will default to {@literal false}.
     *
     * @param version The {@link MinecraftVersion} to compare
     * @return Whether this {@link MinecraftVersion} is newer or equal to the given {@link MinecraftVersion}
     */
    public boolean isAtLeast(MinecraftVersion version) {
        Validate.notNull(version, "A Minecraft version cannot be null!");

        if (this == UNKNOWN) {
            return false;
        }

        return this.ordinal() >= version.ordinal();
    }

    /**
     * This checks whether this {@link MinecraftVersion} is older than the specified {@link MinecraftVersion}.
     *
     * An unknown version will default to {@literal true}.
     *
     * @param version The {@link MinecraftVersion} to compare
     * @return Whether this {@link MinecraftVersion} is older than the given one
     */
    public boolean isBefore(MinecraftVersion version) {
        Validate.notNull(version, "A Minecraft version cannot be null!");

        if (this == UNKNOWN) {
            return true;
        }

        return version.ordinal() > this.ordinal();
    }
}
