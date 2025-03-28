package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.core.attributes.rotations.NotRotatable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.qscbm.slimefun4.handlers.CargoTicker;
import me.qscbm.slimefun4.tasks.BaseTickerTask;
import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CargoManager extends SlimefunItem implements HologramOwner, NotRotatable {
    public CargoManager(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemHandler(onBreak());
    }

    private BlockBreakHandler onBreak() {
        return new SimpleBlockBreakHandler() {
            @Override
            public void onBlockBreak(Block b) {
                removeHologram(b);
            }
        };
    }

    public static CargoTicker getTicker() {
        return new CargoTicker() {
            @Override
            public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
                CargoNet.getNetworkFromLocationOrCreate(b.getLocation()).tick(b, data);
            }
        };
    }

    @Override
    public void preRegister() {
        addItemHandler(
                new BlockTicker() {
                    @Override
                    public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
                        Slimefun.getTickerTask().disableTicker(b.getLocation());
                    }

                    @Override
                    public boolean isSynchronized() {
                        return false;
                    }
                },
                (BlockUseHandler) e -> {
                    Optional<Block> block = e.getClickedBlock();

                    if (block.isPresent()) {
                        Player p = e.getPlayer();
                        Block b = block.get();

                        SlimefunBlockData blockData = StorageCacheUtils.getBlock(b.getLocation());
                        if (blockData.getData("visualizer") == null) {
                            blockData.setData("visualizer", "disabled");
                            p.sendMessage(TextUtils.fromText('&', "&c货运网络可视化: " + "§4\u2718"));
                        } else {
                            blockData.removeData("visualizer");
                            p.sendMessage(TextUtils.fromText('&', "&c货运网络可视化: " + "&2\u2714"));
                        }
                    }
                });
    }

    @Override
    public BaseTickerTask getTickerTask() {
        return Slimefun.instance().getCargoTickerTask();
    }
}
