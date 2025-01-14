package city.norain.slimefun4.api.menu;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import javax.annotation.Nullable;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;

public abstract class UniversalMenuPreset extends BlockMenuPreset {
    /**
     * Creates a new ChestMenu with the specified
     * Title
     *
     * @param title The title of the Menu
     */
    public UniversalMenuPreset(String id, String title) {
        super(id, title);
    }

    public void newInstance(UniversalMenu menu, Block b) {
        // This method can optionally be overridden by implementations
    }

    @Override
    protected void clone(DirtyChestMenu menu) {
        if (menu instanceof UniversalMenu universalMenu) {
            SlimefunUniversalBlockData uniData = StorageCacheUtils.getUniversalBlock(universalMenu.getUuid());

            if (uniData == null) {
                return;
            }

            clone(universalMenu, uniData.getLastPresent().toLocation());
        }
    }

    protected void clone(UniversalMenu menu, Location lastPresent) {
        menu.setPlayerInventoryClickable(true);

        for (int slot : occupiedSlots) {
            menu.addItem(slot, getItemInSlot(slot));
        }

        if (getSize() > -1) {
            menu.addItem(getSize() - 1, null);
        }

        newInstance(menu, lastPresent.getBlock());

        for (int slot = 0; slot < 54; slot++) {
            if (getMenuClickHandler(slot) != null) {
                menu.addMenuClickHandler(slot, getMenuClickHandler(slot));
            }
        }

        menu.addMenuOpeningHandler(getMenuOpeningHandler());
        menu.addMenuCloseHandler(getMenuCloseHandler());
    }

    @Nullable
    public static UniversalMenuPreset getPreset(@Nullable String id) {
        if (id == null) {
            return null;
        } else {
            BlockMenuPreset preset = Slimefun.getRegistry().getMenuPresets().get(id);
            if (preset instanceof UniversalMenuPreset uniPreset) {
                return uniPreset;
            } else {
                return null;
            }
        }
    }
}
