package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.runes.SoulboundRune;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.SoulboundListener;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an Item that will not drop upon death.
 *
 * @author TheBusyBiscuit
 *
 * @see Soulbound
 * @see SoulboundRune
 * @see SoulboundListener
 *
 */
public class SoulboundItem extends SlimefunItem implements Soulbound, NotPlaceable {
    public SoulboundItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType type, ItemStack[] recipe) {
        super(itemGroup, item, type, recipe);
    }
}
