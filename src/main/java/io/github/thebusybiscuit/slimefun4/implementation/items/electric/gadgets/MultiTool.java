package io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedEntityType;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * The {@link MultiTool} is an electric device which can mimic
 * the behaviour of any other {@link SlimefunItem}.
 *
 * @author TheBusyBiscuit
 *
 */
public class MultiTool extends SlimefunItem implements Rechargeable {
    private static final float COST = 0.3F;
    private final NamespacedKey multiToolMode = new NamespacedKey(Slimefun.instance(), "MULTI_TOOL_MODE");
    private final List<MultiToolMode> modes = new ArrayList<>();
    private final float capacity;

    public MultiTool(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            float capacity,
            String... items) {
        super(itemGroup, item, recipeType, recipe);

        for (int i = 0; i < items.length; i++) {
            modes.add(new MultiToolMode(this, i, items[i]));
        }

        this.capacity = capacity;
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }

    private int nextIndex(int i) {
        int index = i;

        do {
            index++;

            if (index >= modes.size()) {
                index = 0;
            }
        } while (index != i && !modes.get(index).isEnabled());

        return index;
    }


    protected ItemUseHandler getItemUseHandler() {
        return e -> {
            Player p = e.getPlayer();
            ItemStack item = e.getItem();
            e.cancel();

            var im = item.getItemMeta();
            var pdc = im.getPersistentDataContainer();
            int index = pdc.getOrDefault(multiToolMode, PersistentDataType.INTEGER, 0);

            if (!p.isSneaking()) {
                if (removeItemCharge(item, COST)) {
                    SlimefunItem sfItem = modes.get(index).getItem();

                    if (sfItem != null) {
                        sfItem.callItemHandler(ItemUseHandler.class, handler -> handler.onRightClick(e));
                    }
                }
            } else {
                index = nextIndex(index);

                SlimefunItem selectedItem = modes.get(index).getItem();
                String itemName = selectedItem != null ? selectedItem.getItemName() : "Unknown";
                Slimefun.getLocalization()
                        .sendMessage(p, "messages.multi-tool.mode-change", true, msg -> msg.replace("%device%", "多功能工具")
                                .replace("%mode%", ChatColor.stripColor(itemName)));

                pdc.set(multiToolMode, PersistentDataType.INTEGER, index);
                item.setItemMeta(im);
            }
        };
    }


    private ToolUseHandler getToolUseHandler() {
        return (e, tool, fortune, drops) -> {
            // Multi Tools cannot be used as shears
            Slimefun.getLocalization().sendMessage(e.getPlayer(), "messages.multi-tool.not-shears");
            e.setCancelled(true);
        };
    }


    private EntityInteractHandler getEntityInteractionHandler() {
        return (e, item, offhand) -> {
            // Fixes #2217 - Prevent them from being used to shear entities
            EntityType type = e.getRightClicked().getType();
            if (type == VersionedEntityType.MOOSHROOM
                    || type == VersionedEntityType.SNOW_GOLEM
                    || type == EntityType.SHEEP) {
                Slimefun.getLocalization().sendMessage(e.getPlayer(), "messages.multi-tool.not-shears");
                e.setCancelled(true);
            }
        };
    }

    @Override
    public void preRegister() {
        super.preRegister();

        addItemHandler(getItemUseHandler());
        addItemHandler(getToolUseHandler());
        addItemHandler(getEntityInteractionHandler());
    }
}
