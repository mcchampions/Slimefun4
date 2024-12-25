package io.github.thebusybiscuit.slimefun4.core.services;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

/**
 * This Service is responsible for handling the {@link Permission} of a
 * {@link SlimefunItem}.
 * <p>
 * You can set up these {@link Permission} nodes inside the {@code permissions.yml} file.
 *
 * @author TheBusyBiscuit
 *
 */
public class PermissionsService {
    private final Map<String, String> permissions = new HashMap<>();
    private final Config config;

    @SuppressWarnings("deprecation")
    public PermissionsService(Slimefun plugin) {
        config = new Config(plugin, "permissions.yml");

        config.getConfiguration()
                .options()
                .header("""
This file is used to assign permission nodes to items from Slimefun or any of its addons.
To assign an item a certain permission node you simply have to set the 'permission' attribute
to your desired permission node.
You can also customize the text that is displayed when a Player does not have that permission.""");

        config.getConfiguration().options().parseComments(true);
    }

    /**
     * This method registers the given {@link Iterable} of {@link SlimefunItem}s
     * for use with this {@link PermissionsService}.
     *
     * @param items An {@link Iterable} of {@link SlimefunItem}s to register
     * @param save  Whether to save the default values to our permissions file
     */
    public void update(Iterable<SlimefunItem> items, boolean save) {
        for (SlimefunItem item : items) {
            update(item, false);
        }

        if (save) {
            config.save();
        }
    }

    public void update(SlimefunItem item, boolean save) {
        String path = item.getId() + ".permission";

        config.setDefaultValue(path, "none");
        config.setDefaultValue(
                item.getId() + ".lore", new String[] {"&rYou do not have the permission", "&rto access this item."});

        permissions.put(item.getId(), config.getString(path));

        if (save) {
            config.save();
        }
    }

    /**
     * This method checks whether the given {@link Permissible} has the {@link Permission}
     * to access the given {@link SlimefunItem}.
     *
     * @param p
     *            The {@link Permissible} to check
     * @param item
     *            The {@link SlimefunItem} in question
     *
     * @return Whether the {@link Permissible} has the required {@link Permission}
     */
    public boolean hasPermission(Permissible p, SlimefunItem item) {
        if (item == null) {
            // Failsafe
            return true;
        }

        String permission = permissions.get(item.getId());
        return permission == null || "none".equals(permission) || p.hasPermission(permission);
    }

    /**
     * This returns the associated {@link Permission} with the given {@link SlimefunItem}.
     * It actually returns an {@link Optional}, {@link Optional#empty()} means that there was no
     * {@link Permission} set for the given {@link SlimefunItem}
     *
     * @param item
     *            The {@link SlimefunItem} to retrieve the {@link Permission} for.
     *
     * @return An {@link Optional} holding the {@link Permission} as a {@link String} or an empty {@link Optional}
     */

    public Optional<String> getPermission(SlimefunItem item) {
        String permission = permissions.get(item.getId());

        if (permission == null || "none".equals(permission)) {
            return Optional.empty();
        } else {
            return Optional.of(permission);
        }
    }

    /**
     * This method sets the {@link Permission} for a given {@link SlimefunItem}.
     *
     * @param item
     *            The {@link SlimefunItem} to modify
     * @param permission
     *            The {@link Permission} to set
     */
    public void setPermission(SlimefunItem item, @Nullable String permission) {
        permissions.put(item.getId(), permission != null ? permission : "none");
    }

    /**
     * This saves every configured {@link Permission} to the permissions {@link File}.
     */
    public void save() {
        for (Map.Entry<String, String> entry : permissions.entrySet()) {
            config.setValue(entry.getKey() + ".permission", entry.getValue());
        }

        config.save();
    }

    /**
     * This returns the lore to display for a given {@link SlimefunItem} when a {@link Player}
     * does not have the required permission node.
     *
     * @param item
     *            The {@link SlimefunItem}
     *
     * @return The configured lore to display
     */
    public List<String> getLore(SlimefunItem item) {
        return config.getStringList(item.getId() + ".lore");
    }
}
