package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import city.norain.slimefun4.api.menu.UniversalMenu;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

@FunctionalInterface
interface AndroidAction {

    void perform(ProgrammableAndroid android, Block b, UniversalMenu inventory, BlockFace face);
}
