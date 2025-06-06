package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

// TODO: Replace this with "AbstractContainer" and "AbstractElectricalMachine" classes.
public abstract class AContainer extends SlimefunItem
        implements InventoryBlock, EnergyNetComponent, MachineProcessHolder<CraftingOperation> {
    private static final int[] BORDER = {0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 31, 36, 37, 38, 39, 40, 41, 42, 43, 44};
    private static final int[] BORDER_IN = {9, 10, 11, 12, 18, 21, 27, 28, 29, 30};
    private static final int[] BORDER_OUT = {14, 15, 16, 17, 23, 26, 32, 33, 34, 35};

    protected final List<MachineRecipe> recipes = new ArrayList<>();
    private final MachineProcessor<CraftingOperation> processor = new MachineProcessor<>(this);

    private int energyConsumedPerTick = -1;
    private int energyCapacity = -1;
    @Getter
    private int processingSpeed = -1;

    protected AContainer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        processor.setProgressBar(getProgressBar());
        createPreset(this, getInventoryTitle(), this::constructMenu);

        addItemHandler(onBlockBreak());
    }

    protected BlockBreakHandler onBlockBreak() {
        return new SimpleBlockBreakHandler() {
            @Override
            public void onBlockBreak(Block b) {
                BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getInputSlots());
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }

                processor.endOperation(b);
            }
        };
    }

    protected AContainer(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput) {
        this(itemGroup, item, recipeType, recipe);
        this.recipeOutput = recipeOutput;
    }

    @Override
    public MachineProcessor<CraftingOperation> getMachineProcessor() {
        return processor;
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int i : BORDER) {
            preset.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : BORDER_IN) {
            preset.addItem(i, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : BORDER_OUT) {
            preset.addItem(i, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        preset.addItem(
                22, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "), ChestMenuUtils.getEmptyClickHandler());

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new AdvancedMenuClickHandler() {
                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(
                        InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return cursor == null || cursor.getType() == Material.AIR;
                }
            });
        }
    }

    /**
     * This method returns the title that is used for the {@link Inventory} of an
     * {@link AContainer} that has been opened by a Player.
     * <p>
     * Override this method to set the title.
     *
     * @return The title of the {@link Inventory} of this {@link AContainer}
     */

    public String getInventoryTitle() {
        return getItemName();
    }

    /**
     * This method returns the {@link ItemStack} that this {@link AContainer} will
     * use as a progress bar.
     * <p>
     * Override this method to set the progress bar.
     *
     * @return The {@link ItemStack} to use as the progress bar
     */
    public abstract ItemStack getProgressBar();

    /**
     * This method returns the max amount of electricity this machine can hold.
     *
     * @return The max amount of electricity this Block can store.
     */
    @Override
    public int getCapacity() {
        return energyCapacity;
    }

    /**
     * This method returns the amount of energy that is consumed per operation.
     *
     * @return The rate of energy consumption
     */
    public int getEnergyConsumption() {
        return energyConsumedPerTick;
    }

    /**
     * This method returns the speed at which this machine will operate.
     * This can be implemented on an instantiation-level to create different tiers
     * of machines.
     *
     * @return The speed of this machine
     */
    public int getSpeed() {
        return processingSpeed;
    }

    /**
     * This sets the energy capacity for this machine.
     * This method <strong>must</strong> be called before registering the item
     * and only before registering.
     *
     * @param capacity The amount of energy this machine can store
     * @return This method will return the current instance of {@link AContainer}, so that can be chained.
     */
    public final AContainer setCapacity(int capacity) {
        this.energyCapacity = capacity;
        return this;
    }

    /**
     * This sets the speed of this machine.
     *
     * @param speed The speed multiplier for this machine, must be above zero
     * @return This method will return the current instance of {@link AContainer}, so that can be chained.
     */
    public final AContainer setProcessingSpeed(int speed) {
        this.processingSpeed = speed;
        return this;
    }

    /**
     * This method sets the energy consumed by this machine per tick.
     *
     * @param energyConsumption The energy consumed per tick
     * @return This method will return the current instance of {@link AContainer}, so that can be chained.
     */
    public final AContainer setEnergyConsumption(int energyConsumption) {
        this.energyConsumedPerTick = energyConsumption;
        return this;
    }

    @Override
    public void register(SlimefunAddon addon) {
        this.addon = addon;

        if (energyCapacity > 0 && energyConsumedPerTick > 0 && processingSpeed > 0) {
            super.register(addon);
        }
    }

    /**
     * This method returns an internal identifier that is used to identify this {@link AContainer}
     * and its recipes.
     * <p>
     * When adding recipes to an {@link AContainer} we will use this identifier to
     * identify all instances of the same {@link AContainer}.
     * This way we can add the recipes to all instances of the same machine.
     * <p>
     * <strong>This method will be deprecated and replaced in the future</strong>
     *
     * @return The identifier of this machine
     */

    public abstract String getMachineIdentifier();

    /**
     * This method registers all default recipes for this machine.
     */
    protected void registerDefaultRecipes() {
        // Override this method to register your machine recipes
    }

    public List<MachineRecipe> getMachineRecipes() {
        return recipes;
    }

    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>(recipes.size() << 1);

        for (MachineRecipe recipe : recipes) {
            if (recipe.getInput().length != 1) {
                continue;
            }

            displayRecipes.add(recipe.getInput()[0]);
            displayRecipes.add(recipe.getOutput()[0]);
        }

        return displayRecipes;
    }

    @Override
    public int[] getInputSlots() {
        return new int[]{19, 20};
    }

    @Override
    public int[] getOutputSlots() {
        return new int[]{24, 25};
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    public void registerRecipe(MachineRecipe recipe) {
        recipe.setTicks(recipe.getTicks() / processingSpeed);
        recipes.add(recipe);
    }

    public void registerRecipe(int seconds, ItemStack[] input, ItemStack[] output) {
        registerRecipe(new MachineRecipe(seconds, input, output));
    }

    public void registerRecipe(int seconds, ItemStack input, ItemStack output) {
        registerRecipe(new MachineRecipe(seconds, new ItemStack[]{input}, new ItemStack[]{output}));
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {
            @Override
            public void tick(Block b, SlimefunItem sf, SlimefunBlockData data) {
                AContainer.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    protected void tick(Block b) {
        BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());
        CraftingOperation currentOperation = processor.getOperation(b);

        if (currentOperation != null) {
            if (!takeCharge(b.getLocation())) {
                return;
            }
            if (!currentOperation.isFinished()) {
                processor.updateProgressBar(inv, 22, currentOperation);
                currentOperation.addProgress(1);
                return;
            }
            inv.replaceExistingItem(22, new CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));

            for (ItemStack output : currentOperation.getResults()) {
                inv.pushItem(output.clone(), getOutputSlots());
            }

            processor.endOperation(b);
            return;
        }
        MachineRecipe next = findNextRecipe(inv);

        if (next != null) {
            currentOperation = new CraftingOperation(next);
            processor.startOperation(b, currentOperation);

            // Fixes #3534 - Update indicator immediately
            processor.updateProgressBar(inv, 22, currentOperation);
        }
    }

    /**
     * This method will remove charge from a location if it is chargeable.
     *
     * @param l location to try to remove charge from
     * @return Whether charge was taken if its chargeable
     */
    protected boolean takeCharge(Location l) {
        if (isChargeable()) {
            int charge = getCharge(l);

            if (charge < energyConsumedPerTick) {
                return false;
            }

            setCharge(l, charge - energyConsumedPerTick);
        }
        return true;
    }

    protected MachineRecipe findNextRecipe(BlockMenu inv) {
        Map<Integer, ItemStack> inventory = new HashMap<>();

        for (int slot : getInputSlots()) {
            ItemStack item = inv.getItemInSlot(slot);

            if (item != null) {
                inventory.put(slot, ItemStackWrapper.wrap(item));
            }
        }

        Map<Integer, Integer> found = new HashMap<>();

        for (MachineRecipe recipe : recipes) {
            for (ItemStack input : recipe.getInput()) {
                for (int slot : getInputSlots()) {
                    if (SlimefunUtils.isItemSimilar(inventory.get(slot), input, true)) {
                        found.put(slot, input.getAmount());
                        break;
                    }
                }
            }

            if (found.size() != recipe.getInput().length) {
                found.clear();
                continue;
            }
            if (!InvUtils.fitAll(inv.toInventory(), recipe.getOutput(), getOutputSlots())) {
                return null;
            }
            for (Map.Entry<Integer, Integer> entry : found.entrySet()) {
                inv.consumeItem(entry.getKey(), entry.getValue());
            }
            return recipe;
        }

        return null;
    }

    @Override
    public void enable() {
        super.enable();
        registerDefaultRecipes();
    }

    @Override
    public void disable() {
        super.disable();
        recipes.clear();
    }

    @Override
    public void postRegister() {
        if (getState() == ItemState.ENABLED) {
            registerDefaultRecipes();
        }
    }
}
