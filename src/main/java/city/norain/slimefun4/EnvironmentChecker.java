package city.norain.slimefun4;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class EnvironmentChecker {
    private static final List<String> UNSUPPORTED_PLUGINS = List.of(
            "BedrockTechnology", "SlimefunFix", "SlimefunBugFixer", "Slimefunbookfix", "PlaceItemsOnGroundRebuilt");

    static boolean checkIncompatiblePlugins(Logger logger) {
         return false;
    }

    static boolean checkHybridServer() {
        return false;
    }

    static void scheduleSlimeGlueCheck(Slimefun sf) {}

    private static void printBorder(Logger logger) {
        logger.log(Level.WARNING, "#######################################################");
    }
}
