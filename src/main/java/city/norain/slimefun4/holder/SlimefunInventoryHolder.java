package city.norain.slimefun4.holder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Setter
@Getter
public class SlimefunInventoryHolder implements InventoryHolder {
    protected Inventory inventory;
}
