package me.qscbm.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.utils.compatibillty.HighVersionEventsConstructor;
import me.qscbm.slimefun4.utils.compatibillty.VersionEventsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * 这是个临时适配的工具类
 * 如果上游更新相关的工具类会迁移保证适配附属
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

    public static Inventory getTopInventory(InventoryEvent event) {
        /*
        我使用1.20.4编译, 无需理会
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            return event.getView().getTopInventory();
        } else {
            if (GET_TOP_INVENTORY == null) {
                throw new IllegalStateException("Unable to get top inventory: missing method");
            }

            try {
                return (Inventory) GET_TOP_INVENTORY.invoke(event.getView());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
         */
        return event.getView().getTopInventory();
    }

    public static Inventory getClickedInventory(InventoryClickEvent event) {
        /*
        我使用1.20.4编译, 无需理会
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            return event.getClickedInventory();
        } else {
            if (GET_CLICKED_INVENTORY == null) {
                throw new IllegalStateException("Unable to get clicked inventory: missing method");
            }

            try {
                return (Inventory) GET_CLICKED_INVENTORY.invoke(event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
         */
        return event.getClickedInventory();
    }
}
