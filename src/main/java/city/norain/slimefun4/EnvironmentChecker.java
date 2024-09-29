package city.norain.slimefun4;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

class EnvironmentChecker {
    static boolean checkIncompatiblePlugins(Logger logger) {
        List<String> plugins = Set.of("BedrockTechnology", "SlimefunFix", "SlimefunBugFixer",
                        "Slimefunbookfix", "PlaceItemsOnGroundRebuilt").stream()
                .filter(name -> Bukkit.getServer().getPluginManager().isPluginEnabled(name))
                .toList();

        if (plugins.isEmpty()) {
            return false;
        }

        logger.log(Level.WARNING, "检测到不兼容的插件, 已自动禁用 Slimefun!");
        logger.log(Level.WARNING, "不兼容插件列表: ", String.join(", ", plugins));

        return true;
    }

}
