package me.mrCookieSlime.Slimefun.api.inventory;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

// This class will be deprecated, relocated and rewritten in a future version.
public abstract class BlockMenuPreset extends ChestMenu {
    private final Set<Integer> occupiedSlots = new HashSet<>();
    private final String inventoryTitle;
    private final String id;

    private boolean locked;

    protected BlockMenuPreset(String id, String title) {
        super(title);

        this.id = id;
        this.inventoryTitle = title;
        init();

        Slimefun.getRegistry().getMenuPresets().put(id, this);
    }

    private void checkIfLocked() {
        if (locked) {
            throw new UnsupportedOperationException(
                    "You cannot modify the BlockMenuPreset anymore, modify the individual instances" + " instead.");
        }
    }

    public abstract void init();

    /**
     * This method returns whether a given {@link Player} is allowed to open the
     * {@link BlockMenu} of that {@link Block}.
     * Override this as necessary.
     *
     * @param b
     *            The {@link Block} trying to be opened
     * @param p
     *            The {@link Player} who wants to open the {@link BlockMenu}
     *
     * @return Whether that {@link Player} is allowed
     */
    public abstract boolean canOpen(Block b, Player p);

    public abstract int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow);

    /**
     * This method is called whenever an {@link ItemStack} changes.
     * You can override this as necessary if you need to listen to these events
     *
     * @param next
     *            The {@link ItemStack} that it changes to
     *
     * @return The new outcome of this operation
     */
    @Nullable protected ItemStack onItemStackChange(
            @Nullable ItemStack next) {
        // Override this as necessary
        return next;
    }

    public void newInstance(BlockMenu menu, Block b) {
        // This method can optionally be overridden by implementations
    }

    public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
        // This method will default to that method, it can be overridden by subclasses though
        return getSlotsAccessedByItemTransport(flow);
    }

    @Override
    public void replaceExistingItem(int slot, ItemStack item) {
        throw new UnsupportedOperationException("BlockMenuPreset does not support this method.");
    }

    /**
     * This method will draw unclickable background items into this {@link BlockMenuPreset}.
     *
     * @param item
     *            The {@link ItemStack} that should be used as background
     * @param slots
     *            The slots which should be treated as background
     */
    public void drawBackground(ItemStack item, int[] slots) {
        checkIfLocked();

        for (int slot : slots) {
            addItem(slot, item, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    /**
     * This method will draw unclickable background items into this {@link BlockMenuPreset}.
     *
     * @param slots
     *            The slots which should be treated as background
     */
    public void drawBackground(int[] slots) {
        drawBackground(ChestMenuUtils.getBackground(), slots);
    }

    @Override
    public ChestMenu addItem(int slot, @Nullable ItemStack item) {
        checkIfLocked();

        occupiedSlots.add(slot);
        return super.addItem(slot, item);
    }

    @Override
    public ChestMenu addMenuClickHandler(int slot, MenuClickHandler handler) {
        checkIfLocked();
        return super.addMenuClickHandler(slot, handler);
    }

    
    public ChestMenu setSize(int size) {
        checkIfLocked();

        return super.setSize(size);
    }

    /**
     * This method returns the size of this {@link BlockMenuPreset}.
     * If the size has not been determined yet, this will return -1.
     *
     * @return The size of this {@link BlockMenuPreset}
     */
    public int getSize() {
        return super.getSize();
    }

    /**
     * This returns the title of this {@link BlockMenuPreset}, the title will
     * be visible in every {@link InventoryView} for any menu created using this {@link BlockMenuPreset}.
     *
     * @return The inventory title for this {@link BlockMenuPreset}
     */
    public String getTitle() {
        return inventoryTitle;
    }

    
    public Set<Integer> getPresetSlots() {
        return occupiedSlots;
    }

    
    public Set<Integer> getInventorySlots() {
        Set<Integer> emptySlots = new HashSet<>();

        if (isSizeAutomaticallyInferred()) {
            for (int i = 0; i < toInventory().getSize(); i++) {
                if (!occupiedSlots.contains(i)) {
                    emptySlots.add(i);
                }
            }
        } else {
            for (int i = 0; i < getSize(); i++) {
                if (!occupiedSlots.contains(i)) {
                    emptySlots.add(i);
                }
            }
        }

        return emptySlots;
    }

    protected void clone(DirtyChestMenu menu) {
        menu.setPlayerInventoryClickable(true);

        if (isSizeAutomaticallyInferred()) {
            menu.addItem(getSize() - 1, null);
        } else menu.setSize(getSize());

        for (int slot : occupiedSlots) {
            menu.addItem(slot, getItemInSlot(slot));
        }

        if (menu instanceof BlockMenu blockMenu) {
            newInstance(blockMenu, blockMenu.getLocation());
        }

        for (int slot = 0; slot < 54; slot++) {
            if (getMenuClickHandler(slot) != null) {
                menu.addMenuClickHandler(slot, getMenuClickHandler(slot));
            }
        }

        menu.addMenuOpeningHandler(getMenuOpeningHandler());
        menu.addMenuCloseHandler(getMenuCloseHandler());
    }

    public void newInstance(BlockMenu menu, Location l) {
        Slimefun.runSync(() -> {
            locked = true;

            try {
                newInstance(menu, l.getBlock());
            } catch (Exception | LinkageError x) {
                getSlimefunItem().error("An Error occurred while trying to create a BlockMenu", x);
            }
        });
    }

    /**
     * This returns the id of the associated {@link SlimefunItem}.
     * It also doubles as the id for this {@link BlockMenuPreset}.
     *
     * @return Our identifier
     */
    
    public String getID() {
        return id;
    }

    /**
     * This returns the {@link SlimefunItem} associated with this {@link BlockMenuPreset}.
     *
     * @return The associated {@link SlimefunItem}
     */
    
    public SlimefunItem getSlimefunItem() {
        return SlimefunItem.getById(id);
    }

    @Nullable public static BlockMenuPreset getPreset(@Nullable String id) {
        return id == null ? null : Slimefun.getRegistry().getMenuPresets().get(id);
    }

    public static boolean isInventory(String id) {
        return Slimefun.getRegistry().getMenuPresets().containsKey(id);
    }
}
