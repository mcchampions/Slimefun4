package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotHopperable;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedPotionType;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.qscbm.slimefun4.slimefunitems.machines.ASpeedableContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

/**
 *
 * The {@link AutoBrewer} machine with most if not all potion recipes.
 *
 * @author Linox
 *
 */
public class AutoBrewer extends ASpeedableContainer implements NotHopperable {

    private static final Map<Material, PotionType> potionRecipes = new EnumMap<>(Material.class);
    private static final Map<PotionType, PotionType> fermentations = new EnumMap<>(PotionType.class);

    static {
        potionRecipes.put(Material.SUGAR, VersionedPotionType.SWIFTNESS);
        potionRecipes.put(Material.RABBIT_FOOT, VersionedPotionType.LEAPING);
        potionRecipes.put(Material.BLAZE_POWDER, PotionType.STRENGTH);
        potionRecipes.put(Material.GLISTERING_MELON_SLICE, VersionedPotionType.HEALING);
        potionRecipes.put(Material.SPIDER_EYE, PotionType.POISON);
        potionRecipes.put(Material.GHAST_TEAR, VersionedPotionType.REGENERATION);
        potionRecipes.put(Material.MAGMA_CREAM, PotionType.FIRE_RESISTANCE);
        potionRecipes.put(Material.PUFFERFISH, PotionType.WATER_BREATHING);
        potionRecipes.put(Material.GOLDEN_CARROT, PotionType.NIGHT_VISION);
        potionRecipes.put(Material.TURTLE_HELMET, PotionType.TURTLE_MASTER);
        potionRecipes.put(Material.PHANTOM_MEMBRANE, PotionType.SLOW_FALLING);

        fermentations.put(VersionedPotionType.SWIFTNESS, PotionType.SLOWNESS);
        fermentations.put(VersionedPotionType.LEAPING, PotionType.SLOWNESS);
        fermentations.put(VersionedPotionType.HEALING, VersionedPotionType.HARMING);
        fermentations.put(PotionType.POISON, VersionedPotionType.HARMING);
        fermentations.put(PotionType.NIGHT_VISION, PotionType.INVISIBILITY);
    }

    @ParametersAreNonnullByDefault
    public AutoBrewer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    protected @Nullable MachineRecipe findNextRecipe(BlockMenu menu) {
        ItemStack input1 = menu.getItemInSlot(getInputSlots()[0]);
        ItemStack input2 = menu.getItemInSlot(getInputSlots()[1]);

        if (input1 == null || input2 == null) {
            return null;
        }

        if (isPotion(input1.getType()) || isPotion(input2.getType())) {
            boolean isPotionInFirstSlot = isPotion(input1.getType());
            ItemStack ingredient = isPotionInFirstSlot ? input2 : input1;

            // Reject any named items
            if (ingredient.hasItemMeta()) {
                return null;
            }

            ItemStack potionItem = isPotionInFirstSlot ? input1 : input2;
            PotionMeta potion = (PotionMeta) potionItem.getItemMeta();
            ItemStack output = brew(ingredient.getType(), potionItem.getType(), potion);

            if (output == null) {
                return null;
            }

            output.setItemMeta(potion);

            if (!InvUtils.fits(menu.toInventory(), output, getOutputSlots())) {
                return null;
            }

            for (int slot : getInputSlots()) {
                menu.consumeItem(slot);
            }

            return new MachineRecipe(30, new ItemStack[] {input1, input2}, new ItemStack[] {output});
        } else {
            return null;
        }
    }

    @ParametersAreNonnullByDefault
    private @Nullable ItemStack brew(Material input, Material potionType, PotionMeta potion) {
        PotionType type = potion.getBasePotionType();
        if (type == PotionType.WATER) {
            if (input == Material.FERMENTED_SPIDER_EYE) {
                potion.setBasePotionType(PotionType.WEAKNESS);
                return new ItemStack(potionType);
            } else if (input == Material.NETHER_WART) {
                potion.setBasePotionType(PotionType.AWKWARD);
                return new ItemStack(potionType);
            } else if (potionType == Material.POTION && input == Material.GUNPOWDER) {
                return new ItemStack(Material.SPLASH_POTION);
            } else if (potionType == Material.SPLASH_POTION && input == Material.DRAGON_BREATH) {
                return new ItemStack(Material.LINGERING_POTION);
            }
        } else if (input == Material.FERMENTED_SPIDER_EYE) {
            PotionType fermented = fermentations.get(type);

            if (fermented != null) {
                potion.setBasePotionType(fermented);
                return new ItemStack(potionType);
            }
        } else if (input == Material.REDSTONE && type.isExtendable() && !type.isUpgradeable()) {
            // Fixes #3390 - Potions can only be either extended or upgraded. Not both.
            potion.setBasePotionType(type);
            return new ItemStack(potionType);
        } else if (input == Material.GLOWSTONE_DUST && type.isUpgradeable() && !type.isExtendable()) {
            // Fixes #3390 - Potions can only be either extended or upgraded. Not both.
            potion.setBasePotionType(type);
            return new ItemStack(potionType);
        } else if (type == PotionType.AWKWARD) {
            PotionType potionRecipe = potionRecipes.get(input);

            if (potionRecipe != null) {
                potion.setBasePotionType(potionRecipe);
                return new ItemStack(potionType);
            }
        }

        return null;
    }

    /**
     * Checks whether a given {@link Material} is a valid Potion material.
     *
     * @param mat
     *            The {@link Material} to check
     *
     * @return Whether this {@link Material} is a valid potion
     */
    private boolean isPotion(Material mat) {
        return mat == Material.POTION || mat == Material.SPLASH_POTION || mat == Material.LINGERING_POTION;
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.FISHING_ROD);
    }

    @Override
    public String getMachineIdentifier() {
        return "AUTO_BREWER";
    }
}
