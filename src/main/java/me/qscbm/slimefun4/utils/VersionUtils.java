package me.qscbm.slimefun4.utils;

import org.bukkit.Bukkit;

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
            } catch (Exception ignored) {}
            if (matchResult.groupCount() >= 3) {
                try {
                    patchVersion = Integer.parseInt(matchResult.group(3), 10);
                } catch (Exception ignored) {}
            }
            if (matchResult.groupCount() >= 5) {
                try {
                    final int ver = Integer.parseInt(matcher.group(5));
                    if (matcher.group(4).toLowerCase(Locale.ENGLISH).contains("pre")) {
                        preReleaseVersion = ver;
                    } else {
                        releaseCandidateVersion = ver;
                    }
                } catch (Exception ignored) {}
            }
        }
        MINECRAFT_VERSION = version;
        MINECRAFT_PATCH_VERSION = patchVersion;
        MINECRAFT_PRE_RELEASE_VERSION = preReleaseVersion;
        MINECRAFT_RELEASE_CANDIDATE_VERSION = releaseCandidateVersion;
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
