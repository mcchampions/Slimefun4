package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.command.CommandSender;

class HelpCommand extends SubCommand {
    HelpCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "help", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        cmd.sendHelp(sender);
    }
}
