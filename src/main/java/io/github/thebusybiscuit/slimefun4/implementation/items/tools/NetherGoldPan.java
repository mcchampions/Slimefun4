package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.settings.GoldPanDrop;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link NetherGoldPan} is a variant of the regular {@link GoldPan}
 * which can be used on Soul Sand.
 *
 * @author TheBusyBiscuit
 * @author svr333
 * @author JustAHuman
 */
public class NetherGoldPan extends GoldPan {
    private final Set<Material> inputMaterials = EnumSet.of(Material.SOUL_SAND, Material.SOUL_SOIL);

    public NetherGoldPan(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    @Deprecated(since = "RC-36")
    public Material getInputMaterial() {
        return Material.SOUL_SAND;
    }

    @Override
    public Set<Material> getInputMaterials() {
        return inputMaterials;
    }

    @Override
    protected Set<GoldPanDrop> getGoldPanDrops() {
        Set<GoldPanDrop> settings = new HashSet<>();

        settings.add(new GoldPanDrop(this, "chance.QUARTZ", 50, new ItemStack(Material.QUARTZ)));
        settings.add(new GoldPanDrop(this, "chance.GOLD_NUGGET", 25, new ItemStack(Material.GOLD_NUGGET)));
        settings.add(new GoldPanDrop(this, "chance.NETHER_WART", 10, new ItemStack(Material.NETHER_WART)));
        settings.add(new GoldPanDrop(this, "chance.BLAZE_POWDER", 8, new ItemStack(Material.BLAZE_POWDER)));
        settings.add(new GoldPanDrop(this, "chance.GLOWSTONE_DUST", 5, new ItemStack(Material.GLOWSTONE_DUST)));
        settings.add(new GoldPanDrop(this, "chance.GHAST_TEAR", 2, new ItemStack(Material.GHAST_TEAR)));

        return settings;
    }
}
