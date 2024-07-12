package me.qscbm.slimefun4.slimefunitems.tools;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.qscbm.slimefun4.slimefunitems.machines.ASpeedableContainer;
import me.qscbm.slimefun4.slimefunitems.machines.Speedable;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

public class OverclockModule extends SimpleSlimefunItem<ItemUseHandler> {
    @ParametersAreNonnullByDefault
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

                if (item instanceof Speedable machine) {
                    if (!machine.speedUp(blockData)) {
                        e.getPlayer().sendMessage("超频倍率已达上限: " + machine.getSpeedLimit() + "x");
                    } else {
                        e.getPlayer().sendMessage("超频机器成功, 目前倍率: " + machine.getIncreasedSpeed(blockData) + "x");
                    }
                } else {
                    e.getPlayer().sendMessage("该机器无法超频");
                }
            }
        };
    }
}

