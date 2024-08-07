package city.norain.slimefun4;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.logging.Level;
import lombok.Getter;

public final class SlimefunExtended {
    @Getter
    private final static boolean databaseDebugMode = false;

    public static boolean checkEnvironment(Slimefun sf) {
        if (Slimefun.getConfigManager().isBypassEnvironmentCheck()) {
            return true;
        } else {
            if (EnvironmentChecker.checkHybridServer()) {
                sf.getLogger().log(Level.WARNING, "检测到正在使用混合端, Slimefun 将会被禁用!");
            }
            return !EnvironmentChecker.checkIncompatiblePlugins(sf.getLogger());
        }
    }

    public static void init(Slimefun sf) {
        VaultIntegration.register(sf);
    }

    public static void shutdown() {
        VaultIntegration.cleanup();
    }
}
