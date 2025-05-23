package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.core.services.LocalizationService;
import io.github.thebusybiscuit.slimefun4.core.services.localization.SlimefunLocalization;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link FortuneCookie} is a rather simple {@link SlimefunItem}, it's a cookie which
 * sends the {@link Player} who ate it a random text message.
 * The messages can be defined in the {@link LocalizationService}.
 *
 * @author TheBusyBiscuit
 *
 */
public class FortuneCookie extends SimpleSlimefunItem<ItemConsumptionHandler> {
    public FortuneCookie(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> {
            List<String> messages = SlimefunLocalization.getMessages(p, "messages.fortune-cookie");
            String message = messages.get(ThreadLocalRandom.current().nextInt(messages.size()));

            p.sendMessage(ChatColors.color(message));
        };
    }
}
