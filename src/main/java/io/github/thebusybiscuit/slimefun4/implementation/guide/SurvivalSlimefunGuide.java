package io.github.thebusybiscuit.slimefun4.implementation.guide;

import city.norain.slimefun4.VaultIntegration;
import io.github.bakedlibs.dough.chat.ChatInput;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.bakedlibs.dough.recipes.MinecraftRecipe;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.MinecraftRecipeService;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedItemFlag;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;

import java.util.*;
import java.util.logging.Level;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.MenuClickHandler;
import me.qscbm.slimefun4.message.QsTextComponentImpl;
import me.qscbm.slimefun4.utils.PinyinUtils;
import me.qscbm.slimefun4.utils.QsConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

/**
 * The {@link SurvivalSlimefunGuide} is the standard version of our {@link SlimefunGuide}.
 * It uses an {@link Inventory} to display {@link SlimefunGuide} contents.
 *
 * @author TheBusyBiscuit
 * @see SlimefunGuide
 * @see SlimefunGuideImplementation
 * @see CheatSheetSlimefunGuide
 */
public class SurvivalSlimefunGuide implements SlimefunGuideImplementation {
    private static final int MAX_ITEM_GROUPS = 36;

    private final int[] recipeSlots = {3, 4, 5, 12, 13, 14, 21, 22, 23};
    private final ItemStack item;

    public SurvivalSlimefunGuide() {
        item = new SlimefunGuideItem(this, "&aSlimefun 指南 &7(箱子界面)");
    }

    // fallback
    @Deprecated
    public SurvivalSlimefunGuide(boolean v1, boolean v2) {
        item = new SlimefunGuideItem(this, "&aSlimefun 指南 &7(箱子界面)");
    }

    @Override
    public SlimefunGuideMode getMode() {
        return SlimefunGuideMode.SURVIVAL_MODE;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    protected final boolean isSurvivalMode() {
        return getMode() != SlimefunGuideMode.CHEAT_MODE;
    }

    /**
     * Returns a {@link List} of visible {@link ItemGroup} instances that the {@link SlimefunGuide} would display.
     *
     * @param p       The {@link Player} who opened his {@link SlimefunGuide}
     * @param profile The {@link PlayerProfile} of the {@link Player}
     * @return a {@link List} of visible {@link ItemGroup} instances
     */
    protected List<ItemGroup> getVisibleItemGroups(Player p, PlayerProfile profile) {
        List<ItemGroup> groups = new LinkedList<>();

        for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
            try {
                if (group instanceof FlexItemGroup flexItemGroup) {
                    if (flexItemGroup.isVisible(p, profile, getMode())) {
                        groups.add(group);
                    }
                } else if (!group.isHidden(p)) {
                    groups.add(group);
                }
            } catch (RuntimeException | LinkageError x) {
                SlimefunAddon addon = group.getAddon();

                if (addon != null) {
                    addon.getLogger().log(Level.SEVERE, x, () -> "Could not display item group: " + group);
                } else {
                    Slimefun.logger().log(Level.SEVERE, x, () -> "Could not display item group: " + group);
                }
            }
        }

        return groups;
    }

    @Override
    public void openMainMenu(PlayerProfile profile, int page) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        if (isSurvivalMode()) {
            GuideHistory history = profile.getGuideHistory();
            history.clear();
            history.setMainMenuPage(page);
        }

        ChestMenu menu = create(p);
        List<ItemGroup> itemGroups = getVisibleItemGroups(p, profile);

        int index = 9;
        createHeader(p, profile, menu);

        int target = (MAX_ITEM_GROUPS * (page - 1)) - 1;

        while (target < (itemGroups.size() - 1) && index < MAX_ITEM_GROUPS + 9) {
            target++;

            ItemGroup group = itemGroups.get(target);
            showItemGroup(menu, p, profile, group, index);

            index++;
        }

        int pages = target == itemGroups.size() - 1 ? page : (itemGroups.size() - 1) / MAX_ITEM_GROUPS + 1;

        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;

            if (next > 0) {
                openMainMenu(profile, next);
            }

            return false;
        });

        menu.addItem(52, ChestMenuUtils.getNextButton(p, page, pages));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            int next = page + 1;

            if (next <= pages) {
                openMainMenu(profile, next);
            }

            return false;
        });

        menu.open(p);
    }

    private static final List<Component> LOCKED_ITEMGROUP_LORE = List.of(
            Component.empty(),
            new QsTextComponentImpl("为了解锁这一类别").color(NamedTextColor.WHITE),
            new QsTextComponentImpl("你需要先解锁以下").color(NamedTextColor.WHITE),
            new QsTextComponentImpl("类别里的所有物品").color(NamedTextColor.WHITE),
            Component.empty()
    );

    private static final QsTextComponentImpl LOCKED_NAME_PREFIX =
            new QsTextComponentImpl("已锁定 ").color(NamedTextColor.DARK_RED)
                    .append(new QsTextComponentImpl("- ").color(NamedTextColor.GRAY));

    private void showItemGroup(ChestMenu menu, Player p, PlayerProfile profile, ItemGroup group, int index) {
        if (!(group instanceof LockedItemGroup)
                || !isSurvivalMode()
                || ((LockedItemGroup) group).hasUnlocked(p, profile)) {
            menu.addItem(index, group.getItem(p));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                openItemGroup(profile, group, 1);
                return false;
            });
        } else {
            List<Component> lore = new ArrayList<>(LOCKED_ITEMGROUP_LORE);
            for (ItemGroup parent : ((LockedItemGroup) group).getParents()) {
                lore.add(parent.getItem(p).getItemMeta().displayName());
            }

            menu.addItem(
                    index,
                    new CustomItemStack(
                            new ItemStack(Material.BARRIER),
                            LOCKED_NAME_PREFIX.clone().append(group.getItem(p).getItemMeta().displayName(), NamedTextColor.WHITE),
                            lore));
            menu.addMenuClickHandler(index, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public void openItemGroup(PlayerProfile profile, ItemGroup itemGroup, int page) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        if (itemGroup instanceof FlexItemGroup flexItemGroup) {
            flexItemGroup.open(p, profile, getMode());
            return;
        }

        if (isSurvivalMode()) {
            profile.getGuideHistory().add(itemGroup, page);
        }

        ChestMenu menu = create(p);
        createHeader(p, profile, menu);

        addBackButton(menu, 1, p, profile);

        int pages = (itemGroup.getItems().size() - 1) / MAX_ITEM_GROUPS + 1;

        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;

            if (next > 0) {
                openItemGroup(profile, itemGroup, next);
            }

            return false;
        });

        menu.addItem(52, ChestMenuUtils.getNextButton(p, page, pages));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            int next = page + 1;

            if (next <= pages) {
                openItemGroup(profile, itemGroup, next);
            }

            return false;
        });

        int index = 9;
        int itemGroupIndex = MAX_ITEM_GROUPS * (page - 1);

        for (int i = 0; i < MAX_ITEM_GROUPS; i++) {
            int target = itemGroupIndex + i;

            if (target >= itemGroup.getItems().size()) {
                break;
            }

            SlimefunItem sfitem = itemGroup.getItems().get(target);

            if (!sfitem.isDisabledIn(p.getWorld())) {
                displaySlimefunItem(menu, itemGroup, p, profile, sfitem, page, index);
                index++;
            }
        }

        menu.open(p);
    }

    private void displaySlimefunItem(
            ChestMenu menu,
            ItemGroup itemGroup,
            Player p,
            PlayerProfile profile,
            SlimefunItem sfitem,
            int page,
            int index) {
        Research research = sfitem.getResearch();

        if (isSurvivalMode() && !hasPermission(p, sfitem)) {
            List<String> message = Slimefun.getPermissionsService().getLore(sfitem);
            menu.addItem(
                    index,
                    new CustomItemStack(
                            ChestMenuUtils.getNoPermissionItem(),
                            sfitem.getItemName(),
                            message.toArray(QsConstants.EMPTY_STRINGS)));
            menu.addMenuClickHandler(index, ChestMenuUtils.getEmptyClickHandler());
        } else if (isSurvivalMode() && research != null && !profile.hasUnlocked(research)) {
            String lore;

            if (VaultIntegration.isEnabled()) {
                lore = String.format("%.2f", research.getCurrencyCost()) + " 游戏币";
            } else {
                lore = research.getLevelCost() + " 级经验";
            }

            menu.addItem(
                    index,
                    new CustomItemStack(new CustomItemStack(
                            ChestMenuUtils.getNoPermissionItem(),
                            "&f" + ItemUtils.getItemName(sfitem.getItem()),
                            "&7" + sfitem.getId(),
                            "§4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                            "",
                            "&a> 单击解锁",
                            "",
                            "&7需要 &b",
                            lore)));
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                research.unlockFromGuide(this, p, profile, sfitem, itemGroup, page);
                return false;
            });
        } else {
            menu.addItem(index, sfitem.getItem());
            menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
                try {
                    if (isSurvivalMode()) {
                        displayItem(profile, sfitem, true);
                    } else if (pl.hasPermission("slimefun.cheat.items")) {
                        if (sfitem instanceof MultiBlockMachine) {
                            Slimefun.getLocalization().sendMessage(pl, "guide.cheat.no-multiblocks");
                        } else {
                            ItemStack clonedItem = sfitem.getItem().clone();

                            if (action.isShiftClicked()) {
                                clonedItem.setAmount(clonedItem.getMaxStackSize());
                            }

                            pl.getInventory().addItem(clonedItem);
                        }
                    } else {
                        /*
                         * Fixes #3548 - If for whatever reason,
                         * an unpermitted players gets access to this guide,
                         * this will be our last line of defense to prevent any exploit.
                         */
                        Slimefun.getLocalization().sendMessage(pl, "messages.no-permission", true);
                    }
                } catch (Exception | LinkageError x) {
                    printErrorMessage(pl, sfitem, x);
                }

                return false;
            });
        }
    }


    @Override
    public void openSearch(PlayerProfile profile, String input, boolean addToHistory, boolean usePinyin) {
        Player p = profile.getPlayer();
        if (p == null) {
            return;
        }

        String searchTerm = input.toLowerCase(Locale.ROOT);

        String title = Slimefun.getLocalization()
                .getMessage(p, "guide.search.inventory")
                + " &f" + input;
        if (usePinyin) {
            title = title + "   --&b已启用拼音搜索";
        }
        ChestMenu menu = new ChestMenu(title);

        if (addToHistory) {
            profile.getGuideHistory().add(searchTerm, usePinyin);
        }

        menu.setEmptySlotsClickable(false);
        createHeader(p, profile, menu);
        addBackButton(menu, 1, p, profile);

        int index = 9;
        // Find items and add them
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (index == 44) {
                break;
            }

            if (!slimefunItem.isHidden()
                    && isSearchFilterApplicable(slimefunItem, searchTerm, usePinyin)) {
                ItemStack itemstack = new CustomItemStack(slimefunItem.getItem(), meta -> {
                    ItemGroup itemGroup = slimefunItem.getItemGroup();
                    meta.setLore(Arrays.asList(
                            "", ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE + itemGroup.getDisplayName(p)));
                    meta.addItemFlags(
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_ENCHANTS,
                            VersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
                });

                menu.addItem(index, itemstack);
                menu.addMenuClickHandler(index, (pl, slot, itm, action) -> {
                    try {
                        if (!isSurvivalMode()) {
                            pl.getInventory().addItem(slimefunItem.getItem().clone());
                        } else {
                            displayItem(profile, slimefunItem, true);
                        }
                    } catch (Exception | LinkageError x) {
                        printErrorMessage(pl, slimefunItem, x);
                    }

                    return false;
                });

                index++;
            }
        }
        menu.open(p);
    }

    private static boolean isSearchFilterApplicable(SlimefunItem slimefunItem, String searchTerm, boolean usePinyin) {
        String itemName ;
        if (usePinyin) {
            itemName = PinyinUtils.getPinyin(slimefunItem);

        } else {
            itemName = slimefunItem.getItemNormalName();
        }
        return itemName.contains(searchTerm);
    }

    @Override
    public void displayItem(PlayerProfile profile, ItemStack item, int index, boolean addToHistory) {
        Player p = profile.getPlayer();

        if (p == null || item.getType() == Material.AIR) {
            return;
        }

        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem != null) {
            displayItem(profile, sfItem, addToHistory);
            return;
        }

        if (!Slimefun.getConfigManager().isShowVanillaRecipes()) {
            return;
        }

        Recipe[] recipes = Slimefun.getMinecraftRecipeService().getRecipesFor(item);

        if (recipes.length == 0) {
            return;
        }

        showMinecraftRecipe(recipes, index, item, profile, p, addToHistory);
    }

    private void showMinecraftRecipe(
            Recipe[] recipes, int index, ItemStack item, PlayerProfile profile, Player p, boolean addToHistory) {
        Recipe recipe = recipes[index];

        ItemStack[] recipeItems = new ItemStack[9];
        RecipeType recipeType = RecipeType.NULL;
        ItemStack result = null;

        Optional<MinecraftRecipe<? super Recipe>> optional = MinecraftRecipe.of(recipe);
        AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();

        if (optional.isPresent()) {
            showRecipeChoices(recipe, recipeItems, task);

            recipeType = new RecipeType(optional.get());
            result = recipe.getResult();
        } else {
            recipeItems = new ItemStack[]{
                    null,
                    null,
                    null,
                    null,
                    new CustomItemStack(Material.BARRIER, "§4We are somehow unable to show you this Recipe :/"),
                    null,
                    null,
                    null,
                    null
            };
        }

        ChestMenu menu = create(p);

        if (addToHistory) {
            profile.getGuideHistory().add(item, index);
        }

        displayItem(menu, profile, p, item, result, recipeType, recipeItems, task);

        if (recipes.length > 1) {
            for (int i = 27; i < 36; i++) {
                menu.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }

            menu.addItem(
                    28, ChestMenuUtils.getPreviousButton(p, index + 1, recipes.length), (pl, slot, action, stack) -> {
                        if (index > 0) {
                            showMinecraftRecipe(recipes, index - 1, item, profile, p, true);
                        }
                        return false;
                    });

            menu.addItem(34, ChestMenuUtils.getNextButton(p, index + 1, recipes.length), (pl, slot, action, stack) -> {
                if (index < recipes.length - 1) {
                    showMinecraftRecipe(recipes, index + 1, item, profile, p, true);
                }
                return false;
            });
        }

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    private <T extends Recipe> void showRecipeChoices(T recipe, ItemStack[] recipeItems, AsyncRecipeChoiceTask task) {
        RecipeChoice[] choices = MinecraftRecipeService.getRecipeShape(recipe);

        if (choices.length == 1 && choices[0] instanceof MaterialChoice materialChoice) {
            recipeItems[4] = new ItemStack(materialChoice.getChoices().get(0));

            if (materialChoice.getChoices().size() > 1) {
                task.add(recipeSlots[4], materialChoice);
            }
        } else {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i] instanceof MaterialChoice materialChoice) {
                    recipeItems[i] = new ItemStack(materialChoice.getChoices().get(0));

                    if (materialChoice.getChoices().size() > 1) {
                        task.add(recipeSlots[i], materialChoice);
                    }
                }
            }
        }
    }

    private static final QsTextComponentImpl WIKI_ITEM_NAME =
            new QsTextComponentImpl("在非官方中文 Slimefun 维基上查看此物品")
                    .color(NamedTextColor.WHITE);

    private static final List<Component> WIKI_ITEM_LORE =
            List.of(
                    Component.empty(),
                    new QsTextComponentImpl("\u21E8 ").color(NamedTextColor.GRAY).append(
                            new QsTextComponentImpl("单击打开").color(NamedTextColor.GREEN)
                    )
            );

    @Override
    public void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory) {
        Player p = profile.getPlayer();

        if (p == null) {
            return;
        }

        ChestMenu menu = create(p);
        Optional<String> wiki = item.getWikipage();

        if (wiki.isPresent()) {
            menu.addItem(
                    8,
                    new CustomItemStack(
                            new ItemStack(Material.KNOWLEDGE_BOOK),
                            WIKI_ITEM_NAME,
                            WIKI_ITEM_LORE));
            menu.addMenuClickHandler(8, (pl, slot, itemstack, action) -> {
                pl.closeInventory();
                ChatUtils.sendURL(pl, wiki.get());
                return false;
            });
        }

        AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();

        if (addToHistory) {
            profile.getGuideHistory().add(item);
        }

        ItemStack result = item.getRecipeOutput();
        RecipeType recipeType = item.getRecipeType();
        ItemStack[] recipe = item.getRecipe();

        displayItem(menu, profile, p, item, result, recipeType, recipe, task);

        if (item instanceof RecipeDisplayItem recipeDisplayItem) {
            displayRecipes(p, profile, menu, recipeDisplayItem, 0);
        }

        menu.open(p);

        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    private void displayItem(
            ChestMenu menu,
            PlayerProfile profile,
            Player p,
            Object item,
            ItemStack output,
            RecipeType recipeType,
            ItemStack[] recipe,
            AsyncRecipeChoiceTask task) {
        addBackButton(menu, 0, p, profile);

        MenuClickHandler clickHandler = (pl, slot, itemstack, action) -> {
            try {
                if (itemstack != null && itemstack.getType() != Material.BARRIER) {
                    displayItem(profile, itemstack, 0, true);
                }
            } catch (RuntimeException | LinkageError x) {
                printErrorMessage(pl, x);
            }
            return false;
        };

        boolean isSlimefunRecipe = item instanceof SlimefunItem;

        for (int i = 0; i < 9; i++) {
            ItemStack recipeItem = getDisplayItem(p, isSlimefunRecipe, recipe[i]);
            menu.addItem(recipeSlots[i], recipeItem, clickHandler);

            if (recipeItem != null && item instanceof MultiBlockMachine) {
                for (Tag<Material> tag : MultiBlock.getSupportedTags()) {
                    if (tag.isTagged(recipeItem.getType())) {
                        task.add(recipeSlots[i], tag);
                        break;
                    }
                }
            }
        }

        menu.addItem(10, recipeType.getItem(p), ChestMenuUtils.getEmptyClickHandler());
        menu.addItem(16, output, ChestMenuUtils.getEmptyClickHandler());
    }

    public void createHeader(Player p, PlayerProfile profile, ChestMenu menu) {
        for (int i = 0; i < 9; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        // Settings Panel
        menu.addItem(1, ChestMenuUtils.getMenuButton(p));
        menu.addMenuClickHandler(1, (pl, slot, item, action) -> {
            SlimefunGuideSettings.openSettings(pl, pl.getInventory().getItemInMainHand());
            return false;
        });

        // Search feature!
        menu.addItem(7, ChestMenuUtils.getSearchButton(p));
        menu.addMenuClickHandler(7, (pl, slot, item, action) -> {
            pl.closeInventory();

            Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
            ChatInput.waitForPlayer(
                    Slimefun.instance(),
                    pl,
                    msg -> SlimefunGuide.openSearch(profile, msg, getMode(), isSurvivalMode(), action.isRightClicked()));

            return false;
        });

        for (int i = 45; i < 54; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }
    }

    private void addBackButton(ChestMenu menu, int slot, Player p, PlayerProfile profile) {
        GuideHistory history = profile.getGuideHistory();

        if (isSurvivalMode() && history.size() > 1) {
            menu.addItem(
                    slot,
                    new CustomItemStack(ChestMenuUtils.getBackButton(p, "", "&f左键: &7返回上一页", "&fShift + 左键: &7返回主菜单")));

            menu.addMenuClickHandler(slot, (pl, s, is, action) -> {
                if (action.isShiftClicked()) {
                    openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                } else {
                    history.goBack(this);
                }
                return false;
            });

        } else {
            menu.addItem(
                    slot,
                    new CustomItemStack(ChestMenuUtils.getBackButton(
                            p, "", ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "guide.back.guide"))));
            menu.addMenuClickHandler(slot, (pl, s, is, action) -> {
                openMainMenu(profile, profile.getGuideHistory().getMainMenuPage());
                return false;
            });
        }
    }

    private static ItemStack getDisplayItem(Player p, boolean isSlimefunRecipe, ItemStack item) {
        if (isSlimefunRecipe) {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(item);

            if (slimefunItem == null) {
                return item;
            }

            String lore = hasPermission(p, slimefunItem)
                    ? "&f需要在 " + slimefunItem.getItemGroup().getDisplayName(p) + " 中解锁"
                    : "&f无权限";
            return slimefunItem.canUse(p, false)
                    ? item
                    : new CustomItemStack(
                    Material.BARRIER,
                    ItemUtils.getItemName(item),
                    "§4&l" + Slimefun.getLocalization().getMessage(p, "guide.locked"),
                    "",
                    lore);
        } else {
            return item;
        }
    }

    private void displayRecipes(Player p, PlayerProfile profile, ChestMenu menu, RecipeDisplayItem sfItem, int page) {
        List<ItemStack> recipes = sfItem.getDisplayRecipes();

        if (!recipes.isEmpty()) {
            menu.addItem(53, null);

            if (page == 0) {
                for (int i = 27; i < 36; i++) {
                    menu.replaceExistingItem(
                            i, new CustomItemStack(ChestMenuUtils.getBackground(), sfItem.getRecipeSectionLabel(p)));
                    menu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
                }
            }

            int pages = (recipes.size() - 1) / 18 + 1;

            menu.replaceExistingItem(28, ChestMenuUtils.getPreviousButton(p, page + 1, pages));
            menu.addMenuClickHandler(28, (pl, slot, itemstack, action) -> {
                if (page > 0) {
                    displayRecipes(pl, profile, menu, sfItem, page - 1);
                    SoundEffect.GUIDE_BUTTON_CLICK_SOUND.playFor(pl);
                }

                return false;
            });

            menu.replaceExistingItem(34, ChestMenuUtils.getNextButton(p, page + 1, pages));
            menu.addMenuClickHandler(34, (pl, slot, itemstack, action) -> {
                if (recipes.size() > (18 * (page + 1))) {
                    displayRecipes(pl, profile, menu, sfItem, page + 1);
                    SoundEffect.GUIDE_BUTTON_CLICK_SOUND.playFor(pl);
                }

                return false;
            });

            int inputs = 36;
            int outputs = 45;

            for (int i = 0; i < 18; i++) {
                int slot;

                if (i % 2 == 0) {
                    slot = inputs;
                    inputs++;
                } else {
                    slot = outputs;
                    outputs++;
                }

                addDisplayRecipe(menu, profile, recipes, slot, i, page);
            }
        }
    }

    private void addDisplayRecipe(
            ChestMenu menu, PlayerProfile profile, List<ItemStack> recipes, int slot, int i, int page) {
        if ((i + (page * 18)) < recipes.size()) {
            ItemStack displayItem = recipes.get(i + (page * 18));

            /*
             * We want to clone this item to avoid corrupting the original
             * but we wanna make sure no stupid addon creator sneaked some nulls in here
             */
            if (displayItem != null) {
                displayItem = displayItem.clone();
            }

            menu.replaceExistingItem(slot, displayItem);

            if (page == 0) {
                menu.addMenuClickHandler(slot, (pl, s, itemstack, action) -> {
                    displayItem(profile, itemstack, 0, true);
                    return false;
                });
            }
        } else {
            menu.replaceExistingItem(slot, null);
            menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    private static boolean hasPermission(Player p, SlimefunItem item) {
        return Slimefun.getPermissionsService().hasPermission(p, item);
    }

    private static ChestMenu create(Player p) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.main"));

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(SoundEffect.GUIDE_BUTTON_CLICK_SOUND::playFor);
        return menu;
    }

    private static void printErrorMessage(Player p, Throwable x) {
        p.sendMessage(ChatColor.DARK_RED + "服务器发生了一个内部错误. 请联系管理员处理.");
        Slimefun.logger().log(Level.SEVERE, "在打开指南书里的 Slimefun 物品时发生了意外!", x);
    }

    private static void printErrorMessage(Player p, SlimefunItem item, Throwable x) {
        p.sendMessage(ChatColor.DARK_RED
                + "An internal server error has occurred. Please inform an admin, check the console for"
                + " further info.");
        item.error(
                "This item has caused an error message to be thrown while viewing it in the Slimefun" + " guide.", x);
    }
}
