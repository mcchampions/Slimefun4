package io.github.thebusybiscuit.slimefun4.implementation.settings;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.talismans.MagicianTalisman;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.TalismanListener;
import javax.annotation.ParametersAreNonnullByDefault;

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
public class TalismanEnchantment extends ItemSetting<Boolean> {

    private final Enchantment enchantment;
    /**
     * -- GETTER --
     *  This returns the level for this
     * .
     *
     */
    @Getter
    private final int level;

    @ParametersAreNonnullByDefault
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

    /**
     * This returns the actual {@link Enchantment} represented by this {@link ItemSetting}.
     *
     * @return The associated {@link Enchantment}
     */
    
    public Enchantment getEnchantment() {
        return enchantment;
    }

}
