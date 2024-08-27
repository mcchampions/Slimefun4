package net.guizhanss.slimefun4.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.JsonUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.plugin.Plugin;

/**
 * 提供Wiki相关实用方法
 *
 * @author ybw0014
 */
public final class WikiUtils {
    private WikiUtils() {}

    /**
     * 读取附属的 wiki.json 并设置物品的 Wiki 按钮
     *
     * @param addon 附属 {@link SlimefunAddon} 实例
     */
    public static void setupJson(Plugin addon) {
        setupJson(addon, page -> page);
    }

    /**
     * 读取附属的 wiki.json 并设置物品的 Wiki 按钮
     * 可对页面地址进行更改
     *
     * @param plugin 附属 {@link SlimefunAddon} 实例
     * @param formatter 对页面地址进行更改
     */
    public static void setupJson(Plugin plugin, Function<String, String> formatter) {
        if (!(plugin instanceof SlimefunAddon)) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(plugin.getClass().getResourceAsStream("/wiki.json"), StandardCharsets.UTF_8))) {
            JsonElement element = JsonUtils.parseString(reader.lines().collect(Collectors.joining("")));
            JsonObject json = element.getAsJsonObject();

            int count = 0;

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                SlimefunItem item = SlimefunItem.getById(entry.getKey());

                if (item != null) {
                    String page = entry.getValue().getAsString();
                    page = formatter.apply(page);
                    item.addWikiPage(page);
                    count++;
                }
            }

            plugin.getLogger()
                    .log(Level.INFO, MessageFormat.format("加载了 {0} 中 {1} 个物品的 Wiki 页面", plugin.getName(), count));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, MessageFormat.format("无法加载 {0} 的 wiki.json", plugin.getName()), e);
        }
    }
}
