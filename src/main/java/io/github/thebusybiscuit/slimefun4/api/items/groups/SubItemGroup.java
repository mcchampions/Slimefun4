package io.github.thebusybiscuit.slimefun4.api.items.groups;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link SubItemGroup} is a child {@link ItemGroup} of the
 * {@link NestedItemGroup}.
 *
 * @author TheBusyBiscuit
 *
 * @see NestedItemGroup
 *
 */
public class SubItemGroup extends ItemGroup {
    private final NestedItemGroup parentItemGroup;

    public SubItemGroup(NamespacedKey key, NestedItemGroup parent, ItemStack item) {
        this(key, parent, item, 3);
    }

    public SubItemGroup(NamespacedKey key, NestedItemGroup parent, ItemStack item, int tier) {
        super(key, item, tier);

        parentItemGroup = parent;
        parent.addSubGroup(this);
    }

    @Override
    public final boolean isVisible(Player p) {
        /*
         * Sub Categories are always hidden,
         * they won't show up in the normal guide view.
         */
        return false;
    }

    @Override
    public final boolean isAccessible(Player p) {
        /*
         * Sub Categories are accessible, they are invisible
         * but their items are available to the guide search.
         */
        return true;
    }

    /**
     * This method returns whether this {@link SubItemGroup} can be viewed
     * by the given {@link Player} in a {@link NestedItemGroup}.
     * Empty {@link ItemGroup ItemGroups} will not be visible.
     * This includes {@link ItemGroup ItemGroups} where every {@link SlimefunItem}
     * is disabled. If an {@link ItemGroup} is not accessible by the {@link Player},
     * see {@link #isAccessible(Player)}, this method will also return false.
     *
     * @param p
     *            The {@link Player} to check for
     *
     * @return Whether this {@link SubItemGroup} is visible to the given {@link Player}
     * in the {@link NestedItemGroup}
     */
    public final boolean isVisibleInNested(Player p) {
        return super.isVisible(p);
    }

    /**
     * This method returns the parent {@link NestedItemGroup} which this
     * {@link SubItemGroup} belongs to.
     *
     * @return The parent {@link NestedItemGroup}
     */
    public final NestedItemGroup getParent() {
        return parentItemGroup;
    }

    @Override
    public final void register(SlimefunAddon addon) {
        super.register(addon);

        if (!parentItemGroup.isRegistered()) {
            parentItemGroup.register(addon);
        }
    }
}
