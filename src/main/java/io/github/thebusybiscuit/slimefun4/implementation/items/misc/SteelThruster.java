package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import org.bukkit.entity.Cow;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link SteelThruster} is a pretty basic crafting component.
 * However... as it is actually a bucket. We need to make sure that
 * Cows cannot be milked using it.
 *
 * @author TheBusyBiscuit
 *
 */
public class SteelThruster extends SlimefunItem {
    public SteelThruster(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(onRightClickBlock(), onRightClickEntity());
    }

    private static ItemUseHandler onRightClickBlock() {
        return PlayerRightClickEvent::cancel;
    }

    private static EntityInteractHandler onRightClickEntity() {
        return (e, item, hand) -> {
            // Milking cows with a rocket engine? Yeah, that would be weird.
            if (e.getRightClicked() instanceof Cow) {
                e.setCancelled(true);
            }
        };
    }
}
