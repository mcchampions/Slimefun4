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
import org.bukkit.inventory.ItemStack;

class ItemIdCommand extends SubCommand {
    final Component msg = Component.text("该物品的ID为: ");

    protected ItemIdCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "id", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
            return;
        }
        if (!sender.hasPermission("slimefun.command.id")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            sender.sendMessage("§b请将需要查看的物品拿在主手!");
            return;
        }
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        if (sfItem == null) {
            Slimefun.getLocalization().sendMessage(sender, "messages.invalid-item-in-hand", true);
            return;
        }
        String sfId = sfItem.getId();
        Component idMsg = new QsTextComponentImpl(sfId)
                .color(NamedTextColor.GRAY);
        idMsg = idMsg.clickEvent(ClickEvent.copyToClipboard(sfId));
        sender.sendMessage(msg.append(idMsg));
    }
}
