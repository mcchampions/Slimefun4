package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotRotatable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The {@link CargoConnectorNode} connects a {@link CargoNode} with a {@link CargoNet}.
 * It has no further functionality.
 *
 * @author TheBusyBiscuit
 *
 * @see CargoNode
 * @see CargoNet
 *
 */
public class CargoConnectorNode extends SimpleSlimefunItem<BlockUseHandler> implements NotRotatable {
    public CargoConnectorNode(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public BlockUseHandler getItemHandler() {
        return e -> {
            if (e.getClickedBlock().isEmpty()) {
                return;
            }

            Player p = e.getPlayer();
            Block b = e.getClickedBlock().get();

            if (CargoNet.getNetworkFromLocation(b.getLocation()) != null) {
                Slimefun.getLocalization().sendActionbarMessage(p, "machines.CARGO_NODES.connected", false);
            } else {
                Slimefun.getLocalization().sendActionbarMessage(p, "machines.CARGO_NODES.not-connected", false);
            }
        };
    }
}
