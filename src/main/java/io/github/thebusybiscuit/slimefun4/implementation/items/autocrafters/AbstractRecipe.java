package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.EnhancedCraftingTable;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * This class abstracts away from concrete recipes.
 * It supports {@link ShapedRecipe}, {@link ShapelessRecipe} and
 * recipes made using the {@link EnhancedCraftingTable}.
 *
 * @author TheBusyBiscuit
 *
 * @see AbstractAutoCrafter
 * @see VanillaRecipe
 * @see SlimefunItemRecipe
 *
 */
@Getter
public abstract class AbstractRecipe {
    /**
     * Our {@link Collection} of ingredients / predicates.
     * -- GETTER --
     *  This returns the
     *  of ingredients as
     * .
     *

     */
    private final Collection<Predicate<ItemStack>> ingredients;

    /**
     * The recipe result.
     * -- GETTER --
     *  This returns the result of this
     * .
     *  This will return the original
     * , so make sure to
     *  it.
     *

     */
    private final ItemStack result;

    /**
     * Whether this recipe is enabled.
     * -- GETTER --
     *  This returns whether or not this recipe has been enabled.
     *  A disabled recipe will not be crafted.
     *

     */
    private boolean enabled = true;

    /**
     * Protected constructor. For implementation classes only.
     *
     * @param ingredients
     *            The ingredients for this recipe as predicates
     * @param result
     *            The resulting {@link ItemStack}
     */
    protected AbstractRecipe(Collection<Predicate<ItemStack>> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    /**
     * This method enables or disables this recipe.
     * A disabled recipe will not be crafted.
     *
     * @param enabled
     *            Whether this recipe is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * This will visually represent this {@link AbstractRecipe} in the given {@link ChestMenu}.
     * Any {@link MaterialChoice} will be cycled through using the {@link AsyncRecipeChoiceTask}.
     *
     * @param menu
     *            The {@link ChestMenu} to display the recipe in
     * @param task
     *            The {@link AsyncRecipeChoiceTask} instance
     */
    public abstract void show(ChestMenu menu, AsyncRecipeChoiceTask task);

    /**
     * This is our static accessor for the {@link AbstractRecipe} class.
     * It will create a new {@link VanillaRecipe} for the given {@link Recipe}
     * if it is a valid {@link Recipe}.
     * <p>
     * Currently supported recipe types are {@link ShapedRecipe} and {@link ShapelessRecipe}.
     * If the {@link Recipe} is null or none of the aforementioned types, null will be returned.
     *
     * @param recipe
     *            The {@link Recipe} to wrap
     *
     * @return The wrapped {@link AbstractRecipe} or null
     */
    @Nullable public static AbstractRecipe of(@Nullable Recipe recipe) {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return new VanillaRecipe(shapedRecipe);
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            return new VanillaRecipe(shapelessRecipe);
        } else {
            return null;
        }
    }

    /**
     * This static accessor is for {@link SlimefunItem} recipes.
     * Note that the {@link SlimefunItem} must be crafted using the specified {@link RecipeType},
     * otherwise null will be returned.
     *
     * @param item
     *            The {@link SlimefunItem} the recipe belongs to
     * @param recipeType
     *            The {@link RecipeType}
     *
     * @return The wrapped {@link AbstractRecipe} or null
     */
    @Nullable public static AbstractRecipe of(@Nullable SlimefunItem item, RecipeType recipeType) {
        if (item != null && item.getRecipeType().equals(recipeType)) {
            return new SlimefunItemRecipe(item);
        } else {
            return null;
        }
    }
}
