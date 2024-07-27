package me.qscbm.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * 这是个临时适配的工具类
 * 如果上游更新相关的工具类会迁移保证适配附属
 * <p>
 * 事实上这里的版本判断完全可以替换为移除时的版本
 */
public class VersionEventsUtils {
    public static VersionEventsConstructor versionEventsConstructor;

    static {
        if (Slimefun.getMinecraftVersion()
                .isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            versionEventsConstructor = new HighVersionEventsConstructor();
        } else {
            versionEventsConstructor = new VersionEventsConstructor();
        }
    }

    public static EntityDamageByEntityEvent newEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, String type, double damage) {
        return versionEventsConstructor.newEntityDamageByEntityEvent(damager, damagee, cause, type, damage);
    }

    public static BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> blockList, float yield) {
        return versionEventsConstructor.newBlockExplodeEvent(block, blockList, yield);
    }

}
