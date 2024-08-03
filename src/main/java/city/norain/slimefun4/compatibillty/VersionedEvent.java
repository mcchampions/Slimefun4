package city.norain.slimefun4.compatibillty;


import lombok.experimental.UtilityClass;
import me.qscbm.slimefun4.utils.VersionEventsUtils;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

@UtilityClass
public class VersionedEvent {
    public void init() {

    }

    public BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> affectedBlock, float yield) {
        return VersionEventsUtils.newBlockExplodeEvent(block, affectedBlock, yield);
    }

    /**
     * See <a href="https://www.spigotmc.org/threads/inventoryview-changed-to-interface-backwards-compatibility.651754/">...</a>
     */
    public Inventory getTopInventory(InventoryEvent event) {
        return VersionEventsUtils.getTopInventory(event);
    }

    public Inventory getClickedInventory(InventoryClickEvent event) {
        return VersionEventsUtils.getClickedInventory(event);
    }
}
