package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import net.guizhanss.slimefun4.utils.ChatUtils;
import org.bukkit.command.CommandSender;

@Deprecated
public class MigrateCommand extends SubCommand {
    MigrateCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "migrate", true);
    }


    @Override
    protected String getDescription() {
        return "废弃指令";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        ChatUtils.sendMessage(sender, "你小子怎么执行这个命令的");
    }
}
