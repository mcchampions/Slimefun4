package io.github.thebusybiscuit.slimefun4.implementation.items;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.staves.StormStaff;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * This class represents an item with a limited number of uses.
 * When the item runs out of "uses", it breaks.
 *
 * @author Linox
 * @author Walshy
 * @author TheBusyBiscuit
 * @author martinbrom
 *
 * @see StormStaff
 */
public abstract class LimitedUseItem extends SimpleSlimefunItem<ItemUseHandler> implements DistinctiveItem {
    private final NamespacedKey defaultUsageKey;
    private int maxUseCount = -1;

    protected LimitedUseItem(ItemGroup group, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(group, item, recipeType, recipe);

        this.defaultUsageKey = new NamespacedKey(Slimefun.instance(), "uses_left");
        addItemHandler(getItemHandler());
    }

    /**
     * Returns the number of times this item can be used.
     *
     * @return The number of times this item can be used.
     */
    public final int getMaxUseCount() {
        return maxUseCount;
    }

    /**
     * Sets the maximum number of times this item can be used.
     * The number must be greater than zero.
     *
     * @param count
     *            The maximum number of times this item can be used.
     *
     * @return The {@link LimitedUseItem} for chaining of setters
     */
    public final LimitedUseItem setMaxUseCount(int count) {
        maxUseCount = count;
        return this;
    }

    /**
     * Returns the {@link NamespacedKey} under which will the amount of uses left stored.
     *
     * @return The {@link NamespacedKey} to store/load the amount of uses
     */
    protected NamespacedKey getStorageKey() {
        return defaultUsageKey;
    }

    @Override
    public void register(SlimefunAddon addon) {
        if (getMaxUseCount() < 1) {
            warn("The use count has not been configured correctly. It needs to be at least 1. The Item was"
                    + " disabled.");
        } else {
            super.register(addon);
        }
    }

    protected void damageItem(Player p, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);

            // Separate one item from the stack and damage it
            ItemStack separateItem = item.clone();
            separateItem.setAmount(1);
            damageItem(p, separateItem);

            // Try to give the Player the new item
            if (!p.getInventory().addItem(separateItem).isEmpty()) {
                // or throw it on the ground
                p.getWorld().dropItemNaturally(p.getLocation(), separateItem);
            }
        } else {
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = getStorageKey();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            int usesLeft = pdc.getOrDefault(key, PersistentDataType.INTEGER, getMaxUseCount());

            if (usesLeft == 1) {
                SoundEffect.LIMITED_USE_ITEM_BREAK_SOUND.playFor(p);
                item.setAmount(0);
                item.setType(Material.AIR);
            } else {
                usesLeft--;
                pdc.set(key, PersistentDataType.INTEGER, usesLeft);

                updateItemLore(item, meta, usesLeft);
            }
        }
    }

    private void updateItemLore(ItemStack item, ItemMeta meta, int usesLeft) {
        List<String> lore = meta.getLore();

        String newLine = ChatColors.color(LoreBuilder.usesLeft(usesLeft));
        if (lore != null && !lore.isEmpty()) {
            // find the correct line
            for (int i = 0; i < lore.size(); i++) {
                if (PatternUtils.USES_LEFT_LORE.matcher(lore.get(i)).matches()) {
                    lore.set(i, newLine);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    return;
                }
            }
        } else {
            meta.setLore(Collections.singletonList(newLine));
            item.setItemMeta(meta);
        }
    }

    @Override
    public boolean canStack(ItemMeta itemMetaOne, ItemMeta itemMetaTwo) {
        if (Slimefun.getItemDataService().getItemData(itemMetaOne) != Slimefun.getItemDataService().getItemData(itemMetaTwo)) {
            return false;
        }
        NamespacedKey key = getStorageKey();
        PersistentDataContainer pdc1 = itemMetaOne.getPersistentDataContainer();
        int usesLeft1 = pdc1.getOrDefault(key, PersistentDataType.INTEGER, getMaxUseCount());
        PersistentDataContainer pdc2 = itemMetaTwo.getPersistentDataContainer();
        int usesLeft2 = pdc2.getOrDefault(key, PersistentDataType.INTEGER, getMaxUseCount());
        return usesLeft1 == usesLeft2;
    }
}
