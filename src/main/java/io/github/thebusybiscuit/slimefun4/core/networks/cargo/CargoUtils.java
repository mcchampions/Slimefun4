package io.github.thebusybiscuit.slimefun4.core.networks.cargo;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.xzavier0722.mc.plugin.slimefuncomplib.event.cargo.CargoInsertEvent;
import com.xzavier0722.mc.plugin.slimefuncomplib.event.cargo.CargoWithdrawEvent;
import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.*;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * This is a helper class for the {@link CargoNet} which provides
 * a free static utility methods to let the {@link CargoNet} interact with
 * an {@link Inventory} or {@link BlockMenu}.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 * @author DNx5
 */
final class CargoUtils {
    /**
     * These are the slots where our filter items sit.
     */
    private static final int[] FILTER_SLOTS = {19, 20, 21, 28, 29, 30, 37, 38, 39};

    /**
     * This is a utility class and should not be instantiated.
     * Therefore we just hide the public constructor.
     */
    private CargoUtils() {
    }

    /**
     * This is a performance-saving shortcut to quickly test whether a given
     * {@link Block} might be an {@link InventoryHolder} or not.
     *
     * @param block The {@link Block} to check
     * @return Whether this {@link Block} represents a {@link BlockState} that is an {@link InventoryHolder}
     */
    static boolean hasInventory(@Nullable Block block) {
        if (block == null) {
            // No block, no inventory
            return false;
        }

        Material type = block.getType();
        return SlimefunTag.CARGO_SUPPORTED_STORAGE_BLOCKS.isTagged(type);
    }

    static int[] getInputSlotRange(Inventory inv, @Nullable ItemStack item) {
        if (inv instanceof FurnaceInventory) {
            if (item != null && item.getType().isFuel()) {
                if (isSmeltable(item)) {
                    // Any non-smeltable items should not land in the upper slot
                    return new int[]{0, 2};
                } else {
                    return new int[]{1, 2};
                }
            } else {
                return new int[]{0, 1};
            }
        } else if (inv instanceof BrewerInventory) {
            if (isPotion(item)) {
                // Slots for potions
                return new int[]{0, 3};
            } else if (item != null && item.getType() == Material.BLAZE_POWDER) {
                // Blaze Powder slot
                return new int[]{4, 5};
            } else {
                // Input slot
                return new int[]{3, 4};
            }
        } else {
            // Slot 0-size
            return new int[]{0, inv.getSize()};
        }
    }

    static int[] getOutputSlotRange(Inventory inv) {
        if (inv instanceof FurnaceInventory) {
            // Slot 2-3
            return new int[]{2, 3};
        } else if (inv instanceof BrewerInventory) {
            // Slot 0-3
            return new int[]{0, 3};
        } else {
            // Slot 0-size
            return new int[]{0, inv.getSize()};
        }
    }

    @Nullable
    static ItemStackAndInteger withdraw(
            AbstractItemNetwork network, Map<Location, Inventory> inventories, Block node, Block target) {
        DirtyChestMenu menu = getChestMenu(target);

        if (menu != null) {
            CargoWithdrawEvent event = new CargoWithdrawEvent(node, target, menu.toInventory());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return null;
            }
            BlockMenuPreset preset = menu.getPreset();
            try {
                for (int slot : preset.getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, null)) {
                    ItemStack is = menu.getItemInSlot(slot);

                    if (matchesFilter(network, node, is)) {
                        menu.replaceExistingItem(slot, null);
                        return new ItemStackAndInteger(is, slot);
                    }
                }
            } catch (NullPointerException ex) {
                SlimefunItem sfItem = preset.getSlimefunItem();
                int[] slots;
                if (sfItem instanceof InventoryBlock block) {
                    slots = block.getOutputSlots();
                } else {
                    Method method = ReflectionUtils.getMethod(sfItem.getClass(), "getOutputSlots");
                    if (method != null) {
                        try {
                            method.setAccessible(true);
                            slots = (int[]) method.invoke(sfItem);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return null;
                    }
                }
                for (int slot : slots) {
                    ItemStack is = menu.getItemInSlot(slot);

                    if (matchesFilter(network, node, is)) {
                        menu.replaceExistingItem(slot, null);
                        return new ItemStackAndInteger(is, slot);
                    }
                }
            }
        } else if (hasInventory(target)) {
            Inventory inventory = inventories.get(target.getLocation());

            if (inventory == null) {
                BlockState state = target.getState(false);
                if (!(state instanceof InventoryHolder holder)) {
                    return null;
                }

                inventory = holder.getInventory();
                inventories.put(target.getLocation(), inventory);
            }
            CargoWithdrawEvent event = new CargoWithdrawEvent(node, target, inventory);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                return withdrawFromVanillaInventory(network, node, inventory);
            }
        }

        return null;
    }

    @Nullable
    private static ItemStackAndInteger withdrawFromVanillaInventory(
            AbstractItemNetwork network, Block node, Inventory inv) {
        ItemStack[] contents = inv.getContents();
        int[] range = getOutputSlotRange(inv);
        int minSlot = range[0];
        int maxSlot = range[1];

        for (int slot = minSlot; slot < maxSlot; slot++) {
            ItemStack item = contents[slot];

            if (matchesFilter(network, node, item)) {
                inv.setItem(slot, null);
                return new ItemStackAndInteger(item, slot);
            }
        }

        return null;
    }

    @Nullable
    static ItemStack insert(
            AbstractItemNetwork network,
            Map<Location, Inventory> inventories,
            Block node,
            Block target,
            boolean smartFill,
            ItemStack stack,
            ItemStackWrapper wrapper) {
        if (!matchesFilter(network, node, stack)) {
            return stack;
        }

        DirtyChestMenu menu = getChestMenu(target);

        if (menu == null) {
            if (hasInventory(target)) {
                Inventory inventory = inventories.get(target.getLocation());

                if (inventory == null) {
                    BlockState state = target.getState(false);
                    if (!(state instanceof InventoryHolder holder)) {
                        return stack;
                    }
                    inventory = holder.getInventory();
                    inventories.put(target.getLocation(), inventory);
                }
                CargoInsertEvent event = new CargoInsertEvent(node, target, inventory);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    return insertIntoVanillaInventory(stack, wrapper, smartFill, inventory);
                }
            }

            return stack;
        }

        CargoInsertEvent event = new CargoInsertEvent(node, target, menu.toInventory());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return stack;
        }

        for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, wrapper)) {
            ItemStack itemInSlot = menu.getItemInSlot(slot);

            if (itemInSlot == null) {
                menu.replaceExistingItem(slot, stack);
                return null;
            }

            int maxStackSize = itemInSlot.getType().getMaxStackSize();
            int currentAmount = itemInSlot.getAmount();

            if (!smartFill && currentAmount == maxStackSize) {
                // Skip full stacks - Performance optimization for non-smartfill nodes
                continue;
            }

            if (SlimefunUtils.isItemSimilar(itemInSlot, wrapper, true, false)) {
                if (currentAmount < maxStackSize) {
                    int amount = currentAmount + stack.getAmount();

                    itemInSlot.setAmount(Math.min(amount, maxStackSize));
                    if (amount > maxStackSize) {
                        stack.setAmount(amount - maxStackSize);
                    } else {
                        stack = null;
                    }

                    menu.replaceExistingItem(slot, itemInSlot);
                    return stack;
                } else if (smartFill) {
                    return stack;
                }
            }
        }

        return stack;
    }

    @Nullable
    private static ItemStack insertIntoVanillaInventory(
            ItemStack stack, ItemStackWrapper wrapper, boolean smartFill, Inventory inv) {
        /*
         * If the Inventory does not accept this Item Type, bounce the item back.
         * Example: Shulker boxes within shulker boxes (fixes #2662)
         */
        if (!InvUtils.isItemAllowed(stack.getType(), inv.getType())) {
            return stack;
        }

        ItemStack[] contents = inv.getContents();
        int[] range = getInputSlotRange(inv, stack);
        int minSlot = range[0];
        int maxSlot = range[1];

        for (int slot = minSlot; slot < maxSlot; slot++) {
            // Changes to this ItemStack are synchronized with the Item in the Inventory
            ItemStack itemInSlot = contents[slot];

            if (itemInSlot == null) {
                inv.setItem(slot, stack);
                return null;
            } else {
                int currentAmount = itemInSlot.getAmount();
                int maxStackSize = itemInSlot.getType().getMaxStackSize();

                if (!smartFill && currentAmount == maxStackSize) {
                    // Skip full stacks - Performance optimization for non-smartfill nodes
                    continue;
                }

                if (SlimefunUtils.isItemSimilar(itemInSlot, wrapper, true, false)) {
                    if (currentAmount < maxStackSize) {
                        int amount = currentAmount + stack.getAmount();

                        if (amount > maxStackSize) {
                            stack.setAmount(amount - maxStackSize);
                            itemInSlot.setAmount(maxStackSize);
                            return stack;
                        } else {
                            itemInSlot.setAmount(amount);
                            return null;
                        }
                    } else if (smartFill) {
                        return stack;
                    }
                }
            }
        }

        return stack;
    }

    @Nullable
    static DirtyChestMenu getChestMenu(Block block) {
        return StorageCacheUtils.getMenu(block.getLocation());
    }

    static boolean matchesFilter(AbstractItemNetwork network, Block node, @Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        return network.getItemFilter(node).test(item);
    }

    /**
     * This method checks if a given {@link ItemStack} is smeltable or not.
     * The lazy-option is a performance-saver since actually calculating this can be quite expensive.
     * For the current applicational purposes a quick check for any wooden logs is sufficient.
     * Otherwise the "lazyness" can be turned off in the future.
     *
     * @param stack The {@link ItemStack} to test
     * @return Whether the given {@link ItemStack} can be smelted or not
     */
    private static boolean isSmeltable(@Nullable ItemStack stack) {
        return stack != null && Tag.LOGS.isTagged(stack.getType());
    }

    private static boolean isPotion(@Nullable ItemStack item) {
        if (item != null) {
            Material type = item.getType();
            return type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION;
        } else {
            return false;
        }
    }

    /**
     * Gets the {@link ItemFilter} slots for a Cargo Node. If you wish to access the items
     * in the cargo (without hardcoding the slots in case of change) then you can use this method.
     *
     * @return The slots where the {@link ItemFilter} section for a cargo node sits
     */

    public static int[] getFilteringSlots() {
        return FILTER_SLOTS;
    }
}
