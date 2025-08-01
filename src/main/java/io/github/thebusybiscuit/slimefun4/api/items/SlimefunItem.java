package io.github.thebusybiscuit.slimefun4.api.items;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.bakedlibs.dough.collections.OptionalMap;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IdConflictException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.MissingDependencyException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.WrongItemStackException;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.SlimefunRegistry;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotConfigurable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Placeable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.handlers.GlobalItemHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.qscbm.slimefun4.tasks.BaseTickerTask;
import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * A {@link SlimefunItem} is a custom item registered by a {@link SlimefunAddon}.
 * This class defines the behaviours of the item, you can assign an {@link ItemHandler}
 * to give the item functionality.
 * In contrast to that the {@link SlimefunItemStack} defines the look and feel of the item.
 * <p>
 * Remember to call {@link #register(SlimefunAddon)} on your {@link SlimefunItem} for it
 * to appear in the {@link SlimefunGuide}.
 *
 * @author TheBusyBiscuit
 * @author Poslovitch
 * @see SlimefunItemStack
 * @see SlimefunAddon
 */
public class SlimefunItem implements Placeable {
    /**
     * This is our item id.
     */
    private final String id;

    /**
     * This is the original {@link ItemStack} that represents this item.
     * It is immutable and should always be cloned, never used directly.
     */
    private final ItemStack itemStackTemplate;

    /**
     * This is a reference to the {@link SlimefunAddon} that registered this
     * {@link SlimefunItem}, if the item has not been registered yet, it will be null.
     */
    protected SlimefunAddon addon;

    /**
     * This is the state of this {@link SlimefunItem}.
     */
    @Getter
    private ItemState state = ItemState.UNREGISTERED;

    /**
     * This is the {@link ItemGroup} in which this {@link SlimefunItem} can be found.
     */
    @Getter
    private ItemGroup itemGroup;

    /**
     * This is a reference to the associated {@link Research}, can be null.
     */
    private Research research;

    @Getter
    private ItemStack[] recipe;
    /**
     * -- SETTER --
     * Sets the
     * for this
     * .
     */
    @Setter
    @Getter
    private RecipeType recipeType;
    protected ItemStack recipeOutput;

    /**
     * -- GETTER --
     * This returns whether or not this
     * is allowed to be used in
     * an
     * .
     */
    @Getter
    protected boolean enchantable = true;
    /**
     * -- GETTER --
     * This returns whether or not this
     * is allowed to be used in
     * an
     * .
     */
    @Getter
    protected boolean disenchantable = true;
    protected boolean hidden;
    /**
     * -- GETTER --
     * This method returns whether or not this
     * is allowed to
     * be used in a Crafting Table.
     * Items of type
     * may be used in workbenches for example.
     */
    @Getter
    protected boolean useableInWorkbench;

    private Optional<String> wikiURL = Optional.empty();

    private final OptionalMap<Class<? extends ItemHandler>, ItemHandler> itemHandlers = new OptionalMap<>(HashMap::new);
    @Getter
    private final Set<ItemSetting<?>> itemSettings = new HashSet<>();
    @Getter
    private final String baseName;
    /**
     * -- GETTER --
     * This returns whether or not we are scheduling a ticking task for this block.
     */
    @Getter
    private boolean ticking;
    @Getter
    private BlockTicker blockTicker;
    private final String normalItemName;

    /**
     * This creates a new {@link SlimefunItem} from the given arguments.
     *
     * @param itemGroup  The {@link ItemGroup} this {@link SlimefunItem} belongs to
     * @param item       The {@link SlimefunItemStack} that describes the visual features of our {@link SlimefunItem}
     * @param recipeType the {@link RecipeType} that determines how this {@link SlimefunItem} is crafted
     * @param recipe     An Array representing the recipe of this {@link SlimefunItem}
     */
    public SlimefunItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        this(itemGroup, item, recipeType, recipe, null);
    }

    public static final Pattern BASE_NAME_PATTERN = Pattern.compile("[ \\-_.()/\\\\`,=*&$#@!%^\"}{\\[\\]?+—（）【】“：”。，《》<>‘’；？|~·]");

    /**
     * This creates a new {@link SlimefunItem} from the given arguments.
     *
     * @param itemGroup    The {@link ItemGroup} this {@link SlimefunItem} belongs to
     * @param item         The {@link SlimefunItemStack} that describes the visual features of our {@link SlimefunItem}
     * @param recipeType   the {@link RecipeType} that determines how this {@link SlimefunItem} is crafted
     * @param recipe       An Array representing the recipe of this {@link SlimefunItem}
     * @param recipeOutput The result of crafting this item
     */
    public SlimefunItem(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @Nullable ItemStack recipeOutput) {
        this.itemGroup = itemGroup;
        this.itemStackTemplate = item;
        this.id = item.getItemId();
        this.recipeType = recipeType;
        this.recipe = recipe;
        this.recipeOutput = recipeOutput;
        normalItemName = TextUtils.toPlainText(itemStackTemplate.getItemMeta().getDisplayName());
        baseName = BASE_NAME_PATTERN.matcher(normalItemName).replaceAll("");
    }

    // Previously deprecated constructor, now only for internal purposes
    protected SlimefunItem(ItemGroup itemGroup, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        this.itemGroup = itemGroup;
        this.itemStackTemplate = item;
        this.id = id;
        this.recipeType = recipeType;
        this.recipe = recipe;
        normalItemName = TextUtils.toPlainText(itemStackTemplate.getItemMeta().getDisplayName());
        baseName = BASE_NAME_PATTERN.matcher(normalItemName).replaceAll("");
    }

    /**
     * Returns the identifier of this {@link SlimefunItem}.
     *
     * @return the identifier of this {@link SlimefunItem}
     */
    public final String getId() {
        return id;
    }

    /**
     * This returns the {@link ItemStack} of this {@link SlimefunItem}.
     * The {@link ItemStack} describes the look and feel of this {@link SlimefunItem}.
     *
     * @return The {@link ItemStack} that this {@link SlimefunItem} represents
     */
    public ItemStack getItem() {
        return itemStackTemplate;
    }

    /**
     * This method returns the result of crafting this {@link SlimefunItem}
     *
     * @return The recipe output of this {@link SlimefunItem}
     */
    public ItemStack getRecipeOutput() {
        return recipeOutput != null ? recipeOutput.clone() : itemStackTemplate.clone();
    }

    /**
     * This method returns the {@link Research} this {@link SlimefunItem} is linked to.
     * This will be null if the item is not linked to any {@link Research}
     *
     * @return The linked {@link Research} or null
     */
    public final @Nullable Research getResearch() {
        return research;
    }

    /**
     * This returns whether this {@link SlimefunItem} has a {@link Research}
     * assigned to it.
     * It is equivalent to a null check performed on {@link #getResearch()}.
     *
     * @return Whether this {@link SlimefunItem} has a {@link Research}
     */
    public final boolean hasResearch() {
        return research != null;
    }

    /**
     * This method returns an {@link Optional} holding an {@link ItemSetting} with the given
     * key and data type. Or an empty {@link Optional} if this {@link SlimefunItem} has no such {@link ItemSetting}.
     *
     * @param <T> The Type of value stored in this {@link ItemSetting}
     * @param key The key of this {@link ItemSetting}
     * @param c   The {@link Class} of the type of value stored by this setting
     * @return An {@link Optional} describing the result
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<ItemSetting<T>> getItemSetting(String key, Class<T> c) {
        for (ItemSetting<?> setting : itemSettings) {
            if (setting.getKey().equals(key) && setting.isType(c)) {
                return Optional.of((ItemSetting<T>) setting);
            }
        }

        return Optional.empty();
    }

    /**
     * This method returns whether this {@link SlimefunItem} was hidden from the
     * {@link SlimefunGuide}.
     *
     * @return Whether this {@link SlimefunItem} is hidden.
     */
    public final boolean isHidden() {
        return hidden;
    }

    /**
     * This method will forcefully hide this {@link SlimefunItem} from the {@link SlimefunGuide}.
     *
     * @param hidden Whether to hide this {@link SlimefunItem} or not
     */
    public void setHidden(boolean hidden) {
        if (this.hidden != hidden) {
            this.hidden = hidden;

            if (state == ItemState.ENABLED) {
                if (hidden) {
                    itemGroup.remove(this);
                } else {
                    itemGroup.add(this);
                }
            }
        }
    }

    /**
     * This method returns whether this {@link SlimefunItem} is disabled.
     *
     * @return Whether this {@link SlimefunItem} is disabled.
     */
    public boolean isDisabled() {
        if (state == ItemState.UNREGISTERED) {
            return false;
        }

        return state != ItemState.ENABLED;
    }

    /**
     * This method returns whether this {@link SlimefunItem} is disabled
     * for that specific {@link World}.
     * Note that if the item is disabled globally, this method will still return false.
     *
     * @param world The {@link World} to check
     * @return Whether this {@link SlimefunItem} is disabled in that world (or in general).
     */
    public boolean isDisabledIn(World world) {
        if (state == ItemState.UNREGISTERED) {
            return false;
        }

        // Check if the Item is disabled globally or in this specific world
        return isDisabled() || !Slimefun.getWorldSettingsService().isEnabled(world, this);
    }

    /**
     * This method returns the {@link SlimefunAddon} that registered this
     * {@link SlimefunItem}. If this Item is from Slimefun itself, the current
     * instance of {@link Slimefun} will be returned.
     * Use an instanceof check to account for that.
     *
     * @return The {@link SlimefunAddon} that registered this {@link SlimefunItem}
     */
    public final SlimefunAddon getAddon() {
        return addon;
    }

    /**
     * This method registers this {@link SlimefunItem}.
     * Always call this method after your {@link SlimefunItem} has been initialized.
     * Never call it more than once!
     *
     * @param addon The {@link SlimefunAddon} that this {@link SlimefunItem} belongs to.
     */
    public void register(SlimefunAddon addon) {
        this.addon = addon;

        try {
            checkDependencies(addon);
            checkForConflicts();

            preRegister();

            if (recipe == null) {
                recipe = new ItemStack[9];
            } else if (recipe.length < 9) {
                recipe = Arrays.copyOf(recipe, 9);
            }

            Slimefun.getRegistry().getAllSlimefunItems().add(this);
            Slimefun.getRegistry().getSlimefunItemIds().put(id, this);
            Slimefun.getRegistry().getSlimefunItemNames().put(normalItemName, this);

            // Items that are "not-configurable" cannot be configured.
            if (!(this instanceof NotConfigurable)) {
                Slimefun.getItemCfg().setDefaultValue(id + ".enabled", true);
                Slimefun.getItemCfg().setDefaultValue(id + ".can-be-used-in-workbenches", useableInWorkbench);
                Slimefun.getItemCfg().setDefaultValue(id + ".hide-in-guide", hidden);
                Slimefun.getItemCfg().setDefaultValue(id + ".allow-enchanting", enchantable);
                Slimefun.getItemCfg().setDefaultValue(id + ".allow-disenchanting", disenchantable);

                // Load all item settings
                for (ItemSetting<?> setting : itemSettings) {
                    setting.reload();
                }
            }

            if (ticking && !Slimefun.getCfg().getBoolean("URID.enable-tickers")) {
                state = ItemState.DISABLED;
                return;
            }

            if (this instanceof NotConfigurable) {
                /*
                 * Not-configurable items will be enabled.
                 * Any other settings will remain as default.
                 */
                state = ItemState.ENABLED;
            } else if (Slimefun.getItemCfg().getBoolean(id + ".enabled")) {
                // The item has been enabled.
                state = ItemState.ENABLED;
                useableInWorkbench = Slimefun.getItemCfg().getBoolean(id + ".can-be-used-in-workbenches");
                hidden = Slimefun.getItemCfg().getBoolean(id + ".hide-in-guide");
                enchantable = Slimefun.getItemCfg().getBoolean(id + ".allow-enchanting");
                disenchantable = Slimefun.getItemCfg().getBoolean(id + ".allow-disenchanting");
            } else if (this instanceof VanillaItem) {
                // This item is a vanilla "mock" but was disabled.
                state = ItemState.VANILLA_FALLBACK;
            } else {
                // The item has been disabled.
                state = ItemState.DISABLED;
            }

            // Now we can be certain this item should be enabled
            if (state == ItemState.ENABLED) {
                onEnable();
            } else {
                // Clear item handlers if we are disabled so that calling them isn't possible later on
                for (ItemHandler handler : this.itemHandlers.values()) {
                    if (handler instanceof BlockTicker) {
                        Slimefun.getRegistry().getTickerBlocks().remove(id);
                    }
                }
                this.itemHandlers.clear();
                Slimefun.getRegistry().getDisabledSlimefunItemsToSet().add(this);
            }

            postRegister();

            // handle runtime-registrations / auto-loading
            if (Slimefun.getConfigManager().isAutoLoadingEnabled() && state == ItemState.ENABLED) {
                info("Item was registered during runtime.");
                load();
            }
        } catch (RuntimeException x) {
            error("Registering " + this + " has failed!", x);
        }
    }

    public void enable() {
        if (state != ItemState.DISABLED) {
            return;
        }

        if (this instanceof Radioactive) {
            Slimefun.getRegistry().getRadioactiveItems().add(this);
        }

        state = ItemState.ENABLED;

        Slimefun.getRegistry().getEnabledSlimefunItems().add(this);
        Slimefun.getRegistry().getDisabledSlimefunItemsToSet().remove(this);
    }

    /**
     * This method is called when this {@link SlimefunItem} is disabled after reloaded.
     */
    public void disable() {
        if (this instanceof Radioactive) {
            Slimefun.getRegistry().getRadioactiveItems().remove(this);
        }

        // Disable ticking block
        Slimefun.getRegistry().getTickerBlocks().remove(id);

        state = ItemState.DISABLED;

        Slimefun.getRegistry().getEnabledSlimefunItems().remove(this);
        Slimefun.getRegistry().getDisabledSlimefunItemsToSet().add(this);
    }

    /**
     * This method is called when this {@link SlimefunItem} is currently being registered
     * and we are certain that it will be enabled.
     * <p>
     * <strong>This method is for internal purposes, like {@link ItemGroup} registration only</strong>
     */
    private void onEnable() {
        // Register the ItemGroup too if it hasn't been registered yet
        if (!itemGroup.isRegistered()) {
            itemGroup.register(addon);
        }

        // Check for an illegal stack size
        if (itemStackTemplate.getAmount() != 1) {
            warn("无效物品数量: "
                    + itemStackTemplate.getAmount()
                    + "  "
                    + addon.getName());

        }

        // Add it to the list of enabled items
        Slimefun.getRegistry().getEnabledSlimefunItems().add(this);
        Slimefun.getRegistry().getDisabledSlimefunItemsToSet().remove(this);

        // Load our Item Handlers
        loadItemHandlers();

        // Properly mark this Item as radioactive
        if (this instanceof Radioactive) {
            Slimefun.getRegistry().getRadioactiveItems().add(this);
        }
    }

    private void loadItemHandlers() {
        for (ItemHandler handler : itemHandlers.values()) {
            /*
             * If this ItemHandler is "global" (not bound to this SlimefunItem),
             * we add it to the list of global Item handlers
             */
            if (handler instanceof GlobalItemHandler) {
                SlimefunRegistry registry = Slimefun.getRegistry();
                registry.getGlobalItemHandlers(handler.getIdentifier()).add(handler);
            }
        }
    }

    /**
     * This method returns whether the original {@link SlimefunItemStack} of this
     * {@link SlimefunItem} is immutable.
     * <p>
     * If <code>true</code> is returned, then any changes to the original {@link SlimefunItemStack}
     * will be rejected with a {@link WrongItemStackException}.
     * This ensures integrity so developers don't accidentally damage the wrong {@link ItemStack}.
     *
     * @return Whether the original {@link SlimefunItemStack} is immutable.
     */
    protected static boolean isItemStackImmutable() {
        return true;
    }

    /**
     * This method checks if the dependencies have been set up correctly.
     *
     * @param addon The {@link SlimefunAddon} trying to register this {@link SlimefunItem}
     */
    private static void checkDependencies(SlimefunAddon addon) {
        if (!addon.hasDependency("Slimefun")) {
            throw new MissingDependencyException(addon, "Slimefun");
        }
    }

    /**
     * This method checks for id conflicts.
     */
    private void checkForConflicts() {
        SlimefunItem conflictingItem = getById(id);

        if (conflictingItem != null) {
            throw new IdConflictException(this, conflictingItem);
        }
    }

    /**
     * This method will set the {@link Research} of this {@link SlimefunItem}.
     * You don't have to call this method if your {@link SlimefunItem} was linked to your {@link Research}
     * using {@link Research#addItems(SlimefunItem...)}
     *
     * @param research The new {@link Research} for this {@link SlimefunItem}, or null
     */
    public void setResearch(@Nullable Research research) {
        if (this.research != null) {
            this.research.getAffectedItems().remove(this);
        }

        if (research != null) {
            research.getAffectedItems().add(this);
        }

        this.research = research;
    }

    /**
     * Sets the recipe for this {@link SlimefunItem}.
     *
     * @param recipe The recipe for this {@link ItemStack}
     */
    public void setRecipe(ItemStack[] recipe) {
        if (recipe == null || recipe.length != 9) {
            throw new IllegalArgumentException("Recipes must be of length 9");
        }

        this.recipe = recipe;
    }

    /**
     * This sets the {@link ItemGroup} in which this {@link SlimefunItem} will be displayed.
     *
     * @param itemGroup The new {@link ItemGroup}
     */
    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup.remove(this);
        itemGroup.add(this);

        this.itemGroup = itemGroup;
    }

    /**
     * This method will set the result of crafting this {@link SlimefunItem}.
     * If null is passed, then it will use the default item as the recipe result.
     *
     * @param output The {@link ItemStack} that will be the result of crafting this {@link SlimefunItem}
     */
    public void setRecipeOutput(@Nullable ItemStack output) {
        this.recipeOutput = output;
    }

    /**
     * This sets whether or not this {@link SlimefunItem} is allowed to be
     * used in a normal Crafting Table.
     *
     * @param useable Whether this {@link SlimefunItem} should be useable in a workbench
     * @return This instance of {@link SlimefunItem}
     */
    public SlimefunItem setUseableInWorkbench(boolean useable) {
        this.useableInWorkbench = useable;

        return this;
    }

    /**
     * This method checks whether the provided {@link ItemStack} represents
     * this {@link SlimefunItem}.
     *
     * @param item The {@link ItemStack} to compare
     * @return Whether the given {@link ItemStack} represents this {@link SlimefunItem}
     */
    public boolean isItem(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        // If the given item is a SlimefunitemStack, simply compare the id
        if (item instanceof SlimefunItemStack stack) {
            return id.equals(stack.getItemId());
        }

        if (item.hasItemMeta()) {
            Optional<String> itemId = Slimefun.getItemDataService().getItemData(item);

            if (itemId.isPresent()) {
                return id.equals(itemId.get());
            }
        }

        return false;
    }

    /**
     * This method is used for internal purposes only.
     */
    public void load() {
        if (!hidden) {
            itemGroup.add(this);
        }

        recipeType.register(recipe, getRecipeOutput());
    }

    /**
     * This method will add any given {@link ItemHandler} to this {@link SlimefunItem}.
     * Note that this will not work after the {@link SlimefunItem} was registered.
     *
     * @param handlers Any {@link ItemHandler} that should be added to this {@link SlimefunItem}
     */
    public final void addItemHandler(ItemHandler... handlers) {
        for (ItemHandler handler : handlers) {
            itemHandlers.put(handler.getIdentifier(), handler);

            // Tickers are a special case (at the moment at least)
            if (handler instanceof BlockTicker ticker) {
                ticking = true;
                Slimefun.getRegistry().getTickerBlocks().add(id);
                blockTicker = ticker;
            }
        }
    }

    /**
     * This method will add any given {@link ItemSetting} to this {@link SlimefunItem}.
     * Note that this will not work after the {@link SlimefunItem} was registered.
     *
     * @param settings Any {@link ItemSetting} that should be added to this {@link SlimefunItem}
     */
    public final void addItemSetting(ItemSetting<?>... settings) {
        if (state != ItemState.UNREGISTERED) {
            throw new UnsupportedOperationException(
                    "You cannot add an ItemSetting after the SlimefunItem was registered.");
        }

        if (this instanceof NotConfigurable) {
            throw new UnsupportedOperationException(
                    "This Item has been marked as NotConfigurable and cannot accept Item Settings!");
        }

        for (ItemSetting<?> setting : settings) {
            if (setting != null) {
                // Prevent two Item Settings with the same key
                for (ItemSetting<?> existingSetting : itemSettings) {
                    if (existingSetting.getKey().equals(setting.getKey())) {
                        throw new IllegalArgumentException(
                                "This Item has already an ItemSetting with this key: " + setting.getKey());
                    }
                }

                itemSettings.add(setting);
            }
        }
    }

    /**
     * This method is called before {@link #register(SlimefunAddon)}.
     * Override this method to add any additional setup, adding an {@link ItemHandler} for example.
     */
    public void preRegister() {
        // Override this method to execute code before the Item has been registered
        // Useful for calls to addItemHandler(...)
    }

    /**
     * This method is called after {@link #register(SlimefunAddon)}.
     * Override this method to add any additional setup that needs to happen after
     * the original registration of this {@link SlimefunItem}.
     */
    public void postRegister() {
        // Override this method to execute code after the Item has been registered
        // Useful for calls to Slimefun.getItemValue(...)
    }

    /**
     * This method will assign the given wiki page to this Item.
     * Note that you only need to provide the page name itself,
     * the URL to our wiki is prepended automatically.
     * <p>
     * 返回非官方中文Wiki地址
     * 下游应使用 {@link SlimefunItem#addWikiPage(String)} 来添加Wiki页面
     *
     * @param page The associated wiki page
     */
    @Deprecated
    public final void addOfficialWikipage(String page) {
        // 转换链接
        page = page.replace("#", "?id=");
        wikiURL = Optional.of("https://slimefun-wiki.guizhanss.cn/" + page);
    }

    /**
     * 指定该物品的 Wiki 页面
     *
     * @param page 物品的 Wiki 页面
     */
    public final void addWikiPage(String page) {
        if (addon == null) {
            Slimefun.logger().warning("该物品\"" + id + "\"暂未注册, 请在物品注册后再添加Wiki页面");
            return;
        }
        if (addon.getWikiURL() != null) {
            wikiURL = Optional.of(MessageFormat.format(addon.getWikiURL(), page));
        }
    }

    /**
     * This method returns the wiki page that has been assigned to this item.
     * It will return null, if no wiki page was found.
     *
     * @return This item's wiki page
     * @see SlimefunItem#addWikiPage(String)
     */
    public Optional<String> getWikipage() {
        return wikiURL;
    }

    /**
     * This method will return this Item's Name (The name that is displayed when
     * hovering over this {@link ItemStack} in an {@link Inventory}).
     *
     * @return This item's name in {@link ItemStack} form
     */
    public final String getItemName() {
        if (itemStackTemplate instanceof SlimefunItemStack) {
            Optional<String> name = ((SlimefunItemStack) itemStackTemplate)
                    .getItemMetaSnapshot()
                    .getDisplayName();

            if (name.isPresent()) {
                return name.get();
            }
        }

        return ItemUtils.getItemName(itemStackTemplate);
    }

    public final String getItemNormalName() {
        return normalItemName;
    }

    /**
     * This method returns a Set of item handlers associated with this Item.
     *
     * @return The Set of item handlers
     */
    public Collection<ItemHandler> getHandlers() {
        return itemHandlers.values();
    }

    /**
     * This method calls every {@link ItemHandler} of the given {@link Class}
     * and performs the action as specified via the {@link Consumer}.
     *
     * @param c        The {@link Class} of the {@link ItemHandler} to call.
     * @param callable A {@link Consumer} that is called for any found {@link ItemHandler}.
     * @param <T>      The type of {@link ItemHandler} to call.
     * @return Whether or not an {@link ItemHandler} was found.
     */
    public <T extends ItemHandler> boolean callItemHandler(Class<T> c, Consumer<T> callable) {
        Optional<ItemHandler> handler = itemHandlers.get(c);

        if (handler.isPresent()) {
            try {
                callable.accept(c.cast(handler.get()));
            } catch (RuntimeException | LinkageError x) {
                error("Could not pass \"" + c.getSimpleName() + "\" for " + this, x);
            }

            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        if (addon == null) {
            return getClass().getSimpleName() + " - '" + id + "'";
        } else {
            return getClass().getSimpleName()
                    + " - '"
                    + id
                    + "' ("
                    + addon.getName()
                    + " v"
                    + addon.getPluginVersion()
                    + ')';
        }
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return List.of(itemStackTemplate.clone());
    }

    @Override
    public Collection<ItemStack> getDrops(Player p) {
        return getDrops();
    }

    /**
     * This will send an info message to the console and signal that this message came
     * from this {@link SlimefunItem}, the message will be sent using the {@link Logger}
     * of the {@link SlimefunAddon} which registered this {@link SlimefunItem}.
     *
     * @param message The message to send
     */
    public void info(String message) {
        String msg = this + ": " + message;
        addon.getLogger().log(Level.INFO, msg);
    }

    /**
     * This will send a warning to the console and signal that this warning came from
     * this {@link SlimefunItem}, the warning will be sent using the {@link Logger}
     * of the {@link SlimefunAddon} which registered this {@link SlimefunItem}.
     *
     * @param message The message to send
     */
    public void warn(String message) {
        String msg = this + ": " + message;
        addon.getLogger().log(Level.WARNING, msg);
    }

    /**
     * This will throw a {@link Throwable} to the console and signal that
     * this was caused by this {@link SlimefunItem}.
     *
     * @param message   The message to display alongside this Stacktrace
     * @param throwable The {@link Throwable} to throw as a stacktrace.
     */
    public void error(String message, Throwable throwable) {
        addon.getLogger().log(Level.SEVERE, "Item \"{0}\" from {1} v{2} has caused an Error!", new Object[]{
                id, addon.getName(), addon.getPluginVersion()
        });

        addon.getLogger().log(Level.SEVERE, message, throwable);
    }

    /**
     * This method informs the given {@link Player} that this {@link SlimefunItem}
     * will be removed soon.
     *
     * @param player The {@link Player} to inform.
     */
    public static void sendDeprecationWarning(Player player) {
        Slimefun.getLocalization().sendMessage(player, "messages.deprecated-item");
    }

    /**
     * This method checks if the given {@link Player} is able to use this {@link SlimefunItem}.
     * A {@link Player} can use it if the following conditions apply:
     *
     * <ul>
     * <li>The {@link SlimefunItem} is not disabled
     * <li>The {@link SlimefunItem} was not disabled for that {@link Player}'s {@link World}.
     * <li>The {@link Player} has the required {@link Permission} (if present)
     * <li>The {@link Player} has unlocked the required {@link Research} (if present)
     * </ul>
     * <p>
     * If any of these conditions evaluate to <code>false</code>, then an optional message will be
     * sent to the {@link Player}.
     *
     * @param p           The {@link Player} to check
     * @param sendMessage Whether to send that {@link Player} a message response.
     * @return Whether this {@link Player} is able to use this {@link SlimefunItem}.
     */
    public boolean canUse(Player p, boolean sendMessage) {
        if (state == ItemState.VANILLA_FALLBACK) {
            // Vanilla items (which fell back) can always be used.
            return true;
        } else if (isDisabled()) {
            // The Item has been disabled in the config
            if (sendMessage) {
                Slimefun.getLocalization()
                        .sendMessage(
                                p,
                                "messages.disabled-item",
                                true,
                                msg -> msg.replace("%item_name%", normalItemName));
            }

            return false;
        } else if (!Slimefun.getWorldSettingsService().isEnabled(p.getWorld(), this)) {
            // The Item was disabled in the current World
            if (sendMessage) {
                Slimefun.getLocalization()
                        .sendMessage(
                                p,
                                "messages.disabled-in-world",
                                true,
                                msg -> msg.replace("%item_name%", normalItemName));
            }

            return false;
        } else if (!Slimefun.getPermissionsService().hasPermission(p, this)) {
            // The Player does not have the required permission node
            if (sendMessage) {
                Slimefun.getLocalization().sendMessage(p, "messages.no-permission", true);
            }

            return false;
        } else if (hasResearch()) {
            Optional<PlayerProfile> profile = PlayerProfile.find(p);

            if (profile.isEmpty()) {
                /*
                 * We will return false since we cannot know the answer yet.
                 * But we will schedule the Profile for loading and not send
                 * any message.
                 */
                PlayerProfile.request(p);
                return false;
            } else if (!profile.get().hasUnlocked(research)) {
                /*
                 * The Profile is loaded but Player has not unlocked the
                 * required Research to use this SlimefunItem.
                 */
                if (sendMessage && !(this instanceof VanillaItem)) {
                    Slimefun.getLocalization()
                            .sendMessage(p, "messages.not-researched", true, s -> s.replace("%item%", getItemName()));
                }

                return false;
            } else {
                /*
                 * The PlayerProfile is loaded and the Player has unlocked
                 * the required Research.
                 */
                return true;
            }
        } else {
            // All checks have passed, the Player can use this item.
            return true;
        }
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof SlimefunItem item) {
            return item.id.equals(this.id);
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

    /**
     * Retrieve a {@link SlimefunItem} by its id.
     *
     * @param id The id of the {@link SlimefunItem}
     * @return The {@link SlimefunItem} associated with that id. Null if non-existent
     */
    public static @Nullable SlimefunItem getById(String id) {
        return Slimefun.getRegistry().getSlimefunItemIds().get(id);
    }

    public static @Nullable SlimefunItem getByName(String name) {
        return Slimefun.getRegistry().getSlimefunItemNames().get(name);
    }

    /**
     * Retrieve a {@link Optional} {@link SlimefunItem} by its id.
     *
     * @param id The id of the {@link SlimefunItem}
     * @return The {@link Optional} {@link SlimefunItem} associated with that id. Empty if non-existent
     */
    public static Optional<SlimefunItem> getOptionalById(String id) {
        return Optional.ofNullable(getById(id));
    }

    /**
     * Retrieve a {@link SlimefunItem} from an {@link ItemStack}.
     *
     * @param item The {@link ItemStack} to check
     * @return The {@link SlimefunItem} associated with this {@link ItemStack} if present, otherwise null
     */
    public static @Nullable SlimefunItem getByItem(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        if (item instanceof SlimefunItemStack stack) {
            return getById(stack.getItemId());
        }

        Optional<String> itemID = Slimefun.getItemDataService().getItemData(item);

        return itemID.map(SlimefunItem::getById).orElse(null);
    }

    public static @Nullable SlimefunItem getByItem(@Nullable SlimefunItemStack item) {
        Optional<String> itemID = Slimefun.getItemDataService().getItemData(item);

        return itemID.map(SlimefunItem::getById).orElse(null);
    }

    /**
     * Retrieve a {@link Optional} {@link SlimefunItem} from an {@link ItemStack}.
     *
     * @param item The {@link ItemStack} to check
     * @return The {@link Optional} {@link SlimefunItem} associated with this {@link ItemStack} if present, otherwise empty
     */
    public static Optional<SlimefunItem> getOptionalByItem(@Nullable ItemStack item) {
        return Optional.ofNullable(getByItem(item));
    }

    /**
     * Should load the {@link SlimefunBlockData} by default.
     * If return false, only the item with {@link BlockTicker} will be loaded with {@link ChunkLoadEvent}.
     *
     * @return true if the data should be loaded by default, false otherwise
     */
    public boolean loadDataByDefault() {
        return ticking;
    }

    public BaseTickerTask getTickerTask() {
        return Slimefun.getTickerTask();
    }
}
