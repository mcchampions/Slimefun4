package io.github.thebusybiscuit.slimefun4.implementation.items.magical.staves;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link WindStaff} is a powerful staff which launches the {@link Player} forward when right clicked.
 *
 * @author TheBusyBiscuit
 *
 */
public class WindStaff extends SimpleSlimefunItem<ItemUseHandler> {
    private final ItemSetting<Integer> multiplier = new IntRangeSetting(this, "power", 1, 4, Integer.MAX_VALUE);

    public WindStaff(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(multiplier);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();

            if (p.getFoodLevel() >= 2) {
                // The isItem() check is here to prevent the MultiTool from consuming hunger
                if (isItem(e.getItem()) && p.getGameMode() != GameMode.CREATIVE) {
                    FoodLevelChangeEvent event = new FoodLevelChangeEvent(p, p.getFoodLevel() - 2);
                    Bukkit.getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        p.setFoodLevel(event.getFoodLevel());
                    }
                }

                p.setVelocity(p.getEyeLocation().getDirection().multiply(multiplier.getValue()));
                SoundEffect.WIND_STAFF_USE_SOUND.playFor(p);
                p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 1);
                p.setFallDistance(0.0F);
            } else {
                Slimefun.getLocalization().sendMessage(p, "messages.hungry", true);
            }
        };
    }
}
