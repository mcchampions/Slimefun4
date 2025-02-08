package me.qscbm.slimefun4.items.machines;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.block.Block;

public interface Speedable {
    int getSpeedLimit();
    void setSpeedLimit(int speedLimit);
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
        return speedUp(StorageCacheUtils.getBlock(block.getLocation()));
    }
}
