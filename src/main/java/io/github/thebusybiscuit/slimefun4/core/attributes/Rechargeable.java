package io.github.thebusybiscuit.slimefun4.core.attributes;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNet;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.Jetpack;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.MultiTool;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ChargingBench;
import io.github.thebusybiscuit.slimefun4.utils.ChargeUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A {@link Rechargeable} {@link SlimefunItem} can hold energy and is able to
 * be recharged using a {@link ChargingBench}.
 * Any {@link SlimefunItem} which is supposed to be chargeable <b>must</b> implement this interface.
 *
 * @author TheBusyBiscuit
 * @see ChargingBench
 * @see EnergyNet
 * @see Jetpack
 * @see MultiTool
 */
public interface Rechargeable extends ItemAttribute {
    /**
     * This method returns the maximum charge the given {@link ItemStack} is capable of holding.
     *
     * @param item The {@link ItemStack} for which to determine the maximum charge
     * @return The maximum energy charge for this {@link ItemStack}
     */
    float getMaxItemCharge(ItemStack item);

    /**
     * This method sets the stored energy charge for a given {@link ItemStack}.
     * The charge must be at least zero and at most {@link #getMaxItemCharge(ItemStack)}.
     *
     * @param item   The {@link ItemStack} to charge
     * @param charge The amount of charge to store
     */
    default void setItemCharge(ItemStack item, float charge) {
        float maximum = getMaxItemCharge(item);

        ItemMeta meta = item.getItemMeta();
        ChargeUtils.setCharge(meta, charge, maximum);
        item.setItemMeta(meta);
    }

    /**
     * This method returns the currently stored energy charge on the provided {@link ItemStack}.
     *
     * @param item The {@link ItemStack} to get the charge from
     * @return The charge stored on this {@link ItemStack}
     */
    default float getItemCharge(ItemStack item) {
        return ChargeUtils.getCharge(item.getItemMeta());
    }

    default float getDistanceToMaxCharge(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        float currentCharge = ChargeUtils.getCharge(meta);
        float maximum = getMaxItemCharge(item);

        return maximum - currentCharge;
    }

    /**
     * This method adds the given charge to the provided {@link ItemStack}.
     * The method will also return whether this operation was successful.
     * If the {@link ItemStack} is already at maximum charge, the method will return <code>false</code>.
     *
     * @param item   The {@link ItemStack} to charge
     * @param charge The amount of charge to add
     * @return Whether the given charge could be added successfully
     */
    default boolean addItemCharge(ItemStack item, float charge) {
        ItemMeta meta = item.getItemMeta();
        float currentCharge = ChargeUtils.getCharge(meta);
        float maximum = getMaxItemCharge(item);

        if (currentCharge >= maximum) {
            return false;
        }

        float newCharge = Math.min(currentCharge + charge, maximum);
        ChargeUtils.setCharge(meta, newCharge, maximum);

        item.setItemMeta(meta);
        return true;
    }

    default boolean addItemCharge(ItemStack item, ItemMeta meta, float charge) {
        float currentCharge = ChargeUtils.getCharge(meta);
        float maximum = getMaxItemCharge(item);

        if (currentCharge >= maximum) {
            return false;
        }

        float newCharge = Math.min(currentCharge + charge, maximum);
        ChargeUtils.setCharge(meta, newCharge, maximum);

        item.setItemMeta(meta);
        return true;
    }

    /**
     * This method removes the given charge to the provided {@link ItemStack}.
     * The method will also return whether this operation was successful.
     * If the {@link ItemStack} does not have enough charge, the method will return <code>false</code>.
     *
     * @param item   The {@link ItemStack} to remove the charge from
     * @param charge The amount of charge to remove
     * @return Whether the given charge could be removed successfully
     */
    default boolean removeItemCharge(ItemStack item, float charge) {
        ItemMeta meta = item.getItemMeta();
        float currentCharge = ChargeUtils.getCharge(meta);

        // If the item does not have enough charge, we abort
        if (currentCharge < charge) {
            return false;
        }

        float newCharge = Math.max(currentCharge - charge, 0);
        ChargeUtils.setCharge(meta, newCharge, getMaxItemCharge(item));

        item.setItemMeta(meta);
        return true;
    }

    default boolean removeItemCharge(ItemStack item, ItemMeta meta, float charge) {
        float currentCharge = ChargeUtils.getCharge(meta);

        // If the item does not have enough charge, we abort
        if (currentCharge < charge) {
            return false;
        }

        float newCharge = Math.max(currentCharge - charge, 0);
        ChargeUtils.setCharge(meta, newCharge, getMaxItemCharge(item));

        item.setItemMeta(meta);
        return true;
    }
}
