package io.github.thebusybiscuit.slimefun4.api;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import org.bukkit.Server;

/**
 * This enum holds all versions of Minecraft that we currently support.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 * @see Slimefun
 */
public enum MinecraftVersion {
    MINECRAFT_1_16("16", true),
    MINECRAFT_1_17("17", true),

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
     * This constant represents Minecraft (Java Edition) Version 1.21
     * ("The Tricky Trials Update")
     */
    MINECRAFT_1_21(21, "1.21.x"),

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

    public boolean isMinecraftVersion(int minecraftVersion) {
        return this.isMinecraftVersion(minecraftVersion, -1);
    }

    public boolean isMinecraftVersion(int minecraftVersion, int patchVersion) {
        if (virtual) {
            return false;
        }

        if (this.majorVersion != minecraftVersion) {
            return false;
        }
        // the virtual ones are at the last of array, so it will not cause indexOutOfRange
        MinecraftVersion nextVersion = values()[this.ordinal() + 1];
        // checking patchVersion, if next Version is not a virtual version and it is in the same majorVersion as this,
        // then we should ensure patchVersion is lower than nextVersion
        return patchVersion >= this.minorVersion
                && (nextVersion.virtual
                    || nextVersion.majorVersion != this.majorVersion
                    || nextVersion.minorVersion > patchVersion);

        //        if (this.majorVersion == 20) {
        //            return this.minorVersion == -1 ? patchVersion < 5 : patchVersion >= this.minorVersion;
        //        } else {
        //            return this.minorVersion == -1 || patchVersion >= this.minorVersion;
        //        }
    }
    /**
     * This method checks whether this {@link MinecraftVersion} is newer or equal to
     * the given {@link MinecraftVersion},
     * <p>
     * An unknown version will default to {@literal false}.
     *
     * @param version The {@link MinecraftVersion} to compare
     * @return Whether this {@link MinecraftVersion} is newer or equal to the given {@link MinecraftVersion}
     */
    public boolean isAtLeast(MinecraftVersion version) {
        if (this == UNKNOWN) {
            return false;
        }

        return this.ordinal() >= version.ordinal();
    }

    public boolean isAtLeast(int majorVersion, int minorVersion) {
        if (this == UNKNOWN) {
            return false;
        }

        return this.majorVersion > majorVersion
                || (this.majorVersion == majorVersion && this.minorVersion >= minorVersion);
    }

    /**
     * This checks whether this {@link MinecraftVersion} is older than the specified {@link MinecraftVersion}.
     * <p>
     * An unknown version will default to {@literal true}.
     *
     * @param version The {@link MinecraftVersion} to compare
     * @return Whether this {@link MinecraftVersion} is older than the given one
     */
    public boolean isBefore(MinecraftVersion version) {
        return !isAtLeast(version);
        //        Validate.notNull(version, "A Minecraft version cannot be null!");
        //
        //        if (this == UNKNOWN) {
        //            return true;
        //        }
        //
        //        return version.ordinal() > this.ordinal();
    }

    public boolean isBefore(int majorVersion, int minorVersion) {
        return !isAtLeast(majorVersion, minorVersion);
    }
}
