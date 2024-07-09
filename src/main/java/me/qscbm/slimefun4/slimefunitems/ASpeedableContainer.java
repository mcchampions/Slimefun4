package me.qscbm.slimefun4.slimefunitems;

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
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

/**
 * 可提速的机器
 */
@Getter
public abstract class ASpeedableContainer extends AContainer {
    @Setter
    private int speedLimit = 10;

    protected ASpeedableContainer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    protected ASpeedableContainer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    public int getIncreasedSpeed(SlimefunBlockData data) {
        String speedStr = data.getData("speed");
        if (speedStr == null) {
            data.setData("speed", "1");
            speedStr = "1";
        }
        return Integer.parseInt(speedStr);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, SlimefunBlockData data) {
                ASpeedableContainer.this.tick(b,data);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    public synchronized boolean speedUp(SlimefunBlockData data) {
        int speed = getIncreasedSpeed(data);
        if (speed == speedLimit) {
            return false;
        }
        data.setData("speed", String.valueOf(speed + 1));
        return true;
    }

    public boolean speedUp(Block block) {
        return speedUp(Slimefun.getDatabaseManager()
                .getBlockDataController().getBlockData(block.getLocation()));
    }

    protected void tick(Block b, SlimefunBlockData data) {
        BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());
        CraftingOperation currentOperation = getMachineProcessor().getOperation(b);

        if (currentOperation != null) {
            if (takeCharge(b.getLocation())) {

                if (!currentOperation.isFinished()) {
                    getMachineProcessor().updateProgressBar(inv, 22, currentOperation);
                    currentOperation.addProgress(1);
                } else {
                    inv.replaceExistingItem(22, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));

                    for (ItemStack output : currentOperation.getResults()) {
                        inv.pushItem(output.clone(), getOutputSlots());
                    }

                    getMachineProcessor().endOperation(b);
                }
            }
        } else {
            MachineRecipe next = findNextRecipe(inv).clone();
            if (next != null) {
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
        return takeCharge(Slimefun.getDatabaseManager()
                .getBlockDataController().getBlockData(l));
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
}
