package me.qscbm.slimefun4.slimefunitems.machines;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.core.attributes.Placeable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.block.Block;

public interface Speedable {
    int getSpeedLimit();
    void setSpeedLimit();
    default int getIncreasedSpeed(SlimefunBlockData data) {
        String speedStr = data.getData("ispeed");
        if (speedStr == null) {
            data.setData("ispeed", "1");
            speedStr = "1";
        }
        return Integer.parseInt(speedStr);
    }
     default boolean speedUp(SlimefunBlockData data) {
        int speed = getIncreasedSpeed(data);
        if (speed == getSpeedLimit()) {
            return false;
        }
        data.setData("ispeed", String.valueOf(speed + 1));
        return true;
    }

    default boolean speedUp(Block block) {
        return speedUp(Slimefun.getDatabaseManager()
                .getBlockDataController().getBlockData(block.getLocation()));
    }
}
