package io.github.thebusybiscuit.slimefun4.core.services;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * This Service is responsible for applying NBT data to a {@link SlimefunItemStack}.
 * This is used to ensure that the id of a {@link SlimefunItem} is stored alongside any
 * {@link ItemStack} at all times.
 *
 * @author TheBusyBiscuit
 *
 * @see SlimefunItemStack
 *
 */
public class CustomItemDataService implements Keyed {
    /**
     * This is the {@link NamespacedKey} used to store/read data.
     */
    private final NamespacedKey namespacedKey;

    /**
     * This creates a new {@link CustomItemDataService} for the given {@link Plugin} and the
     * provided data key.
     *
     * @param plugin
     *            The {@link Plugin} for this service to use
     * @param key
     *            The key under which to store data
     */
    public CustomItemDataService(Plugin plugin, String key) {
        // Null-Validation is performed in the NamespacedKey constructor
        namespacedKey = new NamespacedKey(plugin, key);
    }

    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }

    /**
     * This method stores the given id on the provided {@link ItemStack} via
     * persistent data.
     *
     * @param item
     *            The {@link ItemStack} to store data on
     * @param id
     *            The id to store on the {@link ItemStack}
     */
    public void setItemData(ItemStack item, String id) {
        ItemMeta im = item.getItemMeta();
        setItemData(im, id);
        item.setItemMeta(im);
    }

    /**
     * This method stores the given id on the provided {@link ItemMeta} via
     * persistent data.
     *
     * @param meta
     *            The {@link ItemMeta} to store data on
     * @param id
     *            The id to store on the {@link ItemMeta}
     */
    public void setItemData(ItemMeta meta, String id) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(namespacedKey, PersistentDataType.STRING, id);
    }

    /**
     * This method returns an {@link Optional} holding the data stored on the given {@link ItemStack}.
     * The {@link Optional} will be empty if the given {@link ItemStack} is null, doesn't have any {@link ItemMeta}
     * or if the requested data simply does not exist on that {@link ItemStack}.
     *
     * @param item
     *            The {@link ItemStack} to check
     *
     * @return An {@link Optional} describing the result
     */
    public Optional<String> getItemData(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return Optional.empty();
        }

        return getItemData(item.getItemMeta());
    }

    /**
     * This method returns an {@link Optional}, either empty or holding the data stored
     * on the given {@link ItemMeta}.
     *
     * @param meta
     *            The {@link ItemMeta} to check
     *
     * @return An {@link Optional} describing the result
     */
    public Optional<String> getItemData(ItemMeta meta) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return Optional.ofNullable(container.get(namespacedKey, PersistentDataType.STRING));
    }

    /**
     * This method compares the custom data stored on two {@link ItemMeta} objects.
     * This method will only return {@literal true} if both {@link ItemMeta}s contain
     * custom data and if both of their data values are equal.
     *
     * @param meta1
     *            The first {@link ItemMeta}
     * @param meta2
     *            The second {@link ItemMeta}
     *
     * @return Whether both metas have data on them and its the same.
     */
    public boolean hasEqualItemData(ItemMeta meta1, ItemMeta meta2) {
        Optional<String> data1 = getItemData(meta1);

        // Check if the first data is present
        if (data1.isPresent()) {
            // Only retrieve the second data where necessary.
            Optional<String> data2 = getItemData(meta2);

            /*
             * Check if both are present and equal.
             * Optional#equals(...) compares their values, so no need
             * to call Optional#get() here.
             */
            return data2.isPresent() && data1.equals(data2);
        } else {
            // No value present, we can return immediately.
            return false;
        }
    }
}
