package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.PiglinBarterDrop;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.runes.VillagerRune;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * This {@link SlimefunItem} can only be obtained via bartering with a {@link Piglin}, its
 * only current uses is the recipe for crafting the {@link VillagerRune}.
 *
 * @author dNiym
 *
 * @see VillagerRune
 * @see PiglinBarterDrop
 *
 */
public class StrangeNetherGoo extends SimpleSlimefunItem<ItemUseHandler> implements PiglinBarterDrop {
    private final ItemSetting<Integer> chance = new IntRangeSetting(this, "barter-chance", 0, 7, 100);

    public StrangeNetherGoo(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(chance);
        addItemHandler(onRightClickEntity());
    }

    @Override
    public int getBarteringLootChance() {
        return chance.getValue();
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> block = e.getClickedBlock();

            if (block.isPresent() && Tag.SIGNS.isTagged(block.get().getType())) {
                e.cancel();
            }
        };
    }

    private static EntityInteractHandler onRightClickEntity() {
        return (e, item, hand) -> {
            if (e.getRightClicked() instanceof Sheep sheep) {
                if (sheep.getCustomName() != null) {
                    e.setCancelled(true);
                    return;
                }

                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    ItemUtils.consumeItem(item, false);
                }

                // Give Sheep color, name and effect
                sheep.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 2));
                sheep.setColor(DyeColor.PURPLE);
                sheep.setCustomName(ChatColor.DARK_PURPLE + "受污染的羊");
                e.setCancelled(true);
            }
        };
    }
}
