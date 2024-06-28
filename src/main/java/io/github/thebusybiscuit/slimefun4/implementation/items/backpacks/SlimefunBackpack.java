package io.github.thebusybiscuit.slimefun4.implementation.items.backpacks;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.BackpackListener;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This class represents a {@link SlimefunItem} that is considered a Backpack.
 * Right-Clicking will open the {@link Inventory} of the currently held Backpack.
 *
 * @author TheBusyBiscuit
 *
 * @see BackpackListener
 * @see PlayerBackpack
 *
 */
@Getter
public class SlimefunBackpack extends SimpleSlimefunItem<ItemUseHandler> implements DistinctiveItem {
    /**
     * -- GETTER --
     *  This returns the size of this
     * .
     *
     */
    private final int size;

    @ParametersAreNonnullByDefault
    public SlimefunBackpack(
            int size, ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.size = size;
    }

    /**
     * This method returns whether a given {@link ItemStack} is allowed to be stored
     * in this {@link SlimefunBackpack}.
     *
     * @param item
     *            The {@link ItemStack} to check for
     *
     * @param itemAsSlimefunItem
     *            The same {@link ItemStack} as a {@link SlimefunItem}, might be null
     *
     * @return Whether the given {@link ItemStack} is allowed to be put into this {@link SlimefunBackpack}
     */
    public boolean isItemAllowed(@Nonnull ItemStack item, @Nullable SlimefunItem itemAsSlimefunItem) {
        // Shulker Boxes are not allowed!
        if (SlimefunTag.SHULKER_BOXES.isTagged(item.getType())) {
            return false;
        }

        return !(itemAsSlimefunItem instanceof SlimefunBackpack);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            if (!PlayerBackpack.isOwnerOnline(e.getItem().getItemMeta())) {
                Slimefun.getLocalization().sendMessage(e.getPlayer(), "backpack.not-backpack-owner");
                return;
            }

            BackpackListener listener = Slimefun.getBackpackListener();

            listener.openBackpack(e.getPlayer(), e.getItem(), this);
        };
    }

    @Override
    public boolean canStack(@Nonnull ItemMeta itemMetaOne, @Nonnull ItemMeta itemMetaTwo) {
        var uuid1 = PlayerBackpack.getBackpackUUID(itemMetaOne);
        var uuid2 = PlayerBackpack.getBackpackUUID(itemMetaTwo);
        if (uuid1.isPresent() || uuid2.isPresent()) {
            return uuid1.equals(uuid2);
        }

        boolean hasLoreItem = itemMetaTwo.hasLore();
        boolean hasLoreSfItem = itemMetaOne.hasLore();

        if (hasLoreItem && hasLoreSfItem && SlimefunUtils.equalsLore(itemMetaTwo.getLore(), itemMetaOne.getLore())) {
            return true;
        }
        return !hasLoreItem && !hasLoreSfItem;
    }
}
