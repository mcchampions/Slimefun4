package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

import java.util.List;
import java.util.UUID;

import me.qscbm.slimefun4.message.QsTextComponentImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The {@link KnowledgeTome} allows you to copy every unlocked {@link Research}
 * from one {@link Player} to another.
 *
 * @author TheBusyBiscuit
 */
public class KnowledgeTome extends SimpleSlimefunItem<ItemUseHandler> {
    public KnowledgeTome(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            ItemStack item = e.getItem();

            e.setUseBlock(Result.DENY);

            ItemMeta im = item.getItemMeta();
            List<Component> lore = im.lore();

            Component fc = lore.get(0);

            if (!(fc instanceof TextComponent firstComponent)) return;

            List<Component> list = firstComponent.children();
            if (list.size() >= 2 && ((TextComponent) list.get(1)).content().equals("None")) {
                lore.set(0, new QsTextComponentImpl("主人: ").color(NamedTextColor.GRAY)
                        .append(new QsTextComponentImpl(p.getName()).color(NamedTextColor.GRAY)));
                lore.set(1, new QsTextComponentImpl(p.getUniqueId().toString()).color(NamedTextColor.BLACK));
                im.lore(lore);
                item.setItemMeta(im);
                SoundEffect.TOME_OF_KNOWLEDGE_USE_SOUND.playFor(p);
            } else {
                UUID uuid;
                try {
                    uuid = UUID.fromString(((TextComponent) lore.get(1)).content());
                } catch (Exception ex) {
                    return;
                }
                if (p.getUniqueId().equals(uuid)) {
                    Slimefun.getLocalization().sendMessage(p, "messages.no-tome-yourself");
                    return;
                }

                PlayerProfile.get(
                        p,
                        profile -> PlayerProfile.fromUUID(uuid, owner -> {
                            for (Research research : owner.getResearches()) {
                                research.unlock(p, true);
                            }
                        }));
                ItemUtils.consumeItem(item, false);
            }
        };
    }
}
