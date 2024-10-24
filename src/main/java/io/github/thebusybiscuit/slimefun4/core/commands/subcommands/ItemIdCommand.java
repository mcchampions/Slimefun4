package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.message.QsTextComponentImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class ItemIdCommand extends SubCommand {
    final Component msg = Component.text("该物品的ID为: ");
    protected ItemIdCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "id", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player p) {
            if (sender.hasPermission("slimefun.command.id")) {
                var item = p.getInventory().getItemInMainHand();
                if (item.getType() != Material.AIR) {
                    var sfItem = SlimefunItem.getByItem(item);
                    if (sfItem != null) {
                        String sfId = sfItem.getId();
                        Component idMsg = new QsTextComponentImpl(sfId)
                                        .color(NamedTextColor.GRAY);
                        idMsg = idMsg.clickEvent(ClickEvent.copyToClipboard(sfId));
                        sender.sendMessage(msg.append(idMsg));
                    } else {
                        Slimefun.getLocalization().sendMessage(sender, "messages.invalid-item-in-hand", true);
                    }
                } else {
                    sender.sendMessage("§b请将需要查看的物品拿在主手!");
                }
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }
}
