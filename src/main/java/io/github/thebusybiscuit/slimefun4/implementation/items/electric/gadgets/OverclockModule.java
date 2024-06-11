package io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.apache.commons.lang.Validate;
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
            Optional<Block> block = e.getClickedBlock();
            Optional<SlimefunItem> sfBlock = e.getSlimefunBlock();
            Optional<SlimefunItem> sfItem = e.getSlimefunItem();
            e.cancel();

            if (sfBlock.isPresent() && block.isPresent()) {
                SlimefunItem item = sfBlock.get();

                if (item instanceof AContainer machine) {
                    AContainer defaultItem = (AContainer) SlimefunItem.getById(item.getId());

                    Validate.notNull(defaultItem, "超频模块获取默认机器参数失败");

                    int multiply = machine.getSpeed() - defaultItem.getSpeed();

                    if (multiply >= 10) {
                        e.getPlayer().sendMessage("超频倍率已达上限: 10x");
                    } else {
                        machine.getDrops().add(sfItem.get().getItem());
                        machine.setProcessingSpeed(machine.getSpeed() + 1);
                        e.getPlayer().sendMessage("超频机器成功, 目前倍率: " + machine.getSpeed() + "x");
                    }
                } else {
                    e.getPlayer().sendMessage("该机器无法超频");
                }
            }
        };
    }
}