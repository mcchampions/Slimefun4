package me.qscbm.slimefun4.integrations;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.config.SlimefunConfigManager;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.utils.NBTUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private File geyserMappingItemsFile;
    private Path geyserMappingItemsFilePath;

    public void register() {
        long start = System.nanoTime();
        Slimefun.logger().info("开始加载自定义粘液科技Geyser支持");

        try {
            File geyserFolder = new File("plugins/Geyser-Spigot/");
            customSkullsFile = new File(geyserFolder, "custom-skulls.yml");
            File geyserMappingFolder = new File("plugins/Geyser-Spigot/custom_mappings/");
            geyserMappingItemsFile = new File(geyserMappingFolder, SlimefunConfigManager.getGeyserMappingItemsFileName());
            if (!geyserFolder.exists()) {
                customSkullsFile.createNewFile();
                geyserFolder.mkdirs();
                geyserMappingFolder.createNewFile();
            }
            geyserMappingItemsFilePath = geyserMappingItemsFile.toPath();
            if (!geyserMappingItemsFile.exists()) {
                geyserMappingItemsFile.createNewFile();
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
            JSONObject jsonObject = new JSONObject("""
                    {
                        "format_version": 1,
                        "items": {
                        }
                    }""");
            JSONObject items = jsonObject.getJSONObject("items");
            Config config = new Config(customSkullsFile);
            List<String> l = config.getStringList("skin-hashes");
            int successSkullCount = 0;
            int successItemCount = 0;
            int errorSkullCount = 0;
            for (SlimefunItem slimefunItem : Slimefun.getRegistry().getAllSlimefunItems()) {
                ItemStack itemStack = slimefunItem.getItem();
                Material type = itemStack.getType();
                String name = "minecraft:" + type.name().toLowerCase(Locale.ROOT);
                JSONObject item = new JSONObject();
                String id = slimefunItem.getId();
                item.put("name", id);
                item.put("custom_model_data", Slimefun.getItemTextureService().getModelData(id));
                if (!items.has(name)) {
                    items.put(name, new JSONArray());
                }
                JSONArray array = items.getJSONArray(name);
                array.put(item);
                successItemCount++;
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
            Files.writeString(geyserMappingItemsFilePath, jsonObject.toString());
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
            Slimefun.logger().info("成功加载" + successItemCount + "个自定义物品");
        } catch (Exception e) {
            Slimefun.logger().warning("加载自定义粘液科技Geyser支持时发生错误");
            throw new RuntimeException(e);
        }
    }
}
