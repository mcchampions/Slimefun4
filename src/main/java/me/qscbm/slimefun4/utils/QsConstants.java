package me.qscbm.slimefun4.utils;

import com.xzavier0722.mc.plugin.slimefun4.storage.common.FieldKey;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.implementation.settings.GoldPanDrop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class QsConstants {
    public static final int[] EMPTY_INTS = new int[0];

    public static final ItemStack[] EMPTY_ITEM_STACKS = new ItemStack[0];

    public static final FieldKey[] EMPTY_FIELD_KEYS = new FieldKey[0];

    public static final GoldPanDrop[] EMPTY_GOLD_PAN_DROPS = new GoldPanDrop[0];

    public static final ItemSetting<?>[] EMPTY_ITEM_SETTINGS = new ItemSetting[0];

    public static final RecipeChoice[] EMPTY_RECIPE_CHOICES = new RecipeChoice[0];

    public static final Recipe[] EMPTY_RECIPES = new Recipe[0];

    public static final Material[] EMPTY_MATERIALS = new Material[0];

    public static final PotionEffect[] EMPTY_POTION_EFFECTS = new PotionEffect[0];

    public static final String[] EMPTY_STRINGS = new String[0];

    public static ItemStack GUIDE_SURVIVAL_MODE_OPTION;

    public static ItemStack GUIDE_CHEAT_MODE_OPTION;

    public static void init() {
        GUIDE_SURVIVAL_MODE_OPTION = new ItemStack(Material.CHEST);
        GUIDE_CHEAT_MODE_OPTION = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta survival_meta = GUIDE_SURVIVAL_MODE_OPTION.getItemMeta();
        survival_meta.setDisplayName(ChatColor.GRAY + "Slimefun 指南样式: " + ChatColor.YELLOW + "普通模式");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + "普通模式");
        lore.add(ChatColor.GRAY + "作弊模式");

        lore.add("");
        lore.add(ChatColor.GRAY + "\u21E8 " + ChatColor.YELLOW + "单击修改指南样式");
        survival_meta.setLore(lore);
        GUIDE_SURVIVAL_MODE_OPTION.setItemMeta(survival_meta);
        ItemMeta cheat_meta = GUIDE_SURVIVAL_MODE_OPTION.getItemMeta();
        cheat_meta.setDisplayName(ChatColor.GRAY + "Slimefun 指南样式: " + ChatColor.YELLOW + "作弊模式");
        lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "普通模式");
        lore.add(ChatColor.GREEN + "作弊模式");

        lore.add("");
        lore.add(ChatColor.GRAY + "\u21E8 " + ChatColor.YELLOW + "单击修改指南样式");
        cheat_meta.setLore(lore);
        GUIDE_SURVIVAL_MODE_OPTION.setItemMeta(cheat_meta);
    }
}
