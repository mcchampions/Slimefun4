package city.norain.slimefun4;

import city.norain.slimefun4.utils.EnvUtil;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.logging.Level;

import lombok.Getter;

public final class SlimefunExtended {
    @Getter
    private static MinecraftVersion minecraftVersion;

    public static boolean checkEnvironment(Slimefun sf) {
        try {
            minecraftVersion = MinecraftVersion.of(sf.getServer());
        } catch (UnknownServerVersionException e) {
            sf.getLogger().log(Level.WARNING, "无法识别你正在使用的服务端版本 :(");
            return false;
        }

        return true;
    }

    public static void init(Slimefun sf) {
        EnvUtil.init();

        VaultIntegration.register(sf);
    }

    public static void shutdown() {
        VaultIntegration.cleanup();
    }
}
