package me.qscbm.slimefun4.integrations;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.utils.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 对 Geyser 的头颅支持
 * <p>
 * 这个实现很委曲求全了
 * <p>
 * 如果使用API的话
 * 那么无法加载附属物品——事件在附属加载完就已经结束了
 * 如果改配置的话
 * 那么需要重启之后才能生效
 */
public class GeyserIntegration {
    private File customSkullsFile;

    public void register() {
        long start = System.nanoTime();
        if (Bukkit.getPluginManager().getPlugin("Geyser-Spigot") == null) {
            return;
        }
        Slimefun.logger().info("开始加载自定义粘液科技Geyser支持");

        try {
            File geyserFolder = new File("plugins/Geyser-Spigot/");
            customSkullsFile = new File(geyserFolder, "custom-skulls.yml");
            if (!geyserFolder.exists()) {
                customSkullsFile.createNewFile();
                geyserFolder.mkdirs();
            }
        } catch (Exception e) {
            Slimefun.logger().warning("加载自定义粘液科技Geyser支持时发生错误");
            throw new RuntimeException(e);
        }

        start();

        Slimefun.logger().info("加载共耗时" + (System.nanoTime() - start) / 1000000 + "ms");
        Slimefun.logger().warning("完成加载自定义粘液科技Geyser支持,如果不生效请重启服务器");
    }

    public void start() {
        try {
            Config config = new Config(customSkullsFile);
            List<String> l = config.getStringList("skin-hashes");
            int successSkullCount = 0;
            int errorSkullCount = 0;
            for (SlimefunItem slimefunItem : Slimefun.getRegistry().getAllSlimefunItems()) {
                ItemStack itemStack = slimefunItem.getItem();
                Material type = itemStack.getType();
                if (type != Material.PLAYER_HEAD) {
                    continue;
                }
                ItemMeta m = itemStack.getItemMeta();
                if (!(m instanceof SkullMeta meta)) {
                    Slimefun.logger().warning("[SlimefunGeyser支持]错误的ItemMetaType:" + m.getClass().getName());
                    errorSkullCount++;
                    continue;
                }
                String textureCode = NBTUtils.getTexture(meta);
                if (textureCode == null) {
                    errorSkullCount++;
                    continue;
                }
                String[] ts = textureCode.split("/");
                String hash = ts[ts.length - 1];
                if (!l.contains(hash)) {
                    l.add(hash);
                }
                successSkullCount++;
            }
            String[] i = {"player-names", "player-uuids", "player-profiles"};
            for (String t : i) {
                if (config.getStringList(t).isEmpty()) {
                    config.setValue(t, new ArrayList<>());
                }
            }
            config.setValue("skin-hashes", l);
            config.save();
            Slimefun.logger().info("成功加载" + successSkullCount + "个自定义头颅");
            Slimefun.logger().warning("加载失败" + errorSkullCount + "个自定义头颅");
        } catch (Exception e) {
            Slimefun.logger().warning("加载自定义粘液科技Geyser支持时发生错误");
            throw new RuntimeException(e);
        }
    }
}
