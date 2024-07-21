package io.github.thebusybiscuit.slimefun4.implementation.items.magical.talismans;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.settings.TalismanEnchantment;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * The {@link MagicianTalisman} is a special kind of {@link Talisman} which awards a {@link Player}
 * with an extra {@link Enchantment} when they enchant their {@link ItemStack}.
 *
 * @author TheBusyBiscuit
 *
 */
public class MagicianTalisman extends Talisman {
    private final ItemSetting<Boolean> allowEnchantmentBooks =
            new ItemSetting<>(this, "allow-enchantment-books", false);
    private final Set<TalismanEnchantment> enchantments = new HashSet<>();

    public MagicianTalisman(SlimefunItemStack item, ItemStack[] recipe) {
        super(item, recipe, false, false, "magician", 80);

        addItemSetting(allowEnchantmentBooks);

        for (Enchantment enchantment : Enchantment.values()) {
            try {
                for (int i = 1; i <= enchantment.getMaxLevel(); i++) {
                    enchantments.add(new TalismanEnchantment(this, enchantment, i));
                }
            } catch (Exception x) {
                Slimefun.logger()
                        .log(
                                Level.SEVERE,
                                x,
                                () -> "The following Exception occurred while trying to register the following"
                                        + " Enchantment: "
                                        + enchantment);
            }
        }

        try {
            if (!enchantments.isEmpty()) {
                // Fixes #3007 - This is a Set, so every Enchantment should only be contained in here once.
                addItemSetting(enchantments.toArray(new ItemSetting[0]));
            }
        } catch (Exception x) {
            Slimefun.logger()
                    .log(
                            Level.SEVERE,
                            x,
                            () -> "The following Exception was thrown when initializing the settings for "
                                  + this);
        }
    }

    /**
     * This method picks a random {@link TalismanEnchantment} for the provided {@link ItemStack}.
     * The method will return null, if null was provided or no applicable {@link Enchantment} was found.
     *
     * @param item
     *            The {@link ItemStack} to find an {@link Enchantment} for
     * @param existingEnchantments
     *            A {@link Set} containing the {@link Enchantment Enchantments} that currently exist on the
     *            {@link ItemStack}
     *
     * @return An applicable {@link TalismanEnchantment} or null
     */
    @Nullable public TalismanEnchantment getRandomEnchantment(
            ItemStack item, Set<Enchantment> existingEnchantments) {

        List<TalismanEnchantment> enabled = enchantments.stream()
                .filter(e -> (isEnchantmentBookAllowed() && item.getType() == Material.BOOK)
                        || e.getEnchantment().canEnchantItem(item))
                .filter(e -> hasConflicts(existingEnchantments, e))
                .filter(TalismanEnchantment::getValue)
                .toList();


        return enabled.isEmpty()
                ? null
                : enabled.get(ThreadLocalRandom.current().nextInt(enabled.size()));
    }

    private boolean hasConflicts(Set<Enchantment> enchantments, TalismanEnchantment ench) {
        for (Enchantment existing : enchantments) {
            if (existing.conflictsWith(ench.getEnchantment())) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method checks whether enchantment books
     * can be given an extra {@link Enchantment} or not.
     *
     * @return Whether enchantment books can receive an extra {@link Enchantment}
     */
    public boolean isEnchantmentBookAllowed() {
        return allowEnchantmentBooks.getValue();
    }
}
