package io.github.thebusybiscuit.slimefun4.implementation.settings;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.talismans.MagicianTalisman;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.TalismanListener;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;

/**
 * This class is an extension of {@link ItemSetting} that holds an {@link Enchantment} and
 * a level. It is only used by the {@link TalismanListener} to handle the {@link MagicianTalisman}.
 *
 * @author TheBusyBiscuit
 *
 * @see MagicianTalisman
 */
@Getter
public class TalismanEnchantment extends ItemSetting<Boolean> {
    /**
     * -- GETTER --
     *  This returns the actual
     *  represented by this
     * .
     */
    private final Enchantment enchantment;
    /**
     * -- GETTER --
     *  This returns the level for this
     * .
     *
     */
    private final int level;

    public TalismanEnchantment(MagicianTalisman talisman, Enchantment enchantment, int level) {
        super(
                talisman,
                "allow-enchantments."
                        + enchantment.getKey().getNamespace()
                        + '.'
                        + enchantment.getKey().getKey()
                        + ".level."
                        + level,
                true);

        this.enchantment = enchantment;
        this.level = level;
    }

}
