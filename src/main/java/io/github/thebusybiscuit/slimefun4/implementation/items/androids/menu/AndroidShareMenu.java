package io.github.thebusybiscuit.slimefun4.implementation.items.androids.menu;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.ProgrammableAndroid;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Level;

/**
 * The {@link AndroidShareMenu} is responsibility to modify trusted users
 * in {@link ProgrammableAndroid}.
 *
 * @author StarWishsama
 * @see ProgrammableAndroid
 */
public final class AndroidShareMenu {
    private static final int DISPLAY_START_SLOT = 9;
    private static final NamespacedKey BLOCK_INFO_KEY = new NamespacedKey(Slimefun.instance(), "share-users");
    private static final int SHARED_USERS_LIMIT = 15;

    private AndroidShareMenu() {
    }

    /**
     * Open a share menu for player.
     *
     * @param p player
     * @param b android
     */
    public static void openShareMenu(Player p, Block b) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage("android.access-manager.title"));

        menu.setEmptySlotsClickable(false);

        List<String> users = getTrustedUsers(b);

        // Draw background start
        for (int i = 0; i < 9; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(i, (pl, slot, item, action) -> false);
        }

        for (int i = 45; i < 54; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(i, (pl, slot, item, action) -> false);
        }
        // Draw background end

        // Add trusted player slot
        menu.addItem(
                0,
                new CustomItemStack(
                        HeadTexture.SCRIPT_UP.getAsItemStack(),
                        Slimefun.getLocalization().getMessage("android.access-manager.menu.add-player-title"),
                        Slimefun.getLocalization().getMessage("android.access-manager.menu.add-player")));
        menu.addMenuClickHandler(0, (p1, slot, item, action) -> {
            p1.closeInventory();

            if (users.size() >= SHARED_USERS_LIMIT) {
                Slimefun.getLocalization().sendMessage(p1, "android.access-manager.messages.reach-limit");
                return false;
            }

            Slimefun.getLocalization().sendMessage(p1, "android.access-manager.messages.input");

            ChatUtils.awaitInput(p1, message -> {
                Player target = Bukkit.getPlayerExact(message);

                if (target == null) {
                    Slimefun.getLocalization()
                            .sendMessage(
                                    p1,
                                    "android.access-manager.messages.cannot-find-player",
                                    msg -> msg.replace("%player%", message));
                } else {
                    addPlayer(p1, target, b, users);
                }
            });
            return false;
        });

        // Display added trusted player(s)
        if (!users.isEmpty()) {
            for (int index = 0; index < users.size(); index++) {
                int slot = index + DISPLAY_START_SLOT;
                OfflinePlayer current = Bukkit.getOfflinePlayer(UUID.fromString(users.get(index)));
                menu.addItem(
                        slot,
                        new CustomItemStack(
                                PlayerHead.getItemStack(current),
                                "&b" + current.getName(),
                                Slimefun.getLocalization().getMessage("android.access-manager.menu.delete-player")));
                menu.addMenuClickHandler(slot, (p1, slot1, item, action) -> {
                    if (!action.isRightClicked() && !action.isShiftClicked()) {
                        removePlayer(p1, current, b, users);
                    }
                    return false;
                });
            }
        }

        menu.open(p);
    }

    private static void addPlayer(Player owner, OfflinePlayer p, Block android, List<String> users) {
        if (users.contains(p.getUniqueId().toString())) {
            Slimefun.getLocalization()
                    .sendMessage(
                            owner,
                            "android.access-manager.messages.is-trusted-player",
                            msg -> msg.replace("%player%", p.getName()));
        } else if (owner.getUniqueId() == p.getUniqueId()) {
            Slimefun.getLocalization().sendMessage(owner, "android.access-manager.messages.cannot-add-yourself");
        } else {
            users.add(p.getUniqueId().toString());
            Slimefun.getLocalization()
                    .sendMessage(
                            owner,
                            "android.access-manager.messages.add-success",
                            msg -> msg.replace("%player%", p.getName()));

            setSharedUserData(android.getState(), users.toString());
        }
    }

    private static void removePlayer(Player owner, OfflinePlayer p, Block android, List<String> users) {
        if (users.contains(p.getUniqueId().toString())) {
            users.remove(p.getUniqueId().toString());
            Slimefun.getLocalization()
                    .sendMessage(
                            owner,
                            "android.access-manager.messages.delete-success",
                            msg -> msg.replace("%player%", p.getName()));

            setSharedUserData(android.getState(), users.toString());
        } else {
            Slimefun.getLocalization()
                    .sendMessage(
                            owner,
                            "android.access-manager.messages.is-not-trusted-player",
                            msg -> msg.replace("%player%", p.getName()));
        }
    }

    /**
     * Parse trusted player list raw string to List.
     *
     * @param value list raw string
     * @return parse trusted player list
     */
    private static List<String> parseBlockInfoToList(String value) {
        String replacedText = value.replace("[", "").replace("]", "");

        if (replacedText.isEmpty()) {
            return Collections.emptyList();
        } else {
            return TextUtils.split(replacedText,", ");
        }
    }

    /**
     * Get a trusted users list of specific android.
     *
     * @param b the block of android
     * @return trusted users list
     */
    public static List<String> getTrustedUsers(Block b) {
        Optional<String> trustUsers = getSharedUserData(b.getState());

        // Checks for old Android
        if (trustUsers.isEmpty()) {
            List<String> emptyUsers = new ArrayList<>();
            setSharedUserData(b.getState(), String.valueOf(emptyUsers));
            return emptyUsers;
        }

        return parseBlockInfoToList(trustUsers.get());
    }

    /**
     * Checks user is in trusted users list.
     *
     * @param b    the block of Android
     * @param uuid user's UUID
     * @return user trusted status
     */
    public static boolean isTrustedUser(Block b, UUID uuid) {
        Optional<String> trustUsers = getSharedUserData(b.getState());

        return trustUsers.map(s -> s.contains(uuid.toString())).orElse(false);
    }

    private static void setSharedUserData(BlockState state, String value) {
        if (!(state instanceof TileState)) {
            return;
        }

        try {
            PersistentDataContainer container = ((TileState) state).getPersistentDataContainer();
            container.set(BLOCK_INFO_KEY, PersistentDataType.STRING, value);
            state.update();
        } catch (Exception x) {
            Slimefun.logger()
                    .log(Level.SEVERE, "An Exception was thrown while trying to set Persistent Data for a Android", x);
        }
    }

    private static Optional<String> getSharedUserData(BlockState state) {
        if (!(state instanceof TileState)) {
            return Optional.empty();
        }

        PersistentDataContainer container = ((TileState) state).getPersistentDataContainer();

        return Optional.ofNullable(container.get(BLOCK_INFO_KEY, PersistentDataType.STRING));
    }
}
