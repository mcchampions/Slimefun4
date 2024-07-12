package io.github.thebusybiscuit.slimefun4.core.networks.cargo;

import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import org.bukkit.inventory.ItemStack;

class ItemStackAndInteger {
    private ItemStack item;
    private ItemStackWrapper wrapper;
    private int number;

    ItemStackAndInteger(ItemStack item, int amount) {
        this.number = amount;
        this.item = item;
    }

    public int getInt() {
        return number;
    }

    public ItemStack getItem() {
        initializeItem();
        return item;
    }

    public ItemStackWrapper getItemStackWrapper() {
        if (wrapper == null) {
            wrapper = ItemStackWrapper.wrap(item);
        }

        return wrapper;
    }

    public void add(int amount) {
        number += amount;
    }

    private void initializeItem() {
        if (this.item instanceof ItemStackWrapper) {
            ItemStack copy = new ItemStack(item.getType(), item.getAmount());
            if (this.item.hasItemMeta()) {
                copy.setItemMeta(this.item.getItemMeta());
            }
            this.item = copy;
        }
    }
}
