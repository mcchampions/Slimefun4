package me.qscbm.slimefun4.utils.compatibillty;

import io.github.bakedlibs.dough.reflection.ReflectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class HighVersionEventsConstructor extends VersionEventsConstructor {
    private final Constructor<BlockExplodeEvent> blockExplodeEventConstructor;
    private Enum<?> destroyEnum = null;

    public HighVersionEventsConstructor() {
        Class<Enum<?>> explosionResultClass;
        try {
            explosionResultClass = (Class<Enum<?>>) Class.forName("org.bukkit.ExplosionResult");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        blockExplodeEventConstructor = ReflectionUtils.getConstructor(BlockExplodeEvent.class, Block.class, BlockState.class, List.class, float.class, explosionResultClass);
        Enum<?>[] enums = explosionResultClass.getEnumConstants();

        for (Enum<?> field : enums) {
            if (field.name().equalsIgnoreCase("destroy")) {
                destroyEnum = field;
                break;
            }
        }
    }

    private DamageSource newDamageSource(String type, Entity clusingEntity) {
        return DamageSource.builder(getDamageType(type)).withCausingEntity(clusingEntity).build();
    }

    private DamageType getDamageType(String key) {
        try {
            Field field = DamageType.class.getDeclaredField(key);
            return (DamageType) field.get(null);
        } catch (Exception e) {
            return DamageType.PLAYER_ATTACK;
        }
    }

    @Override
    public EntityDamageByEntityEvent newEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, String damageType, double damage) {
        return new EntityDamageByEntityEvent(damager, damagee, cause, newDamageSource(damageType, damager), damage);
    }

    @Override
    public BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> blockList, float yield) {
        try {
            return blockExplodeEventConstructor.newInstance(block, block.getState(), blockList, yield, destroyEnum);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
