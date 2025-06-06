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

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class HighVersionEventsConstructor extends VersionEventsConstructor {
    private final MethodHandle blockExplodeEventConstructorHandle;
    private Enum<?> destroyEnum;

    public HighVersionEventsConstructor() {
        Class<Enum<?>> explosionResultClass;
        try {
            //noinspection unchecked
            explosionResultClass = (Class<Enum<?>>) Class.forName("org.bukkit.ExplosionResult");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Constructor<BlockExplodeEvent> blockExplodeEventConstructor = ReflectionUtils.getConstructor(BlockExplodeEvent.class, Block.class, BlockState.class, List.class, float.class, explosionResultClass);
        try {
            blockExplodeEventConstructorHandle = ReflectionUtils.LOOKUP.unreflectConstructor(blockExplodeEventConstructor);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Enum<?>[] enums = explosionResultClass.getEnumConstants();

        for (Enum<?> field : enums) {
            if ("destroy".equalsIgnoreCase(field.name())) {
                destroyEnum = field;
                break;
            }
        }
    }

    private static DamageSource newDamageSource(String type, Entity clusingEntity) {
        return DamageSource.builder(getDamageType(type)).withCausingEntity(clusingEntity).build();
    }

    private static DamageType getDamageType(String key) {
        try {
            Field field = DamageType.class.getDeclaredField(key);
            return (DamageType) field.get(null);
        } catch (Exception e) {
            return DamageType.PLAYER_ATTACK;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public EntityDamageByEntityEvent newEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, String damageType, double damage) {
        return new EntityDamageByEntityEvent(damager, damagee, cause, newDamageSource(damageType, damager), damage);
    }

    @Override
    public BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> blockList, float yield) {
        try {
            return (BlockExplodeEvent) blockExplodeEventConstructorHandle.invoke(block, block.getState(), blockList, yield, destroyEnum);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
