package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * The {@link VanillaRecipe} implements an {@link AbstractRecipe} and represents a
 * {@link ShapedRecipe} or {@link ShapelessRecipe}.
 *
 * @author TheBusyBiscuit
 *
 * @see VanillaAutoCrafter
 *
 */
class VanillaRecipe extends AbstractRecipe {

    private final int[] slots = {11, 12, 13, 20, 21, 22, 29, 30, 31};
    @Getter
    private final Recipe recipe;

    VanillaRecipe(ShapelessRecipe recipe) {
        super(new ArrayList<>(recipe.getChoiceList()), recipe.getResult());

        this.recipe = recipe;
    }

    VanillaRecipe(ShapedRecipe recipe) {
        super(getChoices(recipe), recipe.getResult());

        this.recipe = recipe;
    }

    
    private static Collection<Predicate<ItemStack>> getChoices(ShapedRecipe recipe) {
        List<Predicate<ItemStack>> choices = new ArrayList<>();

        for (String row : recipe.getShape()) {
            for (char c : row.toCharArray()) {
                RecipeChoice choice = recipe.getChoiceMap().get(c);

                if (choice != null) {
                    choices.add(choice);
                }
            }
        }

        return choices;
    }

    @Override
    public void show(ChestMenu menu, AsyncRecipeChoiceTask task) {

        menu.replaceExistingItem(24, getResult().clone());
        menu.addMenuClickHandler(24, ChestMenuUtils.getEmptyClickHandler());

        RecipeChoice[] choices = Slimefun.getMinecraftRecipeService().getRecipeShape(recipe);
        ItemStack[] items = new ItemStack[9];

        if (choices.length == 1 && choices[0] instanceof MaterialChoice materialChoice) {
            items[4] = new ItemStack(materialChoice.getChoices().get(0));

            if (materialChoice.getChoices().size() > 1) {
                task.add(slots[4], materialChoice);
            }
        } else {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i] instanceof MaterialChoice materialChoice) {
                    items[i] = new ItemStack(materialChoice.getChoices().get(0));

                    if (materialChoice.getChoices().size() > 1) {
                        task.add(slots[i], materialChoice);
                    }
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            menu.replaceExistingItem(slots[i], items[i]);
            menu.addMenuClickHandler(slots[i], ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public String toString() {
        if (recipe instanceof Keyed keyed) {
            return keyed.getKey().toString();
        } else {
            return "invalid-recipe";
        }
    }
}
