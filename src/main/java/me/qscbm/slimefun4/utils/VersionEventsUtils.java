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
    public static EntityDamageByEntityEvent newEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, String type, double damage) {
        return Slimefun.getMinecraftVersion()
                .isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? HighVersionUtils.newEntityDamageByEntityEvent(damager, damagee, cause, type, damage) : LowerVersionUtils.newEntityDamageByEntityEvent(damager, damagee, cause, damage);
    }

    public static BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> blockList, float yield) {
        return Slimefun.getMinecraftVersion()
                .isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? HighVersionUtils.newBlockExplodeEvent(block, blockList, yield) : LowerVersionUtils.newBlockExplodeEvent(block, blockList, yield);
    }
}
