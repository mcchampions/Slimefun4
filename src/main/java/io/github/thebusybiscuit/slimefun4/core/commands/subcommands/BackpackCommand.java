package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import com.xzavier0722.mc.plugin.slimefun4.storage.callback.IAsyncReadCallback;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.RestoredBackpack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.qscbm.slimefun4.message.QsTextComponentImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This command that allows for backpack retrieval in the event they are lost.
 * The command accepts a name and id, if those match up it spawns a Medium Backpack
 * with the correct lore set in the sender's inventory.
 *
 * @author Sfiguz7
 * @see RestoredBackpack
 */
class BackpackCommand extends SubCommand {
    private static final int DISPLAY_START_SLOT = 9;

    BackpackCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "backpack", false);
    }

    @Override
    protected String getDescription() {
        return "commands.backpack.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
            return;
        }
        if (!sender.hasPermission("slimefun.command.backpack")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }
        if (args.length < 1) {
            Slimefun.getLocalization()
                    .sendMessage(
                            sender,
                            "messages.usage",
                            true,
                            msg -> msg.replace("%usage%", "/sf backpack (玩家名/UUID)"));
            return;
        }

        if (args.length == 1) {
            openBackpackMenu(player, player);
            Slimefun.getLocalization().sendMessage(player, "commands.backpack.searching");
            return;
        }

        if (!sender.hasPermission("slimefun.command.backpack.other")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }
        IAsyncReadCallback<UUID> callback = new IAsyncReadCallback<>() {
            @Override
            public void onResult(UUID result) {
                if (!player.isOnline()) {
                    return;
                }
                openBackpackMenu(Bukkit.getOfflinePlayer(result), player);
            }

            @Override
            public void onResultNotFound() {
                Slimefun.getLocalization()
                        .sendMessage(player, "commands.backpack.backpack-does-not-exist");
            }
        };
        if (args[1].contains("-")) {
            UUID uuid = UUID.fromString(args[1]);
            Slimefun.getDatabaseManager()
                    .getProfileDataController()
                    .isExistsUuidAsync(uuid, callback);
        } else {
            Slimefun.getDatabaseManager()
                    .getProfileDataController()
                    .getPlayerUuidAsync(args[1], callback);
        }
        Slimefun.getLocalization().sendMessage(player, "commands.backpack.searching");
    }

    public static void openBackpackMenu(OfflinePlayer owner, Player p) {
        Slimefun.getDatabaseManager()
                .getProfileDataController()
                .getBackpacksAsync(owner.getUniqueId().toString(), new IAsyncReadCallback<>() {
                    @Override
                    public boolean runOnMainThread() {
                        return true;
                    }

                    @Override
                    public void onResult(Set<PlayerBackpack> result) {
                        if (!p.isConnected()) {
                            return;
                        }
                        showBackpackMenu(owner, p, result, 1);
                    }

                    @Override
                    public void onResultNotFound() {
                        Slimefun.getLocalization().sendMessage(p, "commands.backpack.backpack-does-not-exist");
                    }
                });
    }

    private static void showBackpackMenu(OfflinePlayer owner, Player p, Set<PlayerBackpack> result, int page) {
        ChestMenu menu = new ChestMenu(owner.getName() + " 拥有的背包列表");
        menu.setEmptySlotsClickable(false);

        int pages = result.size() / 36;

        // Draw background start
        for (int i = 0; i < 9; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(i, (pl, slot, item, action) -> false);
        }

        List<PlayerBackpack> bps = new ArrayList<>(result);
        // max display 36 backpacks per page
        for (int i = 0; i <= 36; i++) {
            int slot = DISPLAY_START_SLOT + i;
            int index = i + 36 * (page - 1);
            if (index >= bps.size()) {
                break;
            }
            PlayerBackpack bp = bps.get(index);

            ItemStack visualBackpack = SlimefunItems.RESTORED_BACKPACK.clone();
            ItemMeta im = visualBackpack.getItemMeta();
            im.displayName(new QsTextComponentImpl(bp.getName().isEmpty() ? "背包 #" + bp.getId() : bp.getName()));
            ArrayList<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(new QsTextComponentImpl("左键 获取此背包").color(NamedTextColor.GREEN));
            im.lore(lore);
            visualBackpack.setItemMeta(im);

            menu.addItem(slot, visualBackpack);
            menu.addMenuClickHandler(slot, (p1, slot1, item, action) -> {
                if (!action.isRightClicked() && !action.isShiftClicked() && p1.getUniqueId() == p.getUniqueId()) {
                    ItemStack restoreBp = SlimefunItems.RESTORED_BACKPACK.clone();
                    PlayerBackpack.bindItem(restoreBp, bp);
                    p1.getInventory().addItem(restoreBp);
                    Slimefun.getLocalization().sendMessage(p1, "commands.backpack.restored-backpack-given");
                }

                return false;
            });
        }

        for (int i = 45; i < 54; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(i, (pl, slot, item, action) -> false);
        }

        // Draw background end

        if (pages > 1) {
            menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
            menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
                int next = page - 1;

                if (next > 0) {
                    showBackpackMenu(owner, p, result, next);
                }

                return false;
            });

            if (page < pages) {
                menu.addItem(52, ChestMenuUtils.getNextButton(p, page, pages));
                menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
                    int next = page + 1;

                    if (next <= pages) {
                        showBackpackMenu(owner, p, result, next);
                    }

                    return false;
                });
            }
        }

        menu.open(p);
    }
}
