package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.qscbm.slimefun4.commands.CalcCommand;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class holds the implementations of every {@link SubCommand}.
 * The implementations themselves are package-private, this class only provides
 * a static setup method
 *
 * @author TheBusyBiscuit
 *
 */
public final class SlimefunSubCommands {
    private SlimefunSubCommands() {}

    public static Collection<SubCommand> getAllCommands(SlimefunCommand cmd) {
        Slimefun plugin = cmd.getPlugin();
        List<SubCommand> commands = new LinkedList<>();

        commands.add(new HelpCommand(plugin, cmd));
        commands.add(new VersionsCommand(plugin, cmd));
        commands.add(new CheatCommand(plugin, cmd));
        commands.add(new GuideCommand(plugin, cmd));
        commands.add(new GiveCommand(plugin, cmd));
        commands.add(new ResearchCommand(plugin, cmd));
        commands.add(new StatsCommand(plugin, cmd));
        commands.add(new TeleporterCommand(plugin, cmd));
        commands.add(new OpenGuideCommand(plugin, cmd));
        commands.add(new SearchCommand(plugin, cmd));
        commands.add(new BackpackCommand(plugin, cmd));
        commands.add(new ChargeCommand(plugin, cmd));
        commands.add(new ItemIdCommand(plugin, cmd));
        commands.add(new BlockDataCommand(plugin, cmd));
        commands.add(new BanItemCommand(plugin, cmd));
        commands.add(new UnbanItemCommand(plugin, cmd));
        commands.add(new ClearDataCommand(plugin, cmd));
        commands.add(new CalcCommand(plugin,cmd));
        return commands;
    }
}
