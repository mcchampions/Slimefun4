package io.github.thebusybiscuit.slimefun4.api.items;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.bakedlibs.dough.items.ItemMetaSnapshot;
import io.github.bakedlibs.dough.skins.PlayerHead;
import io.github.bakedlibs.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.api.exceptions.PrematureCodeException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.WrongItemStackException;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The {@link SlimefunItemStack} functions as the base for any
 * {@link SlimefunItem}.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 *
 */
@SerializableAs("ItemStack")
public class SlimefunItemStack extends ItemStack {

    private final String id;
    private ItemMetaSnapshot itemMetaSnapshot;

    private boolean locked = false;
    private String texture = null;

    public SlimefunItemStack(String id, ItemStack item) {
        super(item);

        Validate.notNull(id, "The Item id must never be null!");
        Validate.isTrue(
                id.equals(id.toUpperCase(Locale.ROOT)), "Slimefun Item Ids must be uppercase! (e.g. 'MY_ITEM_ID')");

        if (Slimefun.instance() == null) {
            throw new PrematureCodeException(
                    "A SlimefunItemStack must never be be created before your Plugin was enabled.");
        }

        this.id = id;

        ItemMeta meta = getItemMeta();

        Slimefun.getItemDataService().setItemData(meta, id);
        Slimefun.getItemTextureService().setTexture(meta, id);

        setItemMeta(meta);
    }

    public SlimefunItemStack(String id, ItemStack item, Consumer<ItemMeta> consumer) {
        this(id, item);

        ItemMeta im = getItemMeta();
        consumer.accept(im);
        setItemMeta(im);
    }

    public SlimefunItemStack(String id, Material type, Consumer<ItemMeta> consumer) {
        this(id, new ItemStack(type), consumer);
    }

    public SlimefunItemStack(
            String id, Material type, @Nullable String name, Consumer<ItemMeta> consumer) {
        this(id, type, meta -> {
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            consumer.accept(meta);
        });
    }

    public SlimefunItemStack(String id, ItemStack item, @Nullable String name, String... lore) {
        this(id, item, im -> {
            if (name != null) {
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            if (lore.length > 0) {
                List<String> lines = new ArrayList<>();

                for (String line : lore) {
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                im.setLore(lines);
            }
        });
    }

    public SlimefunItemStack(String id, Material type, @Nullable String name, String... lore) {
        this(id, new ItemStack(type), name, lore);
    }

    public SlimefunItemStack(
            String id, Material type, Color color, @Nullable String name, String... lore) {
        this(id, type, im -> {
            if (name != null) {
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            if (lore.length > 0) {
                List<String> lines = new ArrayList<>();

                for (String line : lore) {
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }

                im.setLore(lines);
            }

            if (im instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(color);
            }

            if (im instanceof PotionMeta potionMeta) {
                potionMeta.setColor(color);
            }
        });
    }

    public SlimefunItemStack(
            String id,
            Color color,
            PotionEffect effect,
            @Nullable String name,
            String... lore) {
        this(id, Material.POTION, im -> {
            if (name != null) {
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            if (lore.length > 0) {
                List<String> lines = new ArrayList<>();

                for (String line : lore) {
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }

                im.setLore(lines);
            }

            if (im instanceof PotionMeta potionMeta) {
                potionMeta.setColor(color);
                potionMeta.addCustomEffect(effect, true);

                if (effect.getType().equals(PotionEffectType.SATURATION)) {
                    im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                }
            }
        });
    }

    public SlimefunItemStack(SlimefunItemStack item, int amount) {
        this(item.getItemId(), item);
        setAmount(amount);
    }

    public SlimefunItemStack(String id, String texture, @Nullable String name, String... lore) {
        this(id, getSkull(id, texture), name, lore);
        this.texture = getTexture(id, texture);
    }

    public SlimefunItemStack(String id, HeadTexture head, @Nullable String name, String... lore) {
        this(id, head.getTexture(), name, lore);
    }

    public SlimefunItemStack(
            String id, String texture, @Nullable String name, Consumer<ItemMeta> consumer) {
        this(id, getSkull(id, texture), meta -> {
            if (name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            consumer.accept(meta);
        });

        this.texture = getTexture(id, texture);
    }

    public SlimefunItemStack(String id, String texture, Consumer<ItemMeta> consumer) {
        this(id, getSkull(id, texture), consumer);
        this.texture = getTexture(id, texture);
    }

    /**
     * Returns the id that was given to this {@link SlimefunItemStack}.
     *
     * @return The {@link SlimefunItem} id for this {@link SlimefunItemStack}
     */
    public final String getItemId() {
        return id;
    }

    /**
     * Gets the {@link SlimefunItem} associated for this {@link SlimefunItemStack}.
     * Null if no item is found.
     *
     * @return The {@link SlimefunItem} for this {@link SlimefunItemStack}, null if not found.
     */
    public @Nullable SlimefunItem getItem() {
        return SlimefunItem.getById(id);
    }

    /**
     * This method returns the associated {@link SlimefunItem} and casts it to the provided
     * {@link Class}.
     *
     * If no item was found or the found {@link SlimefunItem} is not of the requested type,
     * the method will return null.
     *
     * @param <T>
     *            The type of {@link SlimefunItem} to cast this to
     * @param type
     *            The {@link Class} of the target {@link SlimefunItem}
     *
     * @return The {@link SlimefunItem} this {@link SlimefunItem} represents, casted to the given type
     */
    public @Nullable <T extends SlimefunItem> T getItem(Class<T> type) {
        SlimefunItem item = getItem();
        return type.isInstance(item) ? type.cast(item) : null;
    }

    public ItemMetaSnapshot getItemMetaSnapshot() {
        return itemMetaSnapshot;
    }

    @Override
    public boolean setItemMeta(ItemMeta meta) {
        validate();
        itemMetaSnapshot = new ItemMetaSnapshot(meta);

        return super.setItemMeta(meta);
    }

    @Override
    public void setType(Material type) {
        validate();
        super.setType(type);
    }

    @Override
    public void setAmount(int amount) {
        validate();
        super.setAmount(amount);
    }

    private void validate() {
        if (locked) {
            throw new WrongItemStackException(id + " is not mutable.");
        }
    }

    public void lock() {
        locked = true;
    }

    public Optional<String> getSkullTexture() {
        return Optional.ofNullable(texture);
    }

    public @Nullable String getDisplayName() {
        if (itemMetaSnapshot == null) {
            // Just to be extra safe
            return null;
        }

        return itemMetaSnapshot.getDisplayName().orElse(null);
    }

    private static ItemStack getSkull(String id, String texture) {
        PlayerSkin skin = PlayerSkin.fromBase64(getTexture(id, texture));
        return PlayerHead.getItemStack(skin);
    }

    private static String getTexture(String id, String texture) {
        Validate.notNull(id, "The id cannot be null");
        Validate.notNull(texture, "The texture cannot be null");

        if (texture.startsWith("ey")) {
            return texture;
        } else if (CommonPatterns.HEXADECIMAL.matcher(texture).matches()) {
            String value =
                    "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + texture + "\"}}}";
            return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        } else {
            throw new IllegalArgumentException(
                    "The provided texture for Item \"" + id + "\" does not seem to be a valid texture String!");
        }
    }

    @Override
    public ItemStack clone() {
        return new SlimefunItemStack(id, this);
    }

    @Override
    public String toString() {
        return "SlimefunItemStack (" + id + (getAmount() > 1 ? (" x " + getAmount()) : "") + ')';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object obj) {
        // We don't want people to override this, it should use the super method
        return super.equals(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        // We don't want people to override this, it should use the super method
        return super.hashCode();
    }
}
