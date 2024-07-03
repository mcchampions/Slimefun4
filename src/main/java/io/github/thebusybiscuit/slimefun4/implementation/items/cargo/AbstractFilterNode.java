package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This abstract super class represents all filtered Cargo nodes.
 *
 * @author TheBusyBiscuit
 *
 * @see CargoInputNode
 * @see AdvancedCargoOutputNode
 *
 */
abstract class AbstractFilterNode extends AbstractCargoNode {

    protected static final int[] SLOTS = {19, 20, 21, 28, 29, 30, 37, 38, 39};
    private static final String FILTER_TYPE = "filter-type";
    private static final String FILTER_LORE = "filter-lore";

    @ParametersAreNonnullByDefault
    protected AbstractFilterNode(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);

        addItemHandler(onBreak());
    }

    @Override
    public boolean hasItemFilter() {
        return true;
    }

    
    private BlockBreakHandler onBreak() {
        return new SimpleBlockBreakHandler() {

            @Override
            public void onBlockBreak(Block b) {
                BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());

                if (inv != null) {
                    inv.dropItems(b.getLocation(), SLOTS);
                }
            }
        };
    }

    
    protected abstract int[] getBorder();

    @Override
    protected void onPlace(BlockPlaceEvent e) {
        var blockData = StorageCacheUtils.getBlock(e.getBlock().getLocation());
        blockData.setData("index", "0");
        blockData.setData(FILTER_TYPE, "whitelist");
        blockData.setData(FILTER_LORE, String.valueOf(true));
        blockData.setData("filter-durability", String.valueOf(false));
    }

    @Override
    protected void createBorder(BlockMenuPreset preset) {
        for (int i : getBorder()) {
            preset.addItem(
                    i,
                    new CustomItemStack(Material.CYAN_STAINED_GLASS_PANE, " "),
                    ChestMenuUtils.getEmptyClickHandler());
        }

        preset.addItem(
                2,
                new CustomItemStack(Material.PAPER, "&3物品", "", "&b将你想要添加到黑白名单", "&b的物品放入此处"),
                ChestMenuUtils.getEmptyClickHandler());
    }

    @Override
    protected void updateBlockMenu(BlockMenu menu, Block b) {
        Location loc = b.getLocation();
        var blockData = StorageCacheUtils.getBlock(b.getLocation());
        String filterType = blockData.getData(FILTER_TYPE);

        if (filterType == null || filterType.equals("whitelist")) {
            menu.replaceExistingItem(15, new CustomItemStack(Material.WHITE_WOOL, "&7模式: &r白名单", "", "&e> 单击切换至黑名单"));
            menu.addMenuClickHandler(15, (p, slot, item, action) -> {
                StorageCacheUtils.setData(b.getLocation(), FILTER_TYPE, "blacklist");
                updateBlockMenu(menu, b);
                return false;
            });
        } else {
            menu.replaceExistingItem(15, new CustomItemStack(Material.BLACK_WOOL, "&7模式: &8黑名单", "", "&e> 单击切换至白名单"));
            menu.addMenuClickHandler(15, (p, slot, item, action) -> {
                StorageCacheUtils.setData(b.getLocation(), FILTER_TYPE, "whitelist");
                updateBlockMenu(menu, b);
                return false;
            });
        }

        String lore = blockData.getData(FILTER_LORE);

        if (lore == null || lore.equals(String.valueOf(true))) {
            menu.replaceExistingItem(
                    25, new CustomItemStack(Material.MAP, "&7匹配在物品名称底下的文字: &2\u2714", "", "&e> 单击启用匹配文字"));
            menu.addMenuClickHandler(25, (p, slot, item, action) -> {
                StorageCacheUtils.setData(b.getLocation(), FILTER_LORE, String.valueOf(false));
                updateBlockMenu(menu, b);
                return false;
            });
        } else {
            menu.replaceExistingItem(
                    25, new CustomItemStack(Material.MAP, "&7匹配在物品名称底下的文字: &4\u2718", "", "&e> 单击关闭匹配文字"));
            menu.addMenuClickHandler(25, (p, slot, item, action) -> {
                StorageCacheUtils.setData(b.getLocation(), FILTER_LORE, String.valueOf(true));
                updateBlockMenu(menu, b);
                return false;
            });
        }

        addChannelSelector(b, menu, 41, 42, 43);
        markDirty(loc);
    }

    @Override
    protected void markDirty(Location loc) {
        CargoNet network = CargoNet.getNetworkFromLocation(loc);

        if (network != null) {
            network.markCargoNodeConfigurationDirty(loc);
        }
    }
}
