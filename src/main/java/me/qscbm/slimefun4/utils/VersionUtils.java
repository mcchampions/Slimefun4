package me.qscbm.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {
    private static final String BUKKIT_VERSION = Bukkit.getVersion();
    private static final Pattern VERSION_PATTERN = Pattern.compile("(?i)\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?(?: (Pre-Release|Release Candidate) )?(\\d)?\\)");
    private static final int MINECRAFT_VERSION;
    private static final int MINECRAFT_PATCH_VERSION;
    private static final int MINECRAFT_PRE_RELEASE_VERSION;
    private static final int MINECRAFT_RELEASE_CANDIDATE_VERSION;
    private static Method GET_TOP_INVENTORY = null;
    static {
        Matcher matcher = VERSION_PATTERN.matcher(BUKKIT_VERSION);
        int version = 0;
        int patchVersion = 0;
        int preReleaseVersion = -1;
        int releaseCandidateVersion = -1;
        if (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            try {
                version = Integer.parseInt(matchResult.group(2), 10);
                GET_TOP_INVENTORY =
                        Class.forName("org.bukkit.inventory.InventoryView").getMethod("getTopInventory");
                GET_TOP_INVENTORY.setAccessible(true);
            } catch (Exception ignored) {
            }
            if (matchResult.groupCount() >= 3) {
                try {
                    patchVersion = Integer.parseInt(matchResult.group(3), 10);
                } catch (Exception ignored) {
                }
            }
            if (matchResult.groupCount() >= 5) {
                try {
                    final int ver = Integer.parseInt(matcher.group(5));
                    if (matcher.group(4).toLowerCase(Locale.ENGLISH).contains("pre")) {
                        preReleaseVersion = ver;
                    } else {
                        releaseCandidateVersion = ver;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        MINECRAFT_VERSION = version;
        MINECRAFT_PATCH_VERSION = patchVersion;
        MINECRAFT_PRE_RELEASE_VERSION = preReleaseVersion;
        MINECRAFT_RELEASE_CANDIDATE_VERSION = releaseCandidateVersion;
    }

    public static Inventory getTopInventory(InventoryEvent event) {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            return event.getView().getTopInventory();
        } else {
            if (GET_TOP_INVENTORY == null) {
                throw new IllegalStateException("Unable to get top inventory: missing method");
            }

            try {
                return (Inventory) GET_TOP_INVENTORY.invoke(event.getView());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getBukkitVersion() {
        return BUKKIT_VERSION;
    }

    public static int getMinecraftVersion() {
        return MINECRAFT_VERSION;
    }

    public static int getMinecraftPatchVersion() {
        return MINECRAFT_PATCH_VERSION;
    }

    public static int getMinecraftPreReleaseVersion() {
        return MINECRAFT_PRE_RELEASE_VERSION;
    }

    public static int getMinecraftReleaseCandidateVersion() {
        return MINECRAFT_RELEASE_CANDIDATE_VERSION;
    }
}
