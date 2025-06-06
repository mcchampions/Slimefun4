package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Goat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link ProduceCollector} allows you to collect produce from animals.
 * Providing it with a bucket and a nearby {@link Cow} will allow you to obtain milk.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 *
 */
public class ProduceCollector extends AContainer implements RecipeDisplayItem {
    private final ItemSetting<Integer> range = new IntRangeSetting(this, "range", 1, 2, 32);
    private final Set<AnimalProduce> animalProduces = new HashSet<>();

    public ProduceCollector(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(range);
    }

    @Override
    protected void registerDefaultRecipes() {
        // Milk from adult cows and goats
        addProduce(new AnimalProduce(new ItemStack(Material.BUCKET), new ItemStack(Material.MILK_BUCKET), n -> {
            if (n instanceof Cow ||  n instanceof Goat) {
                return ((Ageable) n).isAdult();
            } else {
                return false;
            }
        }));

        // Mushroom Stew from Mooshrooms
        addProduce(new AnimalProduce(new ItemStack(Material.BOWL), new ItemStack(Material.MUSHROOM_STEW), n -> {
            if (n instanceof MushroomCow mushroomCow) {
                return mushroomCow.isAdult();
            } else {
                return false;
            }
        }));
    }

    /**
     * This method adds a new {@link AnimalProduce} to this machine.
     *
     * @param produce
     *            The {@link AnimalProduce} to add
     */
    public void addProduce(AnimalProduce produce) {
        this.animalProduces.add(produce);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {
            @Override
            public void tick(Block b, SlimefunItem sf, SlimefunBlockData data) {
                ProduceCollector.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                // We override the preRegister() method to override the sync setting here
                return true;
            }
        });
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        displayRecipes.add(new CustomItemStack(Material.BUCKET, null, "&f需要附近有 &b牛 &f才会生产"));
        displayRecipes.add(new ItemStack(Material.MILK_BUCKET));

        displayRecipes.add(new CustomItemStack(Material.BUCKET, null, "&f需要附近有 &b山羊 &f才会生产"));
        displayRecipes.add(new ItemStack(Material.MILK_BUCKET));

        displayRecipes.add(new CustomItemStack(Material.BOWL, null, "&f需要附近有 &b哞菇 &f才会生产"));
        displayRecipes.add(new ItemStack(Material.MUSHROOM_STEW));

        return displayRecipes;
    }

    @Override
    protected @Nullable MachineRecipe findNextRecipe(BlockMenu inv) {
        for (int slot : getInputSlots()) {
            for (AnimalProduce produce : animalProduces) {
                ItemStack item = inv.getItemInSlot(slot);

                if (!SlimefunUtils.isItemSimilar(item, produce.getInput()[0], true)
                        || !InvUtils.fits(inv.toInventory(), produce.getOutput()[0], getOutputSlots())) {
                    continue;
                }

                if (isAnimalNearby(inv.getBlock(), produce)) {
                    inv.consumeItem(slot);
                    return produce;
                }
            }
        }

        return null;
    }

    private boolean isAnimalNearby(Block b, Predicate<LivingEntity> predicate) {
        int radius = range.getValue();
        return !b.getWorld()
                .getNearbyEntities(b.getLocation(), radius, radius, radius, n -> isValidAnimal(n, predicate))
                .isEmpty();
    }

    private static boolean isValidAnimal(Entity n, Predicate<LivingEntity> predicate) {
        if (n instanceof LivingEntity livingEntity) {
            return predicate.test(livingEntity);
        } else {
            return false;
        }
    }

    @Override
    public String getMachineIdentifier() {
        return "PRODUCE_COLLECTOR";
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.SHEARS);
    }
}
