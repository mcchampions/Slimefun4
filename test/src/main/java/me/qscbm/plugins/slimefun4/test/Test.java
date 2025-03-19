package me.qscbm.plugins.slimefun4.test;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.plugin.java.JavaPlugin;

public final class Test extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("Slimefun")) {
            shut();
        }
        try {
            Slimefun.logger().info("Test开始");
            Slimefun.logger().info("Test结束");
            getServer().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            shut();
        }
    }

    public void shut() {
        System.exit(1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
