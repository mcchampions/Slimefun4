package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * {@link ChargeCommand} adds an in game command which charges any {@link Rechargeable}
 * item to maximum charge, defined by {@link Rechargeable#getMaxItemCharge(ItemStack)}.
 *
 * @author FluffyBear
 */
class ChargeCommand extends SubCommand {
    ChargeCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "charge", false);
    }

    @Override
    protected String getDescription() {
        return "commands.charge.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
            return;
        }
        if (!sender.hasPermission("slimefun.command.charge")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        SlimefunItem slimefunItem = SlimefunItem.getByItem(item);

        if (!(slimefunItem instanceof Rechargeable rechargeableItem)) {
            Slimefun.getLocalization().sendMessage(sender, "commands.charge.not-rechargeable", true);
            return;
        }
        rechargeableItem.setItemCharge(item, rechargeableItem.getMaxItemCharge(item));
        Slimefun.getLocalization().sendMessage(sender, "commands.charge.charge-success", true);
    }
}
