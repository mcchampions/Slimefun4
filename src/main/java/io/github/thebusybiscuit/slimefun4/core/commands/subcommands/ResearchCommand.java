package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.bakedlibs.dough.common.PlayerList;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.Optional;
import java.util.function.UnaryOperator;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

class ResearchCommand extends SubCommand {
    private static final String PLACEHOLDER_PLAYER = "%player%";
    private static final String PLACEHOLDER_RESEARCH = "%research%";

    ResearchCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "research", false);
    }

    @Override
    protected String getDescription() {
        return "commands.research.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        // Check if researching is even enabled
        if (!Slimefun.getConfigManager().isResearchingEnabled()) {
            Slimefun.getLocalization().sendMessage(sender, "messages.researching-is-disabled");
            return;
        }

        if (args.length < 3) {
            Slimefun.getLocalization()
                    .sendMessage(
                            sender,
                            "messages.usage",
                            true,
                            msg -> msg.replace("%usage%", "/sf research <Player> <all/reset/Research>"));
            return;
        }
        if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission("slimefun.cheat.researches")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }
        Optional<Player> player = PlayerList.findByName(args[1]);

        if (player.isEmpty()) {
            Slimefun.getLocalization()
                    .sendMessage(
                            sender,
                            "messages.not-online",
                            true,
                            msg -> msg.replace(PLACEHOLDER_PLAYER, args[1]));
            return;
        }
        Player p = player.get();

        // Getting the PlayerProfile async
        PlayerProfile.get(p, profile -> {
            if ("all".equalsIgnoreCase(args[2])) {
                researchAll(sender, profile, p);
                return;
            }
            if ("reset".equalsIgnoreCase(args[2])) {
                reset(profile, p);
                return;
            }
            giveResearch(sender, p, args[2]);
        });
    }

    private static void giveResearch(CommandSender sender, Player p, String input) {
        Optional<Research> research = getResearchFromString(input);

        if (research.isEmpty()) {
            Slimefun.getLocalization()
                    .sendMessage(
                            sender, "messages.invalid-research", true, msg -> msg.replace(PLACEHOLDER_RESEARCH, input));
            return;
        }
        research.get().unlock(p, true, player -> {
            UnaryOperator<String> variables = msg -> msg.replace(PLACEHOLDER_PLAYER, player.getName())
                    .replace(PLACEHOLDER_RESEARCH, research.get().getName(player));
            Slimefun.getLocalization().sendMessage(player, "messages.give-research", true, variables);
        });
    }

    private static void researchAll(CommandSender sender, PlayerProfile profile, Player p) {
        for (Research res : Slimefun.getRegistry().getResearches()) {
            if (!profile.hasUnlocked(res)) {
                Slimefun.getLocalization().sendMessage(sender, "messages.give-research", true, msg -> msg.replace(
                                PLACEHOLDER_PLAYER, p.getName())
                        .replace(PLACEHOLDER_RESEARCH, res.getName(p)));
            }

            res.unlock(p, true);
        }
    }

    private static void reset(PlayerProfile profile, Player p) {
        for (Research research : Slimefun.getRegistry().getResearches()) {
            profile.setResearched(research, false);
        }

        Slimefun.getLocalization()
                .sendMessage(p, "commands.research.reset", true, msg -> msg.replace(PLACEHOLDER_PLAYER, p.getName()));
    }

    private static Optional<Research> getResearchFromString(String input) {
        for (Research research : Slimefun.getRegistry().getResearches()) {
            if (research.getKey().toString().equalsIgnoreCase(input)) {
                return Optional.of(research);
            }
        }

        for (Research research : Slimefun.getRegistry().getResearches()) {
            if (research.getNormalName().equals(input)) {
                return Optional.of(research);
            }
        }

        return Optional.empty();
    }
}
