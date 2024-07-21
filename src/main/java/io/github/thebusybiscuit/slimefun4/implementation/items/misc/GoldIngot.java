package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Smeltery;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link GoldIngot} from Slimefun is a simple resource which is divided into different
 * levels of carat ratings.
 * <p>
 * It can be obtained via gold dust and other gold ingots in a {@link Smeltery}.
 *
 * @author TheBusyBiscuit
 *
 * @see Smeltery
 *
 */
@Getter
public class GoldIngot extends SlimefunItem {
    /**
     * The carat rating.
     * -- GETTER --
     *  This returns the carat rating of this
     * .
     *  <p>
     *  The purity of the
     *  is measured in carat (1-24).
     *  <pre>
     *  24k = 100% gold.
     *  18k = 75% gold.
     *  12k = 50% gold.
     *  </pre>
     *  and so on...
     *

     */
    private final int caratRating;

    public GoldIngot(
            ItemGroup itemGroup, int caratRating, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.caratRating = caratRating;
    }

}
