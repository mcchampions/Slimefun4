package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.qscbm.slimefun4.message.QsTextComponentImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The {@link IndustrialMiner} is a {@link MultiBlockMachine} that can mine any
 * ores it finds in a given range underneath where it was placed.
 * <p>
 * <i>And for those of you who are wondering... yes this is the replacement for the
 * long-time deprecated Digital Miner.</i>
 *
 * @author TheBusyBiscuit
 * @see AdvancedIndustrialMiner
 * @see MiningTask
 */
public class IndustrialMiner extends MultiBlockMachine {
    protected final Map<Location, MiningTask> activeMiners = new HashMap<>();
    protected final List<MachineFuel> fuelTypes = new ArrayList<>();

    private final OreDictionary oreDictionary;
    private final ItemSetting<Boolean> canMineAncientDebris = new ItemSetting<>(this, "can-mine-ancient-debris", false);
    private final ItemSetting<Boolean> canMineDeepslateOres = new ItemSetting<>(this, "can-mine-deepslate-ores", true);
    private final boolean silkTouch;
    @Getter
    private final int range;

    public IndustrialMiner(
            ItemGroup itemGroup, SlimefunItemStack item, Material baseMaterial, boolean silkTouch, int range) {
        super(
                itemGroup,
                item,
                new ItemStack[]{
                        null,
                        null,
                        null,
                        new CustomItemStack(Material.PISTON, "活塞 (朝上)"),
                        new ItemStack(Material.CHEST),
                        new CustomItemStack(Material.PISTON, "活塞 (朝上)"),
                        new ItemStack(baseMaterial),
                        new ItemStack(Material.BLAST_FURNACE),
                        new ItemStack(baseMaterial)
                },
                BlockFace.UP);

        this.oreDictionary = OreDictionary.getInstance();
        this.range = range;
        this.silkTouch = silkTouch;

        registerDefaultFuelTypes();
        addItemSetting(canMineAncientDebris);
        addItemSetting(canMineDeepslateOres);
    }

    /**
     * This returns whether this {@link IndustrialMiner} will output ores as they are.
     * Similar to the Silk Touch {@link Enchantment}.
     *
     * @return Whether to treat ores with Silk Touch
     */
    public boolean hasSilkTouch() {
        return silkTouch;
    }

    /**
     * This registers the various types of fuel that can be used in the
     * {@link IndustrialMiner}.
     */
    protected void registerDefaultFuelTypes() {
        // Coal & Charcoal
        fuelTypes.add(new MachineFuel(4, new ItemStack(Material.COAL)));
        fuelTypes.add(new MachineFuel(4, new ItemStack(Material.CHARCOAL)));

        fuelTypes.add(new MachineFuel(40, new ItemStack(Material.COAL_BLOCK)));
        fuelTypes.add(new MachineFuel(10, new ItemStack(Material.DRIED_KELP_BLOCK)));
        fuelTypes.add(new MachineFuel(4, new ItemStack(Material.BLAZE_ROD)));

        // Logs
        for (Material mat : Tag.LOGS.getValues()) {
            fuelTypes.add(new MachineFuel(1, new ItemStack(mat)));
        }
    }

    /**
     * This method returns the outcome that mining certain ores yields.
     *
     * @param material The {@link Material} of the ore that was mined
     * @return The outcome when mining this ore
     */
    public ItemStack getOutcome(Material material) {
        if (hasSilkTouch()) {
            return new ItemStack(material);
        } else {
            Random random = ThreadLocalRandom.current();
            return oreDictionary.getDrops(material, random);
        }
    }

    /**
     * This registers a new fuel type for this {@link IndustrialMiner}.
     *
     * @param ores The amount of ores this allows you to mine
     * @param item The item that shall be consumed
     */
    public void addFuelType(int ores, ItemStack item) {
        fuelTypes.add(new MachineFuel(ores / 2, item));
    }

    @Override
    public String getLabelLocalPath() {
        return "guide.tooltips.recipes.generator";
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> list = new ArrayList<>();

        for (MachineFuel fuel : fuelTypes) {
            ItemStack item = fuel.getInput().clone();
            ItemMeta im = item.getItemMeta();
            List<Component> lore = new ArrayList<>();
            lore.add(new QsTextComponentImpl("\u21E8 ").color(NamedTextColor.DARK_GRAY)
                    .append(new QsTextComponentImpl("剩余最多 " + fuel.getTicks() + " 个矿石").color(
                            NamedTextColor.GRAY
                    )));
            im.lore(lore);
            item.setItemMeta(im);
            list.add(item);
        }

        return list;
    }

    @Override
    public void onInteract(Player p, Block b) {
        if (activeMiners.containsKey(b.getLocation())) {
            Slimefun.getLocalization().sendMessage(p, "machines.INDUSTRIAL_MINER.already-running");
            return;
        }

        Block chest = b.getRelative(BlockFace.UP);
        Block[] pistons = findPistons(chest);

        int mod = range;
        Block start = b.getRelative(-mod, -1, -mod);
        Block end = b.getRelative(mod, -1, mod);

        MiningTask task = new MiningTask(this, p.getUniqueId(), chest, pistons, start, end);
        task.start(b);
    }

    private static Block[] findPistons(Block chest) {
        Block northern = chest.getRelative(BlockFace.NORTH);

        if (northern.getType() == Material.PISTON) {
            return new Block[]{northern, chest.getRelative(BlockFace.SOUTH)};
        } else {
            return new Block[]{chest.getRelative(BlockFace.WEST), chest.getRelative(BlockFace.EAST)};
        }
    }

    /**
     * This returns whether this {@link IndustrialMiner} can mine the given {@link Block}.
     *
     * @param block The {@link Block} to check
     * @return Whether this {@link IndustrialMiner} is capable of mining this {@link Block}
     */
    public boolean canMine(Block block) {
        Material type = block.getType();
        MinecraftVersion version = Slimefun.getMinecraftVersion();
        if (type == Material.ANCIENT_DEBRIS) {
            return canMineAncientDebris.getValue() && !StorageCacheUtils.hasSlimefunBlock(block.getLocation());
        } else if (version.isAtLeast(MinecraftVersion.MINECRAFT_1_17) && SlimefunTag.DEEPSLATE_ORES.isTagged(type)) {
            return canMineDeepslateOres.getValue() && !StorageCacheUtils.hasSlimefunBlock(block.getLocation());
        } else {
            return SlimefunTag.INDUSTRIAL_MINER_ORES.isTagged(type)
                   && !StorageCacheUtils.hasSlimefunBlock(block.getLocation());
        }
    }
}
