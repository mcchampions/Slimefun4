package io.github.thebusybiscuit.slimefun4.implementation.handlers;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * This is an implementation of {@link BlockBreakHandler} which is suited for any {@link SlimefunItem}
 * that uses the vanilla {@link Inventory} from the {@link BlockState}.
 * <p>
 * The default behaviour is the following:
 *
 * <pre>
 * | Broken by... | Behaviour                |
 * | ------------ | ------------------------ |
 * | Player       | Drop inventory contents. |
 * | Android      | Not allowed.             |
 * | Explosions   | Delete contents.         |
 * </pre>
 *
 * @author TheBusyBiscuit
 *
 * @param <T>
 *            The type of {@link BlockState} and {@link InventoryHolder} we are dealing with
 */
public class VanillaInventoryDropHandler<T extends BlockState & InventoryHolder> extends BlockBreakHandler {
    private final Class<T> blockStateClass;

    /**
     * This creates a new {@link VanillaInventoryDropHandler} for the given {@link BlockState} {@link Class}.
     *
     * @param blockStateClass
     *            The class of the block's {@link BlockState}
     */
    public VanillaInventoryDropHandler(Class<T> blockStateClass) {
        super(false, true);

        this.blockStateClass = blockStateClass;
    }

    @Override
    public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
        Block b = e.getBlock();
        BlockState state = b.getState(false);

        if (blockStateClass.isInstance(state)) {
            T inventoryHolder = blockStateClass.cast(state);

            for (ItemStack stack : getInventory(inventoryHolder)) {
                if (stack != null && !stack.getType().isAir()) {
                    drops.add(stack);
                }
            }
        }
    }

    protected Inventory getInventory(T inventoryHolder) {
        if (inventoryHolder instanceof Chest chest) {
            return chest.getBlockInventory();
        } else {
            return inventoryHolder.getInventory();
        }
    }
}
