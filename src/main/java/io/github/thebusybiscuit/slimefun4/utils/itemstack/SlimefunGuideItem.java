package io.github.thebusybiscuit.slimefun4.utils.itemstack;

import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.ArrayList;
import java.util.List;

import me.qscbm.slimefun4.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This is just a helper {@link ItemStack} class for the {@link SlimefunGuide} {@link ItemStack}.
 *
 * @author TheBusyBiscuit
 * @see SlimefunGuide
 * @see SlimefunGuideImplementation
 */
public class SlimefunGuideItem extends ItemStack {
    public static List<Component> lore = new ArrayList<>();

    static {
        lore.add(TextUtils.fromText("&e右键 &8\u21E8 &7浏览物品"));
        lore.add(TextUtils.fromText("&eShift + 右键 &8\u21E8 &7打开 设置 / 关于"));
    }

    public SlimefunGuideItem(SlimefunGuideImplementation implementation, String name) {
        super(Material.ENCHANTED_BOOK);
        ItemMeta meta = getItemMeta();
        meta.displayName(TextUtils.fromText(name));
        SlimefunGuideMode type = implementation.getMode();
        meta.lore(lore);
        PersistentDataAPI.setString(meta, Slimefun.getRegistry().getGuideDataKey(), type.name());
        Slimefun.getItemTextureService().setTexture(meta, "SLIMEFUN_GUIDE");
        setItemMeta(meta);
    }
}
