package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class SearchCommand extends SubCommand {
    SearchCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "search", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
            return;
        }
        if (!sender.hasPermission("slimefun.command.search")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }
        if (args.length < 1) {
            Slimefun.getLocalization()
                    .sendMessage(
                            sender,
                            "messages.usage",
                            true,
                            msg -> msg.replace("%usage%", "/sf search <SearchTerm>"));
            return;
        }
        String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        PlayerProfile.get(
                player,
                profile -> SlimefunGuide.openSearch(profile, query, SlimefunGuideMode.SURVIVAL_MODE, true));

    }
}
