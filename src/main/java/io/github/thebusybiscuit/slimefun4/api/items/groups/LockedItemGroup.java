package io.github.thebusybiscuit.slimefun4.api.items.groups;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a {@link ItemGroup} that cannot be opened until the parent group(s)
 * are fully unlocked.
 * <p>
 * See {@link ItemGroup} for the complete documentation.
 *
 * @author TheBusyBiscuit
 *
 * @see ItemGroup
 * @see SeasonalItemGroup
 *
 */
public class LockedItemGroup extends ItemGroup {
    private final NamespacedKey[] keys;
    @Getter
    private final Set<ItemGroup> parents = new HashSet<>();

    /**
     * The basic constructor for a LockedItemGroup.
     * Like {@link ItemGroup}, the default tier is automatically set to 3.
     *
     * @param key
     *            A unique identifier for this group
     * @param item
     *            The display item for this group
     * @param parents
     *            The parent categories for this group
     *
     */
    public LockedItemGroup(NamespacedKey key, ItemStack item, NamespacedKey... parents) {
        this(key, item, 3, parents);
    }

    /**
     * The constructor for a LockedItemGroup.
     *
     * @param key
     *            A unique identifier for this group
     * @param item
     *            The display item for this group
     * @param tier
     *            The tier of this group
     * @param parents
     *            The parent categories for this group
     *
     */
    public LockedItemGroup(NamespacedKey key, ItemStack item, int tier, NamespacedKey... parents) {
        super(key, item, tier);

        this.keys = parents;
    }

    @Override
    public void register(SlimefunAddon addon) {
        super.register(addon);

        List<NamespacedKey> namespacedKeys = new ArrayList<>();

        for (NamespacedKey key : keys) {
            if (key != null) {
                namespacedKeys.add(key);
            }
        }

        for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
            if (namespacedKeys.remove(itemGroup.getKey())) {
                addParent(itemGroup);
            }
        }

        for (NamespacedKey key : namespacedKeys) {
            Slimefun.logger()
                    .log(
                            Level.INFO,
                            "没有找到 LockedItemGroup \"{1}\" 的 父组 \"{0}\",可能被卸载了.",
                            new Object[] {key, getKey()});
        }
    }

    /**
     * Adds a parent {@link ItemGroup} to this {@link LockedItemGroup}.
     *
     * @param group
     *            The {@link ItemGroup} to add as a parent
     *
     * @see #getParents()
     * @see #removeParent(ItemGroup)
     */
    public void addParent(ItemGroup group) {
        parents.add(group);
    }

    /**
     * Removes a {@link ItemGroup} from the parents of this {@link LockedItemGroup}.
     *
     * @param group
     *            The {@link ItemGroup} to remove from the parents of this {@link LockedItemGroup}
     *
     * @see #getParents()
     * @see #addParent(ItemGroup)
     */
    public void removeParent(ItemGroup group) {
        parents.remove(group);
    }

    /**
     * Checks if the {@link Player} has fully unlocked all parent categories.
     *
     * @param p
     *            The {@link Player} to check
     * @param profile
     *            The {@link PlayerProfile} that belongs to the given {@link Player}
     *
     * @return Whether the {@link Player} has fully completed all parent categories, otherwise false
     */
    public boolean hasUnlocked(Player p, PlayerProfile profile) {
        for (ItemGroup parent : parents) {
            for (SlimefunItem item : parent.getItems()) {
                // Check if the Player has researched every item (if the item is enabled)
                if (!item.isDisabledIn(p.getWorld())
                        && item.hasResearch()
                        && !profile.hasUnlocked(item.getResearch())) {
                    return false;
                }
            }
        }

        return true;
    }
}
