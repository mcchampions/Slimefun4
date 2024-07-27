package io.github.thebusybiscuit.slimefun4.core.guide.options;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.services.LocalizationService;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This static utility class offers various methods that provide access to the
 * Settings menu of our {@link SlimefunGuide}.
 * <p>
 * This menu is used to allow a {@link Player} to change things such as the {@link Language}.
 *
 * @author TheBusyBiscuit
 *
 * @see SlimefunGuide
 *
 */
public final class SlimefunGuideSettings {
    private static final int[] BACKGROUND_SLOTS = {
        1, 2, 3, 5, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 48,
        50, 52, 53
    };
    private static final List<SlimefunGuideOption<?>> options = new ArrayList<>();

    static {
        options.add(new GuideModeOption());
        options.add(new FireworksOption());
        options.add(new LearningAnimationOption());
    }

    private SlimefunGuideSettings() {}

    public static <T> void addOption(SlimefunGuideOption<T> option) {
        options.add(option);
    }

    public static void openSettings(Player p, ItemStack guide) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.settings"));

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(SoundEffect.GUIDE_OPEN_SETTING_SOUND::playFor);

        ChestMenuUtils.drawBackground(menu, BACKGROUND_SLOTS);

        addHeader(p, menu, guide);
        addConfigurableOptions(p, menu, guide);

        menu.open(p);
    }

    private static void addHeader(Player p, ChestMenu menu, ItemStack guide) {
        LocalizationService locale = Slimefun.getLocalization();

        menu.addItem(
                0,
                new CustomItemStack(
                        SlimefunGuide.getItem(SlimefunGuideMode.SURVIVAL_MODE),
                        "&e\u21E6 " + locale.getMessage(p, "guide.back.title"),
                        "",
                        "&7" + locale.getMessage(p, "guide.back.guide")));

        menu.addMenuClickHandler(0, (pl, slot, item, action) -> {
            SlimefunGuide.openGuide(pl, guide);
            return false;
        });

        menu.addItem(
                4,
                new CustomItemStack(
                        Material.WRITABLE_BOOK,
                        ChatColor.GREEN + locale.getMessage(p, "guide.title.versions"),
                        "&7&o" + locale.getMessage(p, "guide.tooltips.versions-notice"),
                        "",
                        "&f汉化 By StarWishsama",
                        "&c请不要将此版本信息截图到 Discord/Github 反馈 Bug",
                        "&c而是优先到汉化页面反馈",
                        "",
                        "&fMinecraft: &a" + Bukkit.getBukkitVersion(),
                        "&fSlimefun: &a" + Slimefun.getVersion()),
                ChestMenuUtils.getEmptyClickHandler());

        menu.addItem(
                6,
                new CustomItemStack(
                        Material.COMPARATOR,
                        "&e" + locale.getMessage(p, "guide.title.source"),
                        "",
                        "&7&oSlimefun 4 是一个由社区参与的项目,",
                        "&7&o源代码可以在 GitHub 上找到",
                        "&7&o如果你想让这个项目持续下去",
                        "&7&o你可以考虑对项目做出贡献",
                        "",
                        "&7\u21E8 &e点击前往汉化版 GitHub 仓库"));

        menu.addMenuClickHandler(6, (pl, slot, item, action) -> {
            pl.closeInventory();
            ChatUtils.sendURL(pl, "https://github.com/mcchampions/Slimefun4");
            return false;
        });

        menu.addItem(
                8,
                new CustomItemStack(
                        Material.KNOWLEDGE_BOOK,
                        "&3" + locale.getMessage(p, "guide.title.wiki"),
                        "",
                        "&7你需要对物品或机器方面的帮助吗?",
                        "&7你不知道要干什么?",
                        "&7查看我们的由社区维护的维基",
                        "&7并考虑成为一名编辑者!",
                        "",
                        "&7\u21E8 &e点击前往非官方中文 Wiki"));

        menu.addMenuClickHandler(8, (pl, slot, item, action) -> {
            pl.closeInventory();
            ChatUtils.sendURL(pl, "https://slimefun-wiki.guizhanss.cn/");
            return false;
        });

        menu.addItem(
                47,
                new CustomItemStack(
                        Material.BOOKSHELF,
                        "&3" + locale.getMessage(p, "guide.title.addons"),
                        "",
                        "&7Slimefun 是一个大型项目，但附属插件的存在",
                        "&7能让 Slimefun 真正的发光发亮",
                        "&7看一看它们，也许你要寻找的附属插件就在那里!",
                        "",
                        "&7该服务器已安装附属插件: &b" + Slimefun.getInstalledAddons().size(),
                        "",
                        "&7\u21E8 &e点击查看 Slimefun4 可用的附属插件"));

        menu.addMenuClickHandler(47, (pl, slot, item, action) -> {
            pl.closeInventory();
            ChatUtils.sendURL(pl, "https://slimefun-wiki.guizhanss.cn/Addons");
            return false;
        });

        menu.addItem(
            49,
            new CustomItemStack(
                Material.REDSTONE_TORCH,
                "&4" + locale.getMessage(p, "guide.title.bugs"),
                "",
                "&7&oBug reports have to be made in English!",
                "",
                "&7\u21E8 &eClick to go to the Slimefun4 Bug Tracker"));

        menu.addMenuClickHandler(49, (pl, slot, item, action) -> {
            pl.closeInventory();
            ChatUtils.sendURL(pl, "https://github.com/mcchampions/Slimefun4/issues");
            return false;
        });

        menu.addItem(
                51,
                new CustomItemStack(
                        Material.TOTEM_OF_UNDYING, ChatColor.RED + locale.getMessage(p, "guide.work-in-progress")),
                (pl, slot, item, action) -> {
                    // Add something here
                    return false;
                });
    }

    private static void addConfigurableOptions(Player p, ChestMenu menu, ItemStack guide) {
        int i = 19;

        for (SlimefunGuideOption<?> option : options) {
            Optional<ItemStack> item = option.getDisplayItem(p, guide);

            if (item.isPresent()) {
                menu.addItem(i, item.get());
                menu.addMenuClickHandler(i, (pl, slot, stack, action) -> {
                    option.onClick(p, guide);
                    return false;
                });

                i++;
            }
        }
    }

    /**
     * This method checks if the given {@link Player} has enabled the {@link FireworksOption}
     * in their {@link SlimefunGuide}.
     * If they enabled this setting, they will see fireworks when they unlock a {@link Research}.
     *
     * @param p
     *            The {@link Player}
     *
     * @return Whether this {@link Player} wants to see fireworks when unlocking a {@link Research}
     */
    public static boolean hasFireworksEnabled(Player p) {
        return getOptionValue(p, FireworksOption.class, true);
    }

    /**
     * This method checks if the given {@link Player} has enabled the {@link LearningAnimationOption}
     * in their {@link SlimefunGuide}.
     * If they enabled this setting, they will see messages in chat about the progress of their {@link Research}.
     *
     * @param p
     *            The {@link Player}
     *
     * @return Whether this {@link Player} wants to info messages in chat when unlocking a {@link Research}
     */
    public static boolean hasLearningAnimationEnabled(Player p) {
        return getOptionValue(p, LearningAnimationOption.class, true);
    }

    /**
     * Helper method to get the value of a {@link SlimefunGuideOption} that the {@link Player}
     * has set in their {@link SlimefunGuide}
     *
     * @param p
     *            The {@link Player}
     * @param optionsClass
     *            Class of the {@link SlimefunGuideOption} to get the value of
     * @param defaultValue
     *            Default value to return in case the option is not found at all or has no value set
     * @param <T>
     *            Type of the {@link SlimefunGuideOption}
     * @param <V>
     *            Type of the {@link SlimefunGuideOption} value
     *
     * @return The value of given {@link SlimefunGuideOption}
     */

    private static <T extends SlimefunGuideOption<V>, V> V getOptionValue(
            Player p, Class<T> optionsClass, V defaultValue) {
        for (SlimefunGuideOption<?> option : options) {
            if (optionsClass.isInstance(option)) {
                T o = optionsClass.cast(option);
                ItemStack guide = SlimefunGuide.getItem(SlimefunGuideMode.SURVIVAL_MODE);
                return o.getSelectedOption(p, guide).orElse(defaultValue);
            }
        }

        return defaultValue;
    }
}
