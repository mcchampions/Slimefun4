package io.github.thebusybiscuit.slimefun4.storage.data;

import com.google.common.annotations.Beta;
import io.github.thebusybiscuit.slimefun4.api.gps.Waypoint;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

/**
 * The data which backs {@link io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile}
 *
 * <b>This API is still experimental, it may change without notice.</b>
 */
// TODO: Should we keep this in PlayerProfile?
@Getter
@Beta
public class PlayerData {
    private final Set<Research> researches = new HashSet<>();
    private final Map<Integer, PlayerBackpack> backpacks = new HashMap<>();
    private final Set<Waypoint> waypoints = new HashSet<>();

    public PlayerData(Set<Research> researches, Map<Integer, PlayerBackpack> backpacks, Set<Waypoint> waypoints) {
        this.researches.addAll(researches);
        this.backpacks.putAll(backpacks);
        this.waypoints.addAll(waypoints);
    }

    public void addResearch(Research research) {
        researches.add(research);
    }

    public void removeResearch(Research research) {
        researches.remove(research);
    }

    public PlayerBackpack getBackpack(int id) {
        return backpacks.get(id);
    }

    public void addBackpack(PlayerBackpack backpack) {
        backpacks.put(backpack.getId(), backpack);
    }

    public void removeBackpack(PlayerBackpack backpack) {
        backpacks.remove(backpack.getId());
    }

    public void addWaypoint(Waypoint waypoint) {
        for (Waypoint wp : waypoints) {
            if (wp.getId().equals(waypoint.getId())) {
                throw new IllegalArgumentException("A Waypoint with that id already exists for this Player");
            }
        }

        // Limited to 21 due to limited UI space and no pagination
        if (waypoints.size() >= 21) {
            return; // not sure why this doesn't throw but the one above does...
        }

        waypoints.add(waypoint);
    }

    public void removeWaypoint(Waypoint waypoint) {
        waypoints.remove(waypoint);
    }
}
