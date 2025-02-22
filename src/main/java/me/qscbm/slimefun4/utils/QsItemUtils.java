package me.qscbm.slimefun4.utils;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QsItemUtils {
    public static final Map<String, String> ITEM_NAME_MAPPER = new HashMap<>();

    public static void load() {
        Slimefun.instance().saveResource("vanilla_items.yml", true);
        Config config = new Config(Slimefun.instance(), "vanilla_items.yml");
        Set<String> keys = config.getKeys();
        for (String key : keys) {
            ITEM_NAME_MAPPER.put(key, config.getString(key));
        }
    }

    public static String getItemName(ItemStack stack) {
        if (stack instanceof SlimefunItemStack sfItem) {
            return TextUtils.toPlainText(sfItem.getDisplayName());
        }
        ItemStackWrapper wrapper = ItemStackWrapper.wrap(stack);
        ItemMeta meta = wrapper.getItemMeta();
        if (wrapper.hasItemMeta() && meta.hasDisplayName()) {
            //noinspection deprecation
            return TextUtils.toPlainText(meta.getDisplayName());
        }

        String type = wrapper.getType().name();
        if (type.contains("POTION") || "TIPPED_ARROW".equals(type)) {
            //noinspection deprecation
            String potion = ((PotionMeta) (meta == null ? stack.getItemMeta() : meta))
                    .getBasePotionData().getType().toString();
            if (!potion.isEmpty()) {
                type = potion + "_" + type;
            }
        }
        return ITEM_NAME_MAPPER.getOrDefault(type, "Unknown Item(id:" + type + ")");
    }
}
