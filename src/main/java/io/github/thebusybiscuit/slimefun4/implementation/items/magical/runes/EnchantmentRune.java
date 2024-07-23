package io.github.thebusybiscuit.slimefun4.implementation.items.magical.runes;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemDropHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This {@link SlimefunItem} allows you to enchant any enchantable {@link ItemStack} with a random
 * {@link Enchantment}. It is also one of the very few utilisations of {@link ItemDropHandler}.
 *
 * @author Linox
 *
 * @see ItemDropHandler
 *
 */
public class EnchantmentRune extends SimpleSlimefunItem<ItemDropHandler> {
    private static final double RANGE = 1.5;
    private final Map<Material, List<Enchantment>> applicableEnchantments = new EnumMap<>(Material.class);

    public EnchantmentRune(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        for (Material mat : Material.values()) {
            List<Enchantment> enchantments = new ArrayList<>();

            for (Enchantment enchantment : Enchantment.values()) {
                if (!mat.isItem()) {
                    continue;
                }

                if (enchantment.equals(Enchantment.BINDING_CURSE) || enchantment.equals(Enchantment.VANISHING_CURSE)) {
                    continue;
                }

                if (enchantment.canEnchantItem(new ItemStack(mat))) {
                    enchantments.add(enchantment);
                }
            }

            applicableEnchantments.put(mat, enchantments);
        }
    }


    @Override
    public ItemDropHandler getItemHandler() {
        return (e, p, item) -> {
            if (isItem(item.getItemStack())) {
                if (canUse(p, true)) {
                    Slimefun.runSync(
                            () -> {
                                try {
                                    addRandomEnchantment(p, item);
                                } catch (RuntimeException x) {
                                    error("An Exception occurred while trying to apply an Enchantment Rune", x);
                                }
                            },
                            20L);
                }

                return true;
            }

            return false;
        };
    }

    private void addRandomEnchantment(Player p, Item rune) {
        // Being sure the entity is still valid and not picked up or whatsoever.
        if (!rune.isValid()) {
            return;
        }

        Location l = rune.getLocation();
        Collection<Entity> entites = l.getWorld().getNearbyEntities(l, RANGE, RANGE, RANGE, this::findCompatibleItem);
        Optional<Entity> optional = entites.stream().findFirst();

        if (optional.isPresent()) {
            Item item = (Item) optional.get();
            ItemStack itemStack = item.getItemStack();
            ItemStack runeStack = rune.getItemStack();

            List<Enchantment> potentialEnchantments = applicableEnchantments.get(itemStack.getType());

            if (potentialEnchantments == null) {
                Slimefun.getLocalization().sendMessage(p, "messages.enchantment-rune.fail", true);
                return;
            } else {
                potentialEnchantments = new ArrayList<>(potentialEnchantments);
            }

            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);

            // Fixes #2878 - Respect enchatability config setting.
            if (slimefunItem != null && !slimefunItem.isEnchantable()) {
                Slimefun.getLocalization().sendMessage(p, "messages.enchantment-rune.fail", true);
                return;
            }

            /*
             * Removing the enchantments that the item already has from enchantmentSet.
             * This also removes any conflicting enchantments
             */
            removeIllegalEnchantments(itemStack, potentialEnchantments);

            if (potentialEnchantments.isEmpty()) {
                Slimefun.getLocalization().sendMessage(p, "messages.enchantment-rune.no-enchantment", true);
                return;
            }

            Enchantment enchantment =
                    potentialEnchantments.get(ThreadLocalRandom.current().nextInt(potentialEnchantments.size()));
            int level = getRandomlevel(enchantment);

            if (itemStack.getAmount() == 1) {
                // This lightning is just an effect, it deals no damage.
                l.getWorld().strikeLightningEffect(l);

                Slimefun.runSync(
                        () -> {
                            // Being sure entities are still valid and not picked up or whatsoever.
                            if (rune.isValid() && item.isValid() && itemStack.getAmount() == 1) {
                                l.getWorld().spawnParticle(VersionedParticle.ENCHANTED_HIT, l, 1);
                                SoundEffect.ENCHANTMENT_RUNE_ADD_ENCHANT_SOUND.playAt(l, SoundCategory.PLAYERS);

                                item.remove();

                                // When multiple runes have been merged, reduce one rune.
                                if (rune.getItemStack().getAmount() > 1) {
                                    runeStack.setAmount(runeStack.getAmount() - 1);
                                    rune.setItemStack(runeStack);
                                } else {
                                    rune.remove();
                                }

                                if (enchantment.canEnchantItem(itemStack)) {
                                    itemStack.addEnchantment(enchantment, level);
                                    Slimefun.getLocalization()
                                            .sendMessage(p, "messages.enchantment-rune.success", true);
                                } else {
                                    l.getWorld().dropItemNaturally(l, runeStack);
                                    Slimefun.getLocalization().sendMessage(p, "messages.enchantment-rune.fail", true);
                                }

                                l.getWorld().dropItemNaturally(l, itemStack);
                            }
                        },
                        10L);
            } else {
                Slimefun.getLocalization().sendMessage(p, "messages.enchantment-rune.fail", true);
            }
        }
    }

    private int getRandomlevel(Enchantment enchantment) {
        int level = 1;

        if (enchantment.getMaxLevel() != 1) {
            level = ThreadLocalRandom.current().nextInt(enchantment.getMaxLevel()) + 1;
        }

        return level;
    }

    private void removeIllegalEnchantments(
            ItemStack target, List<Enchantment> potentialEnchantments) {
        for (Enchantment enchantment : target.getEnchantments().keySet()) {
            // Duplicate or conflict
            potentialEnchantments.removeIf(possibleEnchantment -> possibleEnchantment.equals(enchantment) || possibleEnchantment.conflictsWith(enchantment));
        }
    }

    private boolean findCompatibleItem(Entity n) {
        if (n instanceof Item item) {
            return !isItem(item.getItemStack());
        }

        return false;
    }
}
