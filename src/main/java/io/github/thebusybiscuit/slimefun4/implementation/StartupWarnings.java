package io.github.thebusybiscuit.slimefun4.implementation;

import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class stores some startup warnings we occasionally need to print.
 * If you setup your server the recommended way, you are never going to see
 * any of these messages.
 *
 * @author TheBusyBiscuit
 *
 */
final class StartupWarnings {
    private static final String BORDER = "****************************************************";
    private static final String PREFIX = "* ";

    private StartupWarnings() {}

    static void discourageCSCoreLib(Logger logger) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "请卸载 CS-CoreLib。");
        logger.log(Level.SEVERE, BORDER);
    }

    static void invalidMinecraftVersion(Logger logger, int majorVersion, String slimefunVersion) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "Slimefun 未被正确安装!");
        logger.log(Level.SEVERE, PREFIX + "你正在使用不支持的 Minecraft 版本!");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "你正在使用 Minecraft 1.{0}.x", majorVersion);
        logger.log(Level.SEVERE, PREFIX + "但 Slimefun {0} 只支持", slimefunVersion);
        logger.log(Level.SEVERE, PREFIX + "Minecraft {0}", String.join(" / ", Slimefun.getSupportedVersions())) ;
        logger.log(Level.SEVERE, BORDER);
    }

    static void oldJavaVersion(Logger logger, int recommendedJavaVersion) {
        int javaVersion = NumberUtils.getJavaVersion();

        logger.log(Level.WARNING, BORDER);
        logger.log(Level.WARNING, PREFIX + "正在使用的 Java 版本 (Java {0}) 已过时.", javaVersion);
        logger.log(Level.WARNING, PREFIX);
        logger.log(Level.WARNING, PREFIX + "由于高版本 Minecraft 对 Java {0} 的强制依赖,", recommendedJavaVersion);
        logger.log(Level.WARNING, PREFIX + "我们推荐您尽快升级到 Java {0}.", recommendedJavaVersion);
        logger.log(Level.WARNING, PREFIX + "同时，为尽快使用到新版本Java带来的特性,");
        logger.log(Level.WARNING, PREFIX + "Slimefun 也会在不久的将来依赖于 Java {0}.", recommendedJavaVersion);
        logger.log(Level.WARNING, PREFIX + "为了不影响您以后的正常使用，请尽快更新!");
        logger.log(Level.WARNING, BORDER);
    }
}
