package me.qscbm.slimefun4.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Field;
import java.util.List;

public class HighVersionUtils {
    public static DamageSource newDamageSource(String type) {
        return DamageSource.builder(getDamageType(type)).build();
    }

    public static DamageSource newDamageSource(String type, Entity clusingEntity) {
        return DamageSource.builder(getDamageType(type)).withCausingEntity(clusingEntity).build();
    }

    public static DamageSource newDamageSource(String type, Entity clusingEntity, Entity directEntity) {
        return DamageSource.builder(getDamageType(type)).withCausingEntity(clusingEntity).withDirectEntity(directEntity).build();
    }

    public static DamageSource newDamageSource(String type, Location location) {
        return DamageSource.builder(getDamageType(type)).withDamageLocation(location).build();
    }

    public static DamageSource newDamageSource(String type, Entity clusingEntity, Location location) {
        return DamageSource.builder(getDamageType(type)).withCausingEntity(clusingEntity).withDamageLocation(location).build();
    }

    public static DamageSource newDamageSource(String type, Entity clusingEntity, Entity directEntity, Location location) {
        return DamageSource.builder(getDamageType(type)).withCausingEntity(clusingEntity).withDirectEntity(directEntity).withDamageLocation(location).build();
    }

    public static DamageType getDamageType(String key) {
        try {
            Field field = DamageType.class.getDeclaredField(key);
            return (DamageType) field.get(null);
        } catch (Exception e) {
            return DamageType.PLAYER_ATTACK;
        }
    }

    public static EntityDamageByEntityEvent newEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, String damageType, double damage) {
        return new EntityDamageByEntityEvent(damager, damagee, cause, newDamageSource(damageType, damager), damage);
    }

    public static BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> blockList, float yield) {
        return new BlockExplodeEvent(block, block.getState(), blockList, yield);
    }
}
