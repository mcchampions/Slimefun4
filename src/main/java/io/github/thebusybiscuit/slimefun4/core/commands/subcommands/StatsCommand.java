package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.bakedlibs.dough.common.PlayerList;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

class StatsCommand extends SubCommand {
    StatsCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "stats", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        // Check if researching is even enabled
        if (!Slimefun.getConfigManager().isResearchingEnabled()) {
            Slimefun.getLocalization().sendMessage(sender, "messages.researching-is-disabled");
            return;
        }

        if (args.length > 1) {
            if (sender.hasPermission("slimefun.stats.others") || sender instanceof ConsoleCommandSender) {
                Optional<Player> player = PlayerList.findByName(args[1]);

                if (player.isPresent()) {
                    PlayerProfile.get(player.get(), profile -> profile.sendStats(sender));
                    return;
                }
                Slimefun.getLocalization()
                        .sendMessage(sender, "messages.not-online", true, msg -> msg.replace("%player%", args[1]));
                return;
            }
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);

            return;
        }
        if (sender instanceof Player player) {
            PlayerProfile.get(player, profile -> profile.sendStats(sender));
            return;
        }
        Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);

    }
}
