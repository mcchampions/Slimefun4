package io.github.thebusybiscuit.slimefun4.api.recipes;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.recipes.MinecraftRecipe;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.localization.SlimefunLocalization;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AltarRecipe;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;

import java.util.*;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// TODO: Remove this class and rewrite the recipe system
public class RecipeType implements Keyed {
    public static final RecipeType MULTIBLOCK = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "multiblock"),
            new CustomItemStack(Material.BRICKS, "&bMultiBlock", "", "&a&oBuild it in the World"));
    public static final RecipeType ARMOR_FORGE = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "armor_forge"),
            SlimefunItems.ARMOR_FORGE,
            "",
            "&a&oCraft it in an Armor Forge");
    public static final RecipeType GRIND_STONE = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "grind_stone"),
            SlimefunItems.GRIND_STONE,
            "",
            "&a&oGrind it using the Grind Stone");
    public static final RecipeType SMELTERY = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "smeltery"),
            SlimefunItems.SMELTERY,
            "",
            "&a&oSmelt it using a Smeltery");
    public static final RecipeType ORE_CRUSHER = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "ore_crusher"),
            SlimefunItems.ORE_CRUSHER,
            "",
            "&a&oCrush it using the Ore Crusher");
    public static final RecipeType GOLD_PAN = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "gold_pan"),
            SlimefunItems.GOLD_PAN,
            "",
            "&a&oUse a Gold Pan on Gravel to obtain this Item");
    public static final RecipeType COMPRESSOR = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "compressor"),
            SlimefunItems.COMPRESSOR,
            "",
            "&a&oCompress it using the Compressor");
    public static final RecipeType PRESSURE_CHAMBER = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "pressure_chamber"),
            SlimefunItems.PRESSURE_CHAMBER,
            "",
            "&a&oCompress it using the Pressure Chamber");
    public static final RecipeType MAGIC_WORKBENCH = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "magic_workbench"),
            SlimefunItems.MAGIC_WORKBENCH,
            "",
            "&a&oCraft it in a Magic Workbench");
    public static final RecipeType ORE_WASHER = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "ore_washer"),
            SlimefunItems.ORE_WASHER,
            "",
            "&a&oWash it in an Ore Washer");
    public static final RecipeType ENHANCED_CRAFTING_TABLE = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "enhanced_crafting_table"),
            SlimefunItems.ENHANCED_CRAFTING_TABLE,
            "",
            "&a&oA regular Crafting Table cannot",
            "&a&ohold this massive Amount of Power...");
    public static final RecipeType JUICER = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "juicer"), SlimefunItems.JUICER, "", "&a&oUsed for Juice Creation");

    public static final RecipeType ANCIENT_ALTAR = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "ancient_altar"),
            SlimefunItems.ANCIENT_ALTAR,
            (recipe, output) -> {
                AltarRecipe altarRecipe = new AltarRecipe(Arrays.asList(recipe), output);
                AncientAltar altar = ((AncientAltar) SlimefunItems.ANCIENT_ALTAR.getItem());
                altar.getRecipes().add(altarRecipe);
            },
            (recipe, output) -> {
                AltarRecipe altarRecipe = new AltarRecipe(Arrays.asList(recipe), output);
                AncientAltar altar = ((AncientAltar) SlimefunItems.ANCIENT_ALTAR.getItem());
                altar.getRecipes().removeIf(ar -> ar.equals(altarRecipe));
            });

    public static final RecipeType MOB_DROP = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "mob_drop"),
            new CustomItemStack(Material.IRON_SWORD, "&bMob Drop"),
            RecipeType::registerMobDrop,
            "",
            "&rKill the specified Mob to obtain this Item");
    public static final RecipeType BARTER_DROP = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "barter_drop"),
            new CustomItemStack(Material.GOLD_INGOT, "&bBarter Drop"),
            RecipeType::registerBarterDrop,
            "&aBarter with piglins for a chance",
            "&ato obtain this item");
    public static final RecipeType INTERACT = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "interact"),
            new CustomItemStack(Material.PLAYER_HEAD, "&bInteract", "", "&a&oRight click with this item"));

    public static final RecipeType HEATED_PRESSURE_CHAMBER = new RecipeType(
            new NamespacedKey(Slimefun.instance(), "heated_pressure_chamber"), SlimefunItems.HEATED_PRESSURE_CHAMBER);
    public static final RecipeType FOOD_FABRICATOR =
            new RecipeType(new NamespacedKey(Slimefun.instance(), "food_fabricator"), SlimefunItems.FOOD_FABRICATOR);
    public static final RecipeType FOOD_COMPOSTER =
            new RecipeType(new NamespacedKey(Slimefun.instance(), "food_composter"), SlimefunItems.FOOD_COMPOSTER);
    public static final RecipeType FREEZER =
            new RecipeType(new NamespacedKey(Slimefun.instance(), "freezer"), SlimefunItems.FREEZER);
    public static final RecipeType REFINERY =
            new RecipeType(new NamespacedKey(Slimefun.instance(), "refinery"), SlimefunItems.REFINERY);

    public static final RecipeType GEO_MINER =
            new RecipeType(new NamespacedKey(Slimefun.instance(), "geo_miner"), SlimefunItems.GEO_MINER);
    public static final RecipeType NUCLEAR_REACTOR =
            new RecipeType(new NamespacedKey(Slimefun.instance(), "nuclear_reactor"), SlimefunItems.NUCLEAR_REACTOR);

    public static final RecipeType NULL = new RecipeType();

    private final ItemStack item;
    private final NamespacedKey key;
    private final String machine;
    private BiConsumer<ItemStack[], ItemStack> registerConsumer;
    private BiConsumer<ItemStack[], ItemStack> unregisterConsumer;

    private RecipeType() {
        this.item = null;
        this.machine = "";
        this.key = new NamespacedKey(Slimefun.instance(), "null");
    }

    public RecipeType(ItemStack item, String machine) {
        this.item = item;
        this.machine = machine;

        if (!machine.isEmpty()) {
            this.key = new NamespacedKey(Slimefun.instance(), machine.toLowerCase(Locale.ROOT));
        } else {
            this.key = new NamespacedKey(Slimefun.instance(), "unknown");
        }
    }

    public RecipeType(NamespacedKey key, SlimefunItemStack slimefunItem, String... lore) {
        this(key, slimefunItem, null, lore);
    }

    public RecipeType(NamespacedKey key, ItemStack item, BiConsumer<ItemStack[], ItemStack> callback, String... lore) {
        this.item = new CustomItemStack(item, null, lore);
        this.key = key;
        this.registerConsumer = callback;

        if (item instanceof SlimefunItemStack slimefunItemStack) {
            this.machine = slimefunItemStack.getItemId();
        } else {
            this.machine = "";
        }
    }

    public RecipeType(
            NamespacedKey key,
            ItemStack item,
            BiConsumer<ItemStack[], ItemStack> registerCallback,
            BiConsumer<ItemStack[], ItemStack> unregisterCallback,
            String... lore) {
        this.item = new CustomItemStack(item, null, lore);
        this.key = key;
        this.registerConsumer = registerCallback;
        this.unregisterConsumer = unregisterCallback;

        if (item instanceof SlimefunItemStack slimefunItemStack) {
            this.machine = slimefunItemStack.getItemId();
        } else {
            this.machine = "";
        }
    }

    public RecipeType(NamespacedKey key, ItemStack item) {
        this.key = key;
        this.item = item;
        this.machine = item instanceof SlimefunItemStack slimefunItemStack ? slimefunItemStack.getItemId() : "";
    }

    public RecipeType(MinecraftRecipe<?> recipe) {
        this.item = new ItemStack(recipe.getMachine());
        this.machine = "";
        this.key = NamespacedKey.minecraft(
                recipe.getRecipeClass().getSimpleName().toLowerCase(Locale.ROOT).replace("recipe", ""));
    }

    public void register(ItemStack[] recipe, ItemStack result) {
        if (registerConsumer != null) {
            registerConsumer.accept(recipe, result);
        } else {
            SlimefunItem slimefunItem = SlimefunItem.getById(this.machine);

            if (slimefunItem instanceof MultiBlockMachine mbm) {
                mbm.addRecipe(recipe, result);
            }
        }
    }

    public void unregister(ItemStack[] recipe, ItemStack result) {
        if (unregisterConsumer != null) {
            unregisterConsumer.accept(recipe, result);
        } else {
            SlimefunItem slimefunItem = SlimefunItem.getById(this.machine);

            if (slimefunItem instanceof MultiBlockMachine mbm) {
                mbm.clearRecipe();
            }
        }
    }

    public @Nullable ItemStack toItem() {
        return this.item;
    }

    public ItemStack getItem(Player p) {
        return SlimefunLocalization.getRecipeTypeItem(p, this);
    }

    public SlimefunItem getMachine() {
        return SlimefunItem.getById(machine);
    }

    @Override
    public final NamespacedKey getKey() {
        return key;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof RecipeType recipeType) {
            return recipeType.key.equals(this.key);
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return key.hashCode();
    }

    private static void registerBarterDrop(ItemStack[] recipe, ItemStack output) {
        Slimefun.getRegistry().getBarteringDrops().add(output);
    }

    private static void registerMobDrop(ItemStack[] recipe, ItemStack output) {
        String mob = TextUtils.toPlainText(recipe[4].getItemMeta().getDisplayName())
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');
        EntityType entity = EntityType.valueOf(mob);
        Set<ItemStack> dropping = Slimefun.getRegistry().getMobDrops().getOrDefault(entity, new HashSet<>());
        dropping.add(output);
        Slimefun.getRegistry().getMobDrops().put(entity, dropping);
    }

    public static List<ItemStack> getRecipeInputs(MultiBlockMachine machine) {
        if (machine == null) {
            return new ArrayList<>();
        }

        List<ItemStack[]> recipes = machine.getRecipes();
        List<ItemStack> convertible = new ArrayList<>();

        for (int i = 0; i < recipes.size(); i++) {
            if (i % 2 == 0) {
                convertible.add(recipes.get(i)[0]);
            }
        }

        return convertible;
    }

    public static List<ItemStack[]> getRecipeInputList(MultiBlockMachine machine) {
        if (machine == null) {
            return new ArrayList<>();
        }

        List<ItemStack[]> recipes = machine.getRecipes();
        List<ItemStack[]> convertible = new ArrayList<>();

        for (int i = 0; i < recipes.size(); i++) {
            if (i % 2 == 0) {
                convertible.add(recipes.get(i));
            }
        }

        convertible.sort(Comparator.comparing(recipe -> {
            int emptySlots = 9;

            for (ItemStack ingredient : recipe) {
                if (ingredient != null) {
                    emptySlots--;
                }
            }

            return emptySlots;
        }));

        return convertible;
    }

    public static ItemStack getRecipeOutput(MultiBlockMachine machine, ItemStack input) {
        List<ItemStack[]> recipes = machine.getRecipes();
        return recipes.get(((getRecipeInputs(machine).indexOf(input) << 1) + 1))[0].clone();
    }

    public static ItemStack getRecipeOutputList(MultiBlockMachine machine, ItemStack[] input) {
        List<ItemStack[]> recipes = machine.getRecipes();
        return recipes.get((recipes.indexOf(input) + 1))[0];
    }
}
