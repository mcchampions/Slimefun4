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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 这是个临时适配的工具类
 * 如果上游更新相关的工具类会迁移保证适配附属
 */
@SuppressWarnings({"JavaReflectionMemberAccess", "FieldCanBeLocal"})
public class VersionEventsUtils {
    public static VersionEventsConstructor versionEventsConstructor;

    private static Method TOP_INVENTORY_GETTER;

    private static Method CLICKED_INVENTORY_GETTER;

    private static Method EXPLOSION_RESULT_GETTER;

    private static Class<?> EXPLOSION_RESULT_CLASS;

    private static Enum<?> TRIGGER_BLOCK_ENUM;

    static {
        if (Slimefun.getMinecraftVersion()
                .isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            versionEventsConstructor = new HighVersionEventsConstructor();
        } else {
            versionEventsConstructor = new VersionEventsConstructor();
        }
        try {
            TOP_INVENTORY_GETTER =
                    Class.forName("org.bukkit.inventory.InventoryView")
                            .getMethod("getTopInventory");
            TOP_INVENTORY_GETTER.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {}
        try {
            CLICKED_INVENTORY_GETTER =
                    Class.forName("org.bukkit.event.inventory.InventoryClickEvent")
                            .getMethod("getClickedInventory");
            CLICKED_INVENTORY_GETTER.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {}
        try {
            EXPLOSION_RESULT_GETTER =
                    Class.forName("org.bukkit.event.block.BlockExplodeEvent")
                            .getMethod("getExplosionResult");
            EXPLOSION_RESULT_GETTER.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException ignored) {}
        try {
            EXPLOSION_RESULT_CLASS = Class.forName("org.bukkit.ExplosionResult");
            Method method = EXPLOSION_RESULT_CLASS.getMethod("values");
            method.setAccessible(true);
            Enum<?>[] enums = (Enum<?>[]) method.invoke(null);
            for (Enum<?> e : enums) {
                if (e.name().equals("TRIGGER_BLOCK")) {
                    TRIGGER_BLOCK_ENUM = e;
                    break;
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException ignored) {}
    }

    public static EntityDamageByEntityEvent newEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, String type, double damage) {
        return versionEventsConstructor.newEntityDamageByEntityEvent(damager, damagee, cause, type, damage);
    }

    public static BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> blockList, float yield) {
        return versionEventsConstructor.newBlockExplodeEvent(block, blockList, yield);
    }

    public static Inventory getTopInventory(InventoryEvent event) {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            return event.getView().getTopInventory();
        }
        try {
            return (Inventory) TOP_INVENTORY_GETTER.invoke(event.getView());
        } catch (Exception e) {
            return event.getView().getTopInventory();
        }
    }

    public static Inventory getClickedInventory(InventoryClickEvent event) {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_21)) {
            return event.getClickedInventory();
        }
        try {
            return (Inventory) CLICKED_INVENTORY_GETTER.invoke(event);
        } catch (Exception e) {
            return event.getClickedInventory();
        }
    }

    public static boolean isTriggerBlock(BlockExplodeEvent e) {
        try {
            Object result = EXPLOSION_RESULT_GETTER.invoke(e);
            return result == TRIGGER_BLOCK_ENUM;
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return true;
        }
    }
}
