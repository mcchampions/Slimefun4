package io.github.thebusybiscuit.slimefun4.api.player;

import city.norain.slimefun4.holder.SlimefunInventoryHolder;
import city.norain.slimefun4.utils.InventoryUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.callback.IAsyncReadCallback;
import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.BackpackListener;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import lombok.Getter;
import me.qscbm.slimefun4.message.QsTextComponentImpl;
import me.qscbm.slimefun4.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * This class represents the instance of a {@link SlimefunBackpack} that is ready to
 * be opened.
 * <p>
 * It holds an actual {@link Inventory} and represents the backpack on the
 * level of an individual {@link ItemStack} as opposed to the class {@link SlimefunBackpack}.
 *
 * @author TheBusyBiscuit
 *
 * @see SlimefunBackpack
 * @see BackpackListener
 */
public class PlayerBackpack extends SlimefunInventoryHolder {
    public static final String LORE_OWNER = "§7所有者: ";
    private static final String COLORED_LORE_OWNER = ChatColors.color(LORE_OWNER);
    private static final String PLAIN_LORE_OWNER = "所有者: ";
    private static final NamespacedKey KEY_BACKPACK_UUID = new NamespacedKey(Slimefun.instance(), "B_UUID");
    private static final NamespacedKey KEY_OWNER_UUID = new NamespacedKey(Slimefun.instance(), "OWNER_UUID");
    @Getter
    private final OfflinePlayer owner;
    private final UUID uuid;
    @Getter
    private final int id;
    @Getter
    private String name;
    @Getter
    private int size;
    private boolean isInvalid;

    public static void getAsync(ItemStack item, Consumer<PlayerBackpack> callback, boolean runCbOnMainThread) {
        ItemMeta im;
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        im = item.getItemMeta();
        if (!im.hasLore()) {
            return;
        }

        Optional<String> bUuid = getBackpackUUID(im);
        if (bUuid.isPresent()) {
            Slimefun.getDatabaseManager()
                    .getProfileDataController()
                    .getBackpackAsync(bUuid.get(), new IAsyncReadCallback<>() {
                        @Override
                        public boolean runOnMainThread() {
                            return runCbOnMainThread;
                        }

                        @Override
                        public void onResult(PlayerBackpack result) {
                            callback.accept(result);
                        }
                    });

            return;
        }

        // Old backpack item
        OptionalInt id = OptionalInt.empty();
        String uuid = "";
        for (String line : im.getLore()) {
            if (line.startsWith("§7ID: ") && line.indexOf('#') != -1) {
                String[] splitLine = line.split("#");

                if (TextUtils.hasNumber(splitLine[1])) {
                    uuid = splitLine[0].replace("§7ID: ", "");
                    id = OptionalInt.of(Integer.parseInt(splitLine[1]));
                }
            }
        }

        if (id.isPresent()) {
            int number = id.getAsInt();
            Slimefun.getDatabaseManager()
                    .getProfileDataController()
                    .getBackpackAsync(
                            Bukkit.getOfflinePlayer(UUID.fromString(uuid)), number, new IAsyncReadCallback<>() {
                                @Override
                                public boolean runOnMainThread() {
                                    return runCbOnMainThread;
                                }

                                @Override
                                public void onResult(PlayerBackpack result) {
                                    im.getPersistentDataContainer()
                                            .set(KEY_BACKPACK_UUID, PersistentDataType.STRING, result.uuid.toString());
                                    item.setItemMeta(im);
                                    // TODO: upgrade lore
                                    callback.accept(result);
                                }
                            });
        }
    }

    public static Optional<String> getBackpackUUID(ItemMeta meta) {
        if (meta == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(meta.getPersistentDataContainer().get(KEY_BACKPACK_UUID, PersistentDataType.STRING));
    }

    public static Optional<String> getOwnerUUID(ItemMeta meta) {
        if (meta == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(meta.getPersistentDataContainer().get(KEY_OWNER_UUID, PersistentDataType.STRING));
    }

    public static OptionalInt getBackpackID(ItemMeta meta) {
        if (meta == null) {
            return OptionalInt.empty();
        }

        for (String line : meta.getLore()) {
            if (line.startsWith("§7ID: ") && line.contains("#")) {
                try {
                    return OptionalInt.of(Integer.parseInt(
                            CommonPatterns.HASH.split(line.replace("§7ID: ", ""))[1]));
                } catch (NumberFormatException e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
        }

        return OptionalInt.empty();
    }

    public static void setItemPdc(ItemStack item, String bpUuid, String ownerUuid) {
        ItemMeta meta = item.getItemMeta();
        setPdc(meta, bpUuid, ownerUuid);
        item.setItemMeta(meta);
    }

    public static void bindItem(ItemStack item, PlayerBackpack bp) {
        ItemMeta meta = item.getItemMeta();
        setPdc(meta, bp.uuid.toString(), bp.owner.getUniqueId().toString());
        setItem(meta, bp);
        item.setItemMeta(meta);
    }

    public static void setItemDisplayInfo(ItemStack item, PlayerBackpack bp) {
        ItemMeta meta = item.getItemMeta();
        setItem(meta, bp);
        item.setItemMeta(meta);
    }

    public static boolean isOwnerOnline(ItemMeta meta) {
        return true;
    }

    private static void setPdc(ItemMeta meta, String bpUuid, String ownerUuid) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(PlayerBackpack.KEY_BACKPACK_UUID, PersistentDataType.STRING, bpUuid);
        pdc.set(PlayerBackpack.KEY_OWNER_UUID, PersistentDataType.STRING, ownerUuid);
    }

    private static void setItem(ItemMeta meta, PlayerBackpack bp) {
        List<Component> lore = meta.lore();
        for (int i = 0; i < lore.size(); i++) {
            Component line = lore.get(i);
            if (line instanceof TextComponent tc) {
                if (COLORED_LORE_OWNER.equals(tc.content())) {
                    lore.set(i, new QsTextComponentImpl(PLAIN_LORE_OWNER + bp.owner.getName())
                            .color(NamedTextColor.GRAY));
                    break;
                }
            }
        }
        meta.lore(lore);

        if (bp.name.isEmpty() || bp.name.isBlank()) {
            return;
        }
        meta.setDisplayName(ChatColors.color(bp.name));
    }

    public PlayerBackpack(
            OfflinePlayer owner, UUID uuid, String name, int id, int size, @Nullable ItemStack[] contents) {
        this.owner = owner;
        this.uuid = uuid;
        this.name = name;
        this.id = id;
        this.size = size;
        inventory = newInv();

        if (contents == null) {
            return;
        }

        inventory.setContents(contents);
    }

    /**
     * This method returns the {@link Inventory} of this {@link PlayerBackpack}
     *
     * @return The {@link Inventory} of this {@link PlayerBackpack}
     */

    public Inventory getInventory() {
        return inventory;
    }

    /**
     * This will open the {@link Inventory} of this backpack to every {@link Player}
     * that was passed onto this method.
     * <p>
     * 二进制兼容
     *
     * @param p The player who this Backpack will be shown to
     */
    public void open(Player p) {
        if (isInvalid) {
            return;
        }
        p.openInventory(inventory);
    }

    /**
     * This will change the current size of this Backpack to the specified size.
     *
     * @param size
     *            The new size for this Backpack
     */
    public void setSize(int size) {
        this.size = size;
        updateInv();
        Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInfo(this);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
        updateInv();
        Slimefun.getDatabaseManager().getProfileDataController().saveBackpackInfo(this);
    }

    public void markInvalid() {
        isInvalid = true;
        InventoryUtil.closeInventory(this.inventory);
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    /**
     * Construct a new backpack inventory.
     * <p>
     * Warning: You should **manually** update inventory contents!
     *
     * @return new {@link Inventory}
     */
    private Inventory newInv() {
        return Bukkit.createInventory(
                this, size, (name.isEmpty() ? "背包" : name + "§r") + " [大小 " + size + "]");
    }

    private void updateInv() {
        InventoryUtil.closeInventory(this.inventory);
        Inventory inv = newInv();
        inv.setContents(this.inventory.getContents());
        this.inventory.clear();
        this.inventory = inv;
        setInventory(inv);
    }
}
