package io.github.thebusybiscuit.slimefun4.storage.backend.legacy;

import com.google.common.annotations.Beta;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.gps.Waypoint;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.storage.Storage;
import io.github.thebusybiscuit.slimefun4.storage.data.PlayerData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@Beta
public class LegacyStorage implements Storage {
    @Override
    public PlayerData loadPlayerData(UUID uuid) {
        Config playerFile = new Config("data-storage/Slimefun/Players/" + uuid + ".yml");
        // Not too sure why this is its own file
        Config waypointsFile = new Config("data-storage/Slimefun/waypoints/" + uuid + ".yml");

        // Load research
        Set<Research> researches = new HashSet<>();
        for (Research research : Slimefun.getRegistry().getResearches()) {
            if (playerFile.contains("researches." + research.getID())) {
                researches.add(research);
            }
        }

        // Load backpacks
        HashMap<Integer, PlayerBackpack> backpacks = new HashMap<>();

        // Load waypoints
        Set<Waypoint> waypoints = new HashSet<>();
        for (String key : waypointsFile.getKeys()) {
            try {
                if (waypointsFile.contains(key + ".world")
                    && Bukkit.getWorld(waypointsFile.getString(key + ".world")) != null) {
                    String waypointName = waypointsFile.getString(key + ".name");
                    Location loc = waypointsFile.getLocation(key);
                    waypoints.add(new Waypoint(uuid, key, loc, waypointName));
                }
            } catch (RuntimeException x) {
                Slimefun.logger()
                        .log(
                                Level.WARNING,
                                x,
                                () -> "无法加载路径点  \"" + key + "\" for Player \"" + uuid + '"');
            }
        }

        return new PlayerData(researches, backpacks, waypoints);
    }

    // The current design of saving all at once isn't great, this will be refined.
    @Override
    public void savePlayerData(UUID uuid, PlayerData data) {
        Config playerFile = new Config("data-storage/Slimefun/Players/" + uuid + ".yml");
        // Not too sure why this is its own file
        Config waypointsFile = new Config("data-storage/Slimefun/waypoints/" + uuid + ".yml");

        // Save research
        playerFile.setValue("rearches", null);
        for (Research research : Slimefun.getRegistry().getResearches()) {
            // Save the research if it's researched
            if (data.getResearches().contains(research)) {
                playerFile.setValue("researches." + research.getID(), true);

                // Remove the research if it's no longer researched
                // ----
                // We have a duplicate ID (173) used for both Coal Gen and Bio Reactor
                // If you researched the Goal Gen we would remove it on save if you didn't also have the Bio Reactor
                // Due to the fact we would set it as researched (true in the branch above) on Coal Gen
                // but then go into this branch and remove it if you didn't have Bio Reactor
                // Sooooo we're gonna hack this for now while we move away from the Legacy Storage
                // Let's make sure the user doesn't have _any_ research with this ID and _then_ remove it
            } else if (playerFile.contains("researches." + research.getID())
                       && data.getResearches().stream().noneMatch((r) -> r.getID() == research.getID())) {
                playerFile.setValue("researches." + research.getID(), null);
            }
        }

        // Save backpacks
        for (PlayerBackpack backpack : data.getBackpacks().values()) {
            playerFile.setValue("backpacks." + backpack.getId() + ".size", backpack.getSize());

            for (int i = 0; i < backpack.getSize(); i++) {
                ItemStack item = backpack.getInventory().getItem(i);
                if (item != null) {
                    playerFile.setValue("backpacks." + backpack.getId() + ".contents." + i, item);

                    // Remove the item if it's no longer in the inventory
                } else if (playerFile.contains("backpacks." + backpack.getId() + ".contents." + i)) {
                    playerFile.setValue("backpacks." + backpack.getId() + ".contents." + i, null);
                }
            }
        }

        // Save waypoints
        waypointsFile.clear();
        for (Waypoint waypoint : data.getWaypoints()) {
            // Legacy data uses IDs
            waypointsFile.setValue(waypoint.getId(), waypoint.getLocation());
            waypointsFile.setValue(waypoint.getId() + ".name", waypoint.getName());
        }

        // Save files
        playerFile.save();
        waypointsFile.save();
    }
}
