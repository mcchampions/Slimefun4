package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.inventory.ItemStack;

/**
 * This implementation of {@link SlimefunItem} represents a Broken Spawner.
 * A {@link BrokenSpawner} can be repaired into a {@link RepairedSpawner}.
 * Without repairing, the block will be unplaceable.
 *
 * @author TheBusyBiscuit
 *
 * @see RepairedSpawner
 *
 */
public class BrokenSpawner extends AbstractMonsterSpawner implements NotPlaceable {
    public BrokenSpawner(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(onRightClick());
    }

    private static ItemUseHandler onRightClick() {
        return PlayerRightClickEvent::cancel;
    }
}
