package me.qscbm.slimefun4.integrations;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.utils.NBTUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeyserIntegration {
    public void register() {
        Slimefun.logger().info("开始加载自定义粘液科技Geyser支持");

        Config config = new Config(new File("plugins/Geyser-Spigot/custom-skulls.yml"));
        List<String> l = config.getStringList("skin-hashes");
        int successCount = 0;
        int errorCount = 0;
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getAllSlimefunItems()) {
            ItemStack itemStack = slimefunItem.getItem();
            if (itemStack.getType() != Material.PLAYER_HEAD) {
                continue;
            }
            ItemMeta m = itemStack.getItemMeta();
            if (!(m instanceof SkullMeta meta)) {
                errorCount++;
                continue;
            }
            String textureCode = NBTUtils.getTexture(meta);
            if (textureCode == null) {
                errorCount++;
                continue;
            }
            String[] ts = textureCode.split("/");
            String hash = ts[ts.length - 1];
            if (!l.contains(hash)) {
                l.add(hash);
            }
            successCount++;
        }
        String[] i = {"player-names", "player-uuids", "player-profiles"};
        for (String t : i) {
            if (config.getStringList(t).isEmpty()) {
                config.setValue(t, new ArrayList<>());
            }
        }
        config.setValue("skin-hashes", l);
        config.save();
        Slimefun.logger().info("成功加载" + successCount + "个自定义头颅");
        Slimefun.logger().info("加载失败" + errorCount + "个自定义头颅");
        Slimefun.logger().warning("完成加载自定义粘液科技Geyser支持,如果不生效请重启服务器");
    }
}
