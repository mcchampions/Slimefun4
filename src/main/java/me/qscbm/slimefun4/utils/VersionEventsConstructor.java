package me.qscbm.slimefun4.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class VersionEventsConstructor {
    public EntityDamageByEntityEvent newEntityDamageByEntityEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, String type, double damage) {
        return new EntityDamageByEntityEvent(damager, damagee, cause, damage);
    }

    public BlockExplodeEvent newBlockExplodeEvent(Block block, List<Block> blockList, float yield) {
        return new BlockExplodeEvent(block, blockList, yield);
    }
}
