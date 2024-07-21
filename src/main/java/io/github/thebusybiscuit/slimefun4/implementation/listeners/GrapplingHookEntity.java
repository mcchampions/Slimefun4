package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

final class GrapplingHookEntity {
    private final boolean dropItem;
    private final boolean wasConsumed;
    @Getter
    private final Arrow arrow;
    private final Entity leashTarget;

    GrapplingHookEntity(Player p, Arrow arrow, Entity leashTarget, boolean dropItem, boolean wasConsumed) {
        this.arrow = arrow;
        this.wasConsumed = wasConsumed;
        this.leashTarget = leashTarget;
        this.dropItem = p.getGameMode() != GameMode.CREATIVE && dropItem;
    }

    public void drop(Location l) {
        // If a grappling hook was consumed, drop one grappling hook on the floor
        if (dropItem && wasConsumed) {
            Item item = l.getWorld().dropItem(l, SlimefunItems.GRAPPLING_HOOK.clone());
            item.setPickupDelay(16);
        }
    }

    public void remove() {
        if (arrow.isValid()) {
            arrow.remove();
        }

        if (leashTarget.isValid()) {
            leashTarget.remove();
        }
    }
}
