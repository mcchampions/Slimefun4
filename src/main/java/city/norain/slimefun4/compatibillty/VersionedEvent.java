package city.norain.slimefun4.compatibillty;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.ExplosionResult;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

@UtilityClass
public class VersionedEvent {

    public void init() {
    }

    @SneakyThrows
    public BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> affectedBlock, float yield) {
        return new BlockExplodeEvent(block, block.getState(), affectedBlock, yield, ExplosionResult.DESTROY);
    }

    /**
     * See <a href="https://www.spigotmc.org/threads/inventoryview-changed-to-interface-backwards-compatibility.651754/">...</a>
     */
    @SneakyThrows
    public Inventory getTopInventory(InventoryEvent event) {
        return event.getView().getTopInventory();
    }

    @SneakyThrows
    public Inventory getClickedInventory(InventoryClickEvent event) {
        return event.getClickedInventory();
    }
}
