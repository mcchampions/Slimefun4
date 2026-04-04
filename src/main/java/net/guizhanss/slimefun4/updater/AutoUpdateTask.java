package net.guizhanss.slimefun4.updater;

import java.io.File;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.plugin.Plugin;

/**
 * 自动更新任务.
 *
 * @author ybw0014
 */
public class AutoUpdateTask implements Runnable {
    @ParametersAreNonnullByDefault
    public AutoUpdateTask(Plugin plugin, File file) {}

    @Override
    public void run() {}
    @Nullable private String getBranch() {
        return null;
    }
}
