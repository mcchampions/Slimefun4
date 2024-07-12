package io.github.thebusybiscuit.slimefun4.api.items.groups;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import java.time.LocalDate;
import java.time.Month;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a {@link ItemGroup} that is only displayed in the Guide during
 * a specified {@link Month}.
 *
 * @author TheBusyBiscuit
 *
 * @see ItemGroup
 * @see LockedItemGroup
 */
@Getter
public class SeasonalItemGroup extends ItemGroup {
    /**
     * -- GETTER --
     *  This method returns the
     *  in which this
     *  will appear.
     *
     */
    private final Month month;

    /**
     * The constructor for a {@link SeasonalItemGroup}.
     *
     * @param key
     *            The {@link NamespacedKey} that is used to identify this {@link ItemGroup}
     * @param month
     *            The month when the {@link ItemGroup} should be displayed (from 1 = January ; to 12 = December)
     * @param tier
     *            The tier of this {@link ItemGroup}
     * @param item
     *            The display item for this {@link ItemGroup}
     */
    @ParametersAreNonnullByDefault
    public SeasonalItemGroup(NamespacedKey key, Month month, int tier, ItemStack item) {
        super(key, item, tier);

        this.month = month;
    }

    @Override
    public boolean isAccessible(Player p) {
        // Block this ItemGroup if the month differs
        if (month != LocalDate.now().getMonth()) {
            return false;
        }

        return super.isAccessible(p);
    }
}
