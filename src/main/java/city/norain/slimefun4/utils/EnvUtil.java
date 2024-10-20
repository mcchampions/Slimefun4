package city.norain.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EnvUtil {
    public static Properties gitInfo = null;

    public void init() {
        try (var resource = Slimefun.class.getResourceAsStream("/git.properties")) {
            var prop = new Properties();
            prop.load(resource);

            gitInfo = prop;
        } catch (IOException e) {
            Slimefun.logger().log(Level.WARNING, "无法加载构建信息", e);
        }
    }

    private String getProperty(String key) {
        return gitInfo == null ? "null" : gitInfo.getProperty(key);
    }

    public String getBuildTime() {
        return getProperty("git.build.time");
    }

    public String getBuildCommitID() {
        return getProperty("git.commit.id.abbrev");
    }

    public String getBranch() {
        return getProperty("git.branch");
    }
}
