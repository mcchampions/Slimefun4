package me.qscbm.slimefun4.helper;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemHelper {
    public static final Map<String, String> ITEM_NAME_MAPPER = new HashMap<>();

    public void load() {
        Config config = new Config(Slimefun.instance(), "vanilla_items.yml");
        if (!config.getFile().exists()) {
            Slimefun.instance().saveResource("vanilla_items.yml", false);
            config.reload();
        }
        Set<String> keys = config.getKeys();
        for (String key : keys) {
            ITEM_NAME_MAPPER.put(key.toLowerCase(), config.getString(key));
        }
    }

    public String getItemName(ItemStack stack) {
        if (stack instanceof SlimefunItemStack sfItem) {
            return sfItem.getDisplayName();
        }
        ItemStackWrapper wrapper = ItemStackWrapper.wrap(stack);
        if (wrapper.hasItemMeta()) {
            //noinspection deprecation
            return wrapper.getItemMeta().getDisplayName();
        }
        String type = wrapper.getType().name();
        return ITEM_NAME_MAPPER.getOrDefault(type.toLowerCase(), "Unknown Item(" + type + ")");
    }
}
