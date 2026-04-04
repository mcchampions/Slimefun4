package city.norain.slimefun4.compatibillty;

import city.norain.slimefun4.SlimefunExtended;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;

@UtilityClass
public class CompatibilityUtil {
    /**
     * 获取玩家放置此方块所使用的物品材质。
     * 对于大多数方块，这与 getMaterial() 相同，但有些方块有不同的材质用于放置它们。
     * 注意：此处没有涵盖所有可能不同的方块数据类型。
     *
     * @param blockData
     * @return 放置此方块所使用的材质
     */
    public Material getPlacementMaterial(BlockData blockData) {
        return blockData.getPlacementMaterial();
    }

    /**
     * 检查玩家是否处于连接状态。
     * 在 1.20- 中不能保证玩家是否连接，仅返回在线状态。
     *
     * @param player 离线玩家
     * @return 玩家连接或在线
     */
    public boolean isConnected(OfflinePlayer player) {
        if (Slimefun.instance().getServer().getOnlineMode()) {
            return player.isConnected();
        } else {
            return player.isOnline();
        }
    }

    /**
     * 获取最大生命值属性。
     * 在 1.21.3 之前，使用 GENERIC_MAX_HEALTH。
     *
     * @return 最大生命值属性
     */
    public static Attribute getMaxHealth() {
        return Registry.ATTRIBUTE.get(NamespacedKey.fromString("max_health"));
    }
}
