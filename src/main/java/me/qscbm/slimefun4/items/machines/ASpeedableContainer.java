package me.qscbm.slimefun4.items.machines;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import lombok.Getter;
import lombok.Setter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * 可提速的机器
 */
@Setter
@Getter
public abstract class ASpeedableContainer extends AContainer implements Speedable {
    private int speedLimit = 10;

    protected ASpeedableContainer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    protected ASpeedableContainer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {
            @Override
            public void tick(Block b, SlimefunItem sf, SlimefunBlockData data) {
                ASpeedableContainer.this.tick(b, data);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b, SlimefunBlockData data) {
        BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());
        CraftingOperation currentOperation = getMachineProcessor().getOperation(b);

        if (currentOperation != null) {
            if (!takeCharge(b.getLocation())) {
                return;
            }
            if (!currentOperation.isFinished()) {
                getMachineProcessor().updateProgressBar(inv, 22, currentOperation);
                currentOperation.addProgress(1);
                return;
            }
            inv.replaceExistingItem(22, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));

            for (ItemStack output : currentOperation.getResults()) {
                inv.pushItem(output.clone(), getOutputSlots());
            }

            getMachineProcessor().endOperation(b);
        } else {
            MachineRecipe recipe = findNextRecipe(inv);
            if (recipe != null) {
                MachineRecipe next = recipe.clone();
                int speed = getIncreasedSpeed(data);
                next.setTicks(next.getTicks() / speed);
                currentOperation = new CraftingOperation(next);
                getMachineProcessor().startOperation(b, currentOperation);

                // Fixes #3534 - Update indicator immediately
                getMachineProcessor().updateProgressBar(inv, 22, currentOperation);
            }
        }
    }

    @Override
    protected boolean takeCharge(Location l) {
        return takeCharge(StorageCacheUtils.getBlock(l));
    }

    public int getEnergyConsumption(SlimefunBlockData data) {
        return getEnergyConsumption() * getIncreasedSpeed(data);
    }

    protected boolean takeCharge(SlimefunBlockData data) {
        if (isChargeable()) {
            Location l = data.getLocation();
            int charge = getCharge(l);

            if (charge < getEnergyConsumption(data)) {
                return false;
            }

            setCharge(l, charge - getEnergyConsumption());
        }
        return true;
    }

    @Override
    @Deprecated
    protected void tick(Block block) {
    }
}
