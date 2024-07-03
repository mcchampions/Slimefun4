package io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class MultiToolMode {

    private final ItemSetting<String> item;
    private final ItemSetting<Boolean> enabled;

    MultiToolMode(MultiTool multiTool, int id, String itemId) {
        this.item = new ItemSetting<>(multiTool, "mode." + id + ".item", itemId);
        this.enabled = new ItemSetting<>(multiTool, "mode." + id + ".enabled", true);

        multiTool.addItemSetting(item, enabled);
    }

    @Nullable SlimefunItem getItem() {
        return SlimefunItem.getById(item.getValue());
    }

    boolean isEnabled() {
        return enabled.getValue();
    }
}
