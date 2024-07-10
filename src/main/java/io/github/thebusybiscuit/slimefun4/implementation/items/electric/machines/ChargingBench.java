package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.qscbm.slimefun4.slimefunitems.machines.ASpeedableContainer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link ChargingBench} is a powered machine that can be used to charge any {@link Rechargeable} item.
 *
 * @author TheBusyBiscuit
 * @see Rechargeable
 */
public class ChargingBench extends ASpeedableContainer {

    public ChargingBench(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.GOLDEN_PICKAXE);
    }

    @Override
    protected void tick(Block b, SlimefunBlockData data) {
        if (getCharge(b.getLocation()) < getEnergyConsumption(data)) {
            return;
        }
        BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());

        int count = getIncreasedSpeed(data);
        for (int i = 0; i < count; i++) {
            for (int slot : getInputSlots()) {
                ItemStack item = inv.getItemInSlot(slot);
                if (item != null) {
                    if (charge(inv, slot, item, data)) {
                        break;
                    }
                }
            }
        }
    }

    private boolean charge(BlockMenu inv, int slot, ItemStack item, SlimefunBlockData data) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem instanceof Rechargeable rechargeable) {
            float charge = getEnergyConsumption(data) / 2F / item.getAmount();

            if (rechargeable.addItemCharge(item, charge)) {
                takeCharge(data);
            } else if (inv.fits(item, getOutputSlots())) {
                inv.pushSlimefunItem(item, getOutputSlots());
                inv.replaceExistingItem(slot, null);
            }

            return true;
        } else if (sfItem != null && inv.fits(item, getOutputSlots())) {
            inv.pushSlimefunItem(item, getOutputSlots());
            inv.replaceExistingItem(slot, null);
        }
        return false;
    }

    @Override
    public String getMachineIdentifier() {
        return "CHARGING_BENCH";
    }
}
