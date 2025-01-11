package io.github.thebusybiscuit.slimefun4.utils;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.Arrays;
import java.util.List;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.MenuClickHandler;
import me.qscbm.slimefun4.message.QsTextComponentImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public final class ChestMenuUtils {
    private ChestMenuUtils() {
    }

    private static final ItemStack UI_BACKGROUND =
            new SlimefunItemStack("_UI_BACKGROUND", Material.GRAY_STAINED_GLASS_PANE, " ");
    private static final ItemStack INPUT_SLOT =
            new SlimefunItemStack("_UI_INPUT_SLOT", Material.CYAN_STAINED_GLASS_PANE, " ");
    private static final ItemStack OUTPUT_SLOT =
            new SlimefunItemStack("_UI_OUTPUT_SLOT", Material.ORANGE_STAINED_GLASS_PANE, " ");

    private static final ItemStack NO_PERMISSION =
            new SlimefunItemStack("_UI_NO_PERMISSION", Material.BARRIER, "No Permission");
    private static final ItemStack NOT_RESEARCHED =
            new SlimefunItemStack("_UI_NOT_RESEARCHED", Material.BARRIER, "Not researched");

    private static final ItemStack BACK_BUTTON = new SlimefunItemStack(
            "_UI_BACK", Material.ENCHANTED_BOOK, "&7\u21E6 Back", meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
    private static final ItemStack MENU_BUTTON = new SlimefunItemStack(
            "_UI_MENU", Material.COMPARATOR, "&eSettings / Info", "", "&7\u21E8 Click to see more");
    private static final ItemStack SEARCH_BUTTON = new SlimefunItemStack("_UI_SEARCH", Material.NAME_TAG, "&bSearch");
    private static final ItemStack WIKI_BUTTON =
            new SlimefunItemStack("_UI_WIKI", Material.KNOWLEDGE_BOOK, "&3Slimefun Wiki");

    private static final ItemStack PREV_BUTTON_ACTIVE =
            new SlimefunItemStack("_UI_PREVIOUS_ACTIVE", Material.LIME_STAINED_GLASS_PANE, "&r\u21E6 Previous Page");
    private static final ItemStack NEXT_BUTTON_ACTIVE =
            new SlimefunItemStack("_UI_NEXT_ACTIVE", Material.LIME_STAINED_GLASS_PANE, "&rNext Page \u21E8");
    private static final ItemStack PREV_BUTTON_INACTIVE =
            new SlimefunItemStack("_UI_PREVIOUS_INACTIVE", Material.BLACK_STAINED_GLASS_PANE, "§8\u21E6 Previous Page");
    private static final ItemStack NEXT_BUTTON_INACTIVE =
            new SlimefunItemStack("_UI_NEXT_INACTIVE", Material.BLACK_STAINED_GLASS_PANE, "§8Next Page \u21E8");

    private static final MenuClickHandler CLICK_HANDLER = (p, s, i, a) -> false;

    public static ItemStack getBackground() {
        return UI_BACKGROUND;
    }

    public static ItemStack getNoPermissionItem() {
        return NO_PERMISSION;
    }

    public static ItemStack getNotResearchedItem() {
        return NOT_RESEARCHED;
    }

    public static ItemStack getInputSlotTexture() {
        return INPUT_SLOT;
    }

    public static ItemStack getOutputSlotTexture() {
        return OUTPUT_SLOT;
    }

    public static MenuClickHandler getEmptyClickHandler() {
        return CLICK_HANDLER;
    }

    public static ItemStack getBackButton(Player p, String... lore) {
        return new CustomItemStack(
                BACK_BUTTON, "&7\u21E6 " + Slimefun.getLocalization().getMessage(p, "guide.back.title"), lore);
    }

    public static ItemStack getMenuButton(Player p) {
        return new CustomItemStack(
                MENU_BUTTON,
                ChatColor.YELLOW + Slimefun.getLocalization().getMessage(p, "guide.title.settings"),
                "",
                "&7\u21E8 " + Slimefun.getLocalization().getMessage(p, "guide.tooltips.open-itemgroup"));
    }

    private static final QsTextComponentImpl GRAY_SEARCH_BUTTON_DISPLAY_NAME = new QsTextComponentImpl("搜索...").color(NamedTextColor.GRAY);

    private static final List<Component> SEARCH_BUTTON_LORE =
            Arrays.asList(Component.empty(),
                    new QsTextComponentImpl("\u21E8 ").color(NamedTextColor.GRAY).append(new QsTextComponentImpl("单击搜索物品").color(NamedTextColor.AQUA))
            );

    public static ItemStack getSearchButton(Player p) {
        return new CustomItemStack(SEARCH_BUTTON, meta -> {
            meta.displayName(GRAY_SEARCH_BUTTON_DISPLAY_NAME);

            meta.lore(SEARCH_BUTTON_LORE);
        });
    }

    public static ItemStack getWikiButton() {
        return WIKI_BUTTON;
    }

    private static final QsTextComponentImpl GRAY_PREVIOUS_BUTTON_DISPLAY_NAME =
            new QsTextComponentImpl("\u21E6 上一页").color(NamedTextColor.DARK_GRAY);

    private static final QsTextComponentImpl WHITEY_PREVIOUS_BUTTON_DISPLAY_NAME =
            new QsTextComponentImpl("\u21E6 上一页").color(NamedTextColor.WHITE);

    private static final QsTextComponentImpl DARK_NEXT_BUTTON_DISPLAY_NAME =
            new QsTextComponentImpl("\u21E6 下一页 \u21E8").color(NamedTextColor.DARK_GRAY);
    private static final QsTextComponentImpl WHITE_NEXT_BUTTON_DISPLAY_NAME =
            new QsTextComponentImpl("\u21E6 下一页 \u21E8").color(NamedTextColor.WHITE);

    public static ItemStack getPreviousButton(Player p, int page, int pages) {
        if (pages == 1 || page == 1) {
            return new CustomItemStack(PREV_BUTTON_INACTIVE, meta -> {
                meta.displayName(GRAY_PREVIOUS_BUTTON_DISPLAY_NAME);
                meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
            });
        } else {
            return new CustomItemStack(PREV_BUTTON_ACTIVE, meta -> {
                meta.displayName(WHITEY_PREVIOUS_BUTTON_DISPLAY_NAME);
                meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
            });
        }
    }

    public static ItemStack getNextButton(Player p, int page, int pages) {
        if (pages == 1 || page == pages) {
            return new CustomItemStack(NEXT_BUTTON_INACTIVE, meta -> {
                meta.displayName(DARK_NEXT_BUTTON_DISPLAY_NAME);
                meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
            });
        } else {
            return new CustomItemStack(NEXT_BUTTON_ACTIVE, meta -> {
                meta.displayName(WHITE_NEXT_BUTTON_DISPLAY_NAME);
                meta.setLore(Arrays.asList("", ChatColor.GRAY + "(" + page + " / " + pages + ")"));
            });
        }
    }

    public static void drawBackground(ChestMenu menu, int... slots) {
        for (int slot : slots) {
            menu.addItem(slot, UI_BACKGROUND, CLICK_HANDLER);
        }
    }

    public static void updateProgressbar(
            ChestMenu menu, int slot, int timeLeft, int time, ItemStack indicator) {
        Inventory inv = menu.toInventory();

        // We don't need to update the progress bar if noone is watching :o
        if (inv == null || inv.getViewers().isEmpty()) {
            return;
        }

        ItemStack item = indicator.clone();
        ItemMeta im = item.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        if (im instanceof Damageable damageable) {
            damageable.setDamage(getDurability(item, timeLeft, time));
        }

        im.displayName(Component.empty());
        im.setLore(Arrays.asList(
                getProgressBar(timeLeft, time), "", ChatColor.GRAY + NumberUtils.getTimeLeft(timeLeft / 2)));
        item.setItemMeta(im);

        menu.replaceExistingItem(slot, item);
    }

    public static String getProgressBar(int time, int total) {
        StringBuilder builder = new StringBuilder();
        float percentage = Math.round(((((total - time) * 100.0F) / total) * 100.0F) / 100.0F);

        builder.append(NumberUtils.getColorFromPercentage(percentage));

        int rest = 20;
        for (int i = (int) percentage; i >= 5; i = i - 5) {
            builder.append(':');
            rest--;
        }

        builder.append("&7");

        builder.append(":".repeat(Math.max(0, rest)));

        builder.append(" - ").append(percentage).append('%');
        return ChatColors.color(builder.toString());
    }

    private static short getDurability(ItemStack item, int timeLeft, int max) {
        return (short) ((item.getType().getMaxDurability() / max) * timeLeft);
    }
}
