package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import city.norain.slimefun4.SlimefunExtended;
import city.norain.slimefun4.api.menu.UniversalMenu;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.blocks.Vein;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class WoodcutterAndroid extends ProgrammableAndroid {
    private static final int MAX_REACH = 160;

    public WoodcutterAndroid(
            ItemGroup itemGroup, int tier, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, tier, item, recipeType, recipe);
    }

    @Override
    public AndroidType getAndroidType() {
        return AndroidType.WOODCUTTER;
    }

    @Override
    protected boolean chopTree(Block b, UniversalMenu menu, BlockFace face) {
        Block target = b.getRelative(face);

        if (!target.getWorld().getWorldBorder().isInside(target.getLocation())) {
            return true;
        }

        if (Tag.LOGS.isTagged(target.getType())) {
            List<Block> list = Vein.find(target, MAX_REACH, block -> Tag.LOGS.isTagged(block.getType()));

            if (!list.isEmpty()) {
                Block log = list.get(list.size() - 1);
                log.getWorld().playEffect(log.getLocation(), Effect.STEP_SOUND, log.getType());

                OfflinePlayer owner = Bukkit.getOfflinePlayer(
                        UUID.fromString(StorageCacheUtils.getUniversalBlock(menu.getUuid(), b.getLocation(), "owner")));
                if (Slimefun.getProtectionManager().hasPermission(owner, log.getLocation(), Interaction.BREAK_BLOCK)) {
                    breakLog(log, b, menu, face);
                }

                return false;
            }
        }

        return true;
    }

    private void breakLog(Block log, Block android, UniversalMenu menu, BlockFace face) {
        ItemStack drop = new ItemStack(log.getType());

        // We try to push the log into the android's inventory, but nothing happens if it does not fit
        menu.pushItem(drop, getOutputSlots());

        log.getWorld().playEffect(log.getLocation(), Effect.STEP_SOUND, log.getType());

        // If the android just chopped the bottom log, we replant the appropriate sapling
        if (log.getY() == android.getRelative(face).getY()) {
            replant(log);
        } else {
            log.setType(Material.AIR);
        }
    }

    private static void replant(Block block) {
        Material logType = block.getType();
        Material saplingType = null;
        Predicate<Material> soilRequirement = null;

        switch (logType) {
            case OAK_LOG, OAK_WOOD, STRIPPED_OAK_LOG, STRIPPED_OAK_WOOD -> {
                saplingType = Material.OAK_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
            }
            case BIRCH_LOG, BIRCH_WOOD, STRIPPED_BIRCH_LOG, STRIPPED_BIRCH_WOOD -> {
                saplingType = Material.BIRCH_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
            }
            case JUNGLE_LOG, JUNGLE_WOOD, STRIPPED_JUNGLE_LOG, STRIPPED_JUNGLE_WOOD -> {
                saplingType = Material.JUNGLE_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
            }
            case SPRUCE_LOG, SPRUCE_WOOD, STRIPPED_SPRUCE_LOG, STRIPPED_SPRUCE_WOOD -> {
                saplingType = Material.SPRUCE_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
            }
            case ACACIA_LOG, ACACIA_WOOD, STRIPPED_ACACIA_LOG, STRIPPED_ACACIA_WOOD -> {
                saplingType = Material.ACACIA_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
            }
            case DARK_OAK_LOG, DARK_OAK_WOOD, STRIPPED_DARK_OAK_LOG, STRIPPED_DARK_OAK_WOOD -> {
                saplingType = Material.DARK_OAK_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
            }
            case CRIMSON_STEM, CRIMSON_HYPHAE, STRIPPED_CRIMSON_STEM, STRIPPED_CRIMSON_HYPHAE -> {
                saplingType = Material.CRIMSON_FUNGUS;
                soilRequirement = SlimefunTag.FUNGUS_SOIL::isTagged;
            }
            case WARPED_STEM, WARPED_HYPHAE, STRIPPED_WARPED_STEM, STRIPPED_WARPED_HYPHAE -> {
                saplingType = Material.WARPED_FUNGUS;
                soilRequirement = SlimefunTag.FUNGUS_SOIL::isTagged;
            }
            default -> {
            }
        }

        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_19)) {
            switch (logType) {
                case MANGROVE_LOG, STRIPPED_MANGROVE_LOG -> {
                    saplingType = Material.MANGROVE_PROPAGULE;
                    soilRequirement = SlimefunTag.MANGROVE_BASE_BLOCKS::isTagged;
                }
                default -> {
                }
            }
        }

        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20)) {
            switch (logType) {
                case CHERRY_LOG, STRIPPED_CHERRY_LOG -> {
                    saplingType = Material.CHERRY_SAPLING;
                    soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
                }
                default -> {
                }
            }
        }

        if (SlimefunExtended.getMinecraftVersion().isAtLeast(1, 21, 2)) {
            if (logType ==  Material.getMaterial("PALE_OAK_LOG") || logType == Material.getMaterial("PALE_OAK_WOOD")
                    || logType ==  Material.getMaterial("STRIPPED_PALE_OAK_LOG") || logType ==  Material.getMaterial("STRIPPED_PALE_OAK_WOOD")) {
                    saplingType = Material.getMaterial("PALE_OAK_SAPLING");
                    soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
            }
        }

        if (saplingType != null) {
            if (soilRequirement.test(block.getRelative(BlockFace.DOWN).getType())) {
                // Replant the block
                block.setType(saplingType);
            } else {
                // Simply drop the sapling if the soil does not fit
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(saplingType));
                block.setType(Material.AIR);
            }
        }
    }
}
