package io.github.thebusybiscuit.slimefun4.core.services;

import io.github.bakedlibs.dough.collections.OptionalMap;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.Server;
import org.bukkit.World;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * This Service is responsible for disabling a {@link SlimefunItem} in a certain {@link World}.
 *
 * @author TheBusyBiscuit
 *
 */
public class PerWorldSettingsService {
    private final Slimefun plugin;

    private final OptionalMap<UUID, Set<String>> disabledItems = new OptionalMap<>(HashMap::new);
    private final Map<SlimefunAddon, Set<String>> disabledAddons = new HashMap<>();
    private final Set<UUID> disabledWorlds = new HashSet<>();

    public PerWorldSettingsService(Slimefun plugin) {
        this.plugin = plugin;
    }

    /**
     * This method will forcefully load all currently active Worlds to load up their settings.
     *
     * @param worlds
     *            An {@link Iterable} of {@link World Worlds} to load
     */
    public void load(Iterable<World> worlds) {
        for (World world : worlds) {
            load(world);
        }
    }

    /**
     * This method loads the given {@link World} if it was not loaded before.
     *
     * @param world
     *            The {@link World} to load
     */
    public void load(World world) {
        disabledItems.putIfAbsent(world.getUID(), loadWorldFromConfig(world));
    }

    /**
     * This method checks whether the given {@link SlimefunItem} is enabled in the given {@link World}.
     *
     * @param world
     *            The {@link World} to check
     * @param item
     *            The {@link SlimefunItem} that should be checked
     *
     * @return Whether the given {@link SlimefunItem} is enabled in that {@link World}
     */
    public boolean isEnabled(World world, SlimefunItem item) {
        Set<String> items = disabledItems.computeIfAbsent(world.getUID(), id -> loadWorldFromConfig(world));

        if (disabledWorlds.contains(world.getUID())) {
            return false;
        }

        return !items.contains(item.getId());
    }

    /**
     * This method enables or disables the given {@link SlimefunItem} in the specified {@link World}.
     *
     * @param world
     *            The {@link World} in which to disable or enable the given {@link SlimefunItem}
     * @param item
     *            The {@link SlimefunItem} to enable or disable
     * @param enabled
     *            Whether the given {@link SlimefunItem} should be enabled in that world
     */
    public void setEnabled(World world, SlimefunItem item, boolean enabled) {
        Set<String> items = disabledItems.computeIfAbsent(world.getUID(), id -> loadWorldFromConfig(world));

        if (enabled) {
            items.remove(item.getId());
        } else {
            items.add(item.getId());
        }
    }

    /**
     * This method enables or disables the given {@link World}.
     *
     * @param world
     *            The {@link World} to enable or disable
     * @param enabled
     *            Whether this {@link World} should be enabled or not
     */
    public void setEnabled(World world, boolean enabled) {
        load(world);

        if (enabled) {
            disabledWorlds.remove(world.getUID());
        } else {
            disabledWorlds.add(world.getUID());
        }
    }

    /**
     * This checks whether the given {@link World} is enabled or not.
     *
     * @param world
     *            The {@link World} to check
     *
     * @return Whether this {@link World} is enabled
     */
    public boolean isWorldEnabled(World world) {
        load(world);

        return !disabledWorlds.contains(world.getUID());
    }

    /**
     * This method checks whether the given {@link SlimefunAddon} is enabled in that {@link World}.
     *
     * @param world
     *            The {@link World} to check
     * @param addon
     *            The {@link SlimefunAddon} to check
     *
     * @return Whether this addon is enabled in that {@link World}
     */
    public boolean isAddonEnabled(World world, SlimefunAddon addon) {
        return isWorldEnabled(world)
                && disabledAddons.getOrDefault(addon, Collections.emptySet()).contains(world.getName());
    }

    /**
     * This will forcefully save the settings for that {@link World}.
     * This should only be called if you altered the settings while the {@link Server} was still running.
     * This writes to a {@link File} so it can be a heavy operation.
     *
     * @param world
     *            The {@link World} to save
     */
    public void save(World world) {
        Set<String> items = disabledItems.computeIfAbsent(world.getUID(), id -> loadWorldFromConfig(world));

        Config config = getConfig(world);

        for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (item != null) {
                String addon = item.getAddon().getName().toLowerCase(Locale.ROOT);
                config.setValue(addon + '.' + item.getId(), !items.contains(item.getId()));
            }
        }

        config.save();
    }

    @SuppressWarnings("deprecation")
    private Set<String> loadWorldFromConfig(World world) {
        String name = world.getName();
        Optional<Set<String>> optional = disabledItems.get(world.getUID());

        if (optional.isPresent()) {
            return optional.get();
        } else {
            Set<String> items = new LinkedHashSet<>();
            Config config = getConfig(world);

            config.getConfiguration()
                    .options()
                    .header("This file is used to disable certain items in a particular world.\n"
                            + "You can set any item to 'false' to disable it in the world '"
                            + name
                            + "'.\n"
                            + "You can also disable an entire addon from Slimefun by setting the respective\n"
                            + "value of 'enabled' for that Addon.\n\n"
                            + "Items which are disabled in this world will not show up in the Slimefun"
                            + " Guide.\n"
                            + "You won't be able to use these items either. Using them will result in a"
                            + " warning message.");
            config.getConfiguration().options().parseComments(true);
            config.setDefaultValue("enabled", true);

            if (config.getBoolean("enabled")) {
                loadItemsFromWorldConfig(name, config, items);
                config.save();
            } else {
                disabledWorlds.add(world.getUID());
            }

            return items;
        }
    }

    private void loadItemsFromWorldConfig(
            String worldName, Config config, Set<String> items) {
        for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (item != null) {
                String addon = item.getAddon().getName().toLowerCase(Locale.ROOT);
                config.setDefaultValue(addon + ".enabled", true);
                config.setDefaultValue(addon + '.' + item.getId(), true);

                // Whether the entire addon has been disabled
                boolean isAddonDisabled = config.getBoolean(addon + ".enabled");

                if (isAddonDisabled) {
                    Set<String> blacklist = disabledAddons.computeIfAbsent(plugin, key -> new HashSet<>());
                    blacklist.add(worldName);
                }

                if (!isAddonDisabled || !config.getBoolean(addon + '.' + item.getId())) {
                    items.add(item.getId());
                }
            }
        }
    }

    /**
     * This method returns the relevant {@link Config} for the given {@link World}
     *
     * @param world
     *            Our {@link World}
     *
     * @return The corresponding {@link Config}
     */

    private Config getConfig(World world) {
        return new Config(plugin, "world-settings/" + world.getName() + ".yml");
    }
}
