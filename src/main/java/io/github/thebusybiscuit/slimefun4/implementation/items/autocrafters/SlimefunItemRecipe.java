package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.inventory.ItemStack;

/**
 * This {@link AbstractRecipe} implementation stands for a {@link SlimefunItem} which
 * is crafted using any {@link RecipeType}.
 *
 * @author TheBusyBiscuit
 *
 * @see SlimefunAutoCrafter
 *
 */
class SlimefunItemRecipe extends AbstractRecipe {
    private final int[] slots = {11, 12, 13, 20, 21, 22, 29, 30, 31};
    private final SlimefunItem item;

    SlimefunItemRecipe(SlimefunItem item) {
        super(getInputs(item), item.getRecipeOutput());
        this.item = item;
    }

    private static Collection<Predicate<ItemStack>> getInputs(SlimefunItem item) {
        List<Predicate<ItemStack>> predicates = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            ItemStack ingredient = item.getRecipe()[i];

            if (ingredient != null && !ingredient.getType().isAir()) {
                predicates.add(stack -> SlimefunUtils.isItemSimilar(stack, ingredient, true));
            }
        }

        return predicates;
    }

    @Override
    public void show(ChestMenu menu, AsyncRecipeChoiceTask task) {
        menu.addItem(24, getResult().clone(), ChestMenuUtils.getEmptyClickHandler());
        ItemStack[] recipe = item.getRecipe();

        for (int i = 0; i < 9; i++) {
            menu.addItem(slots[i], recipe[i], ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public String toString() {
        return item.getId();
    }
}
