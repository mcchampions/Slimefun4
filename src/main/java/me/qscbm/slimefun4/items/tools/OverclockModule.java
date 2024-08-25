package me.qscbm.slimefun4.items.tools;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.qscbm.slimefun4.items.machines.Speedable;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class OverclockModule extends SimpleSlimefunItem<ItemUseHandler> {
    public OverclockModule(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> blockOptional = e.getClickedBlock();
            Optional<SlimefunItem> sfBlock = e.getSlimefunBlock();
            e.cancel();

            if (sfBlock.isPresent() && blockOptional.isPresent()) {
                Block block = blockOptional.get();
                SlimefunBlockData blockData = Slimefun.getDatabaseManager()
                        .getBlockDataController().getBlockData(block.getLocation());
                SlimefunItem item = sfBlock.get();
                Player player = e.getPlayer();
                if (item instanceof Speedable machine) {
                    if (!machine.speedUp(blockData)) {
                        player.sendMessage("超频倍率已达上限: " + machine.getSpeedLimit() + "x");
                    } else {
                        player.sendMessage("超频机器成功, 目前倍率: " + machine.getIncreasedSpeed(blockData) + "x");
                        e.getItem().setAmount(e.getItem().getAmount() - 1);
                    }
                } else {
                    player.sendMessage("该机器无法超频");
                }
            }
        };
    }
}

