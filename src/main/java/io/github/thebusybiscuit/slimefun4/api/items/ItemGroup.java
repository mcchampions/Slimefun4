package io.github.thebusybiscuit.slimefun4.api.items;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.services.localization.SlimefunLocalization;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedItemFlag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents an item group, which structure
 * multiple {@link SlimefunItem} in the {@link SlimefunGuide}.
 *
 * @author TheBusyBiscuit
 *
 * @see LockedItemGroup
 * @see SeasonalItemGroup
 *
 */
public class ItemGroup implements Keyed {
    private SlimefunAddon addon;

    @Getter
    protected final List<SlimefunItem> items = new ArrayList<>();
    protected final NamespacedKey key;
    protected final ItemStack item;
    @Getter
    protected int tier;
    /**
     * -- SETTER --
     *  This method will set if this
     *  will
     *  allow
     * s from other addons to
     *  be added, without a warning, into the group. False by default.
     *  If set to true, Slimefun will not warn about items being added.
     *
     */
    @Setter
    @Getter
    protected boolean crossAddonItemGroup;

    /**
     * Constructs a new {@link ItemGroup} with the given {@link NamespacedKey} as an identifier
     * and the given {@link ItemStack} as its display item.
     * The tier is set to a default value of {@code 3}.
     *
     * @param key
     *            The {@link NamespacedKey} that is used to identify this {@link ItemGroup}
     * @param item
     *            The {@link ItemStack} that is used to display this {@link ItemGroup}
     */
    public ItemGroup(NamespacedKey key, ItemStack item) {
        this(key, item, 3);
    }

    /**
     * Constructs a new {@link ItemGroup} with the given {@link NamespacedKey} as an identifier
     * and the given {@link ItemStack} as its display item.
     *
     * @param key
     *            The {@link NamespacedKey} that is used to identify this {@link ItemGroup}
     * @param item
     *            The {@link ItemStack} that is used to display this {@link ItemGroup}
     * @param tier
     *            The tier of this {@link ItemGroup}, higher tiers will make this {@link ItemGroup} appear further down
     *            in
     *            the {@link SlimefunGuide}
     */
    public ItemGroup(NamespacedKey key, ItemStack item, int tier) {
        this.item = item;
        this.key = key;

        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(VersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        this.item.setItemMeta(meta);
        this.tier = tier;
    }

    @Override
    public final NamespacedKey getKey() {
        return key;
    }

    /**
     * Registers this {@link ItemGroup}.
     * <p>
     * By default, an {@link ItemGroup} is automatically registered when
     * a {@link SlimefunItem} was added to it.
     *
     * @param addon
     *            The {@link SlimefunAddon} that wants to register this {@link ItemGroup}
     */
    public void register(SlimefunAddon addon) {
        if (isRegistered()) {
            throw new UnsupportedOperationException("This ItemGroup has already been registered!");
        }

        this.addon = addon;

        Slimefun.getRegistry().getAllItemGroups().add(this);
        sortCategoriesByTier();
    }

    /**
     * This method returns whether this {@link ItemGroup} has been registered yet.
     * More specifically: Whether {@link #register(SlimefunAddon)} was called or not.
     *
     * @return Whether this {@link ItemGroup} has been registered
     */
    public boolean isRegistered() {
        return this.addon != null && Slimefun.getRegistry().getAllItemGroups().contains(this);
    }

    /**
     * This sets the tier of this {@link ItemGroup}.
     * The tier determines the position of this {@link ItemGroup} in the {@link SlimefunGuide}.
     *
     * @param tier
     *            The tier for this {@link ItemGroup}
     */
    public void setTier(int tier) {
        this.tier = tier;

        // Refresh ItemGroup order if already registered.
        if (isRegistered()) {
            sortCategoriesByTier();
        }
    }

    /**
     * This refreshes the {@link ItemGroup} order.
     */
    private static void sortCategoriesByTier() {
        List<ItemGroup> categories = Slimefun.getRegistry().getAllItemGroups();
        categories.sort(Comparator.comparingInt(ItemGroup::getTier));
    }

    /**
     * This returns the {@link SlimefunAddon} which has registered this {@link ItemGroup}.
     * Or null if it has not been registered yet.
     *
     * @return The {@link SlimefunAddon} or null if unregistered
     */
    public final @Nullable SlimefunAddon getAddon() {
        return addon;
    }

    /**
     * Adds the given {@link SlimefunItem} to this {@link ItemGroup}.
     *
     * @param item
     *            the {@link SlimefunItem} that should be added to this {@link ItemGroup}
     */
    public void add(SlimefunItem item) {
        if (items.contains(item)) {
            // Ignore duplicate entries
            return;
        }

        if (isRegistered()
                && !crossAddonItemGroup
                && !item.getAddon().getName().equals(this.addon.getName())) {
            item.warn("This item does not belong into ItemGroup " + this + " as that group belongs to "
                    + this.addon.getName());
        }

        items.add(item);
    }

    /**
     * Removes the given {@link SlimefunItem} from this {@link ItemGroup}.
     *
     * @param item
     *            the {@link SlimefunItem} that should be removed from this {@link ItemGroup}
     */
    public void remove(SlimefunItem item) {
        items.remove(item);
    }

    /**
     * This method returns a localized display item of this {@link ItemGroup}
     * for the specified {@link Player}.
     *
     * @param p
     *            The Player to create this {@link ItemStack} for
     *
     * @return A localized display item for this {@link ItemGroup}
     */
    public ItemStack getItem(Player p) {
        return new CustomItemStack(item, meta -> {
            String name = SlimefunLocalization.getItemGroupName(p, key);

            if (name == null) {
                name = item.getItemMeta().getDisplayName();
            }

            if (this instanceof SeasonalItemGroup) {
                meta.setDisplayName(ChatColor.GOLD + name);
            } else {
                meta.setDisplayName(ChatColor.YELLOW + name);
            }

            meta.setLore(Arrays.asList(
                    "",
                    ChatColor.GRAY + "\u21E8 " + ChatColor.GREEN
                            + Slimefun.getLocalization().getMessage(p, "guide.tooltips.open-itemgroup")));
        });
    }

    /**
     * This method makes Walshy happy.
     * It adds a way to get the name of a {@link ItemGroup} without localization nor coloring.
     *
     * @return The unlocalized name of this {@link ItemGroup}
     */
    public String getUnlocalizedName() {
        return TextUtils.toPlainText(item.getItemMeta().getDisplayName());
    }

    /**
     * This returns the localized display name of this {@link ItemGroup} for the given {@link Player}.
     * The method will fall back to {@link #getUnlocalizedName()} if no translation was found.
     *
     * @param p
     *            The {@link Player} who to translate the name for
     *
     * @return The localized name of this {@link ItemGroup}
     */
    public String getDisplayName(Player p) {
        String localized = SlimefunLocalization.getItemGroupName(p, key);

        if (localized != null) {
            return localized;
        } else {
            return getUnlocalizedName();
        }
    }

    /**
     * This method returns whether a given {@link SlimefunItem} exists in this {@link ItemGroup}.
     *
     * @param item
     *            The {@link SlimefunItem} to find
     *
     * @return Whether the given {@link SlimefunItem} was found in this {@link ItemGroup}
     */
    public boolean contains(@Nullable SlimefunItem item) {
        return item != null && items.contains(item);
    }

    /**
     * This method returns whether this {@link ItemGroup} can be accessed
     * by the given {@link Player}. If an {@link ItemGroup} is not accessible,
     * it will not show up in the {@link SlimefunGuide} nor will items from this
     * {@link ItemGroup} show up in the guide search.
     *
     * @param p
     *            The {@link Player} to check for
     *
     * @return Whether this {@link ItemGroup} is accessible by the given {@link Player}
     */
    public boolean isAccessible(Player p) {
        return true;
    }

    /**
     * This method returns whether this {@link ItemGroup} can be viewed
     * by the given {@link Player}. Empty {@link ItemGroup ItemGroups} will not
     * be visible. This includes {@link ItemGroup ItemGroups} where every {@link SlimefunItem}
     * is disabled. If an {@link ItemGroup} is not accessible by the {@link Player},
     * see {@link #isAccessible(Player)}, this method will also return false.
     *
     * @param p
     *            The {@link Player} to check for
     *
     * @return Whether this {@link ItemGroup} is visible to the given {@link Player}
     */
    public boolean isVisible(Player p) {
        if (items.isEmpty() || !isAccessible(p)) {
            return false;
        }

        for (SlimefunItem slimefunItem : getItems()) {
            /*
             * If any item for this item group is visible,
             * the item group itself is also visible.
             * Empty item groups are not displayed.
             */
            if (!slimefunItem.isHidden() && !slimefunItem.isDisabledIn(p.getWorld())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ItemGroup group) {
            return group.key.equals(this.key);
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {" + key + ",tier=" + tier + "}";
    }

    /**
     * This method checks whether this {@link ItemGroup} will be hidden for the specified
     * {@link Player}.
     * <p>
     * Categories are hidden if all of their items have been disabled.
     *
     * @param p
     *            The {@link Player} to check for
     *
     * @return Whether this {@link ItemGroup} will be hidden to the given {@link Player}
     */
    public boolean isHidden(Player p) {
        return !isVisible(p);
    }
}
