package io.github.thebusybiscuit.slimefun4.core.commands;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.core.commands.subcommands.SlimefunSubCommands;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

/**
 * This {@link CommandExecutor} holds the functionality of our {@code /slimefun} command.
 *
 * @author TheBusyBiscuit
 *
 */
public class SlimefunCommand implements CommandExecutor, Listener {
    @Getter
    private boolean registered = false;
    @Getter
    private final Slimefun plugin;
    private final List<SubCommand> commands = new ArrayList<>();
    @Getter
    private final Map<SubCommand, Integer> commandUsage = new HashMap<>();

    /**
     * Creates a new instance of {@link SlimefunCommand}
     *
     * @param plugin
     *            The instance of our {@link Slimefun}
     */
    public SlimefunCommand(Slimefun plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("DataFlowIssue")
    public void register() {
        registered = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getCommand("slimefun").setExecutor(this);
        plugin.getCommand("slimefun").setTabCompleter(new SlimefunTabCompleter(this));
        commands.addAll(SlimefunSubCommands.getAllCommands(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand command : commands) {
                if (args[0].equalsIgnoreCase(command.getName())) {
                    command.recordUsage(commandUsage);
                    command.onExecute(sender, args);
                    return true;
                }
            }
        }

        sendHelp(sender);

        /*
         * We could just return true here, but if there's no subcommands,
         * then something went horribly wrong anyway.
         * This will also stop sonarcloud from nagging about
         * this always returning true...
         */
        return !commands.isEmpty();
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColors.color("&aSlimefun &2v" + Slimefun.getVersion()));
        sender.sendMessage("");

        for (SubCommand cmd : commands) {
            if (!cmd.isHidden()) {
                sender.sendMessage(ChatColors.color("&3/sf " + cmd.getName() + " &b") + cmd.getDescription(sender));
            }
        }
    }

    /**
     * This returns A {@link List} containing every possible {@link SubCommand} of this {@link Command}.
     *
     * @return A {@link List} containing every {@link SubCommand}
     */
    public List<String> getSubCommandNames() {
        return commands.stream().map(SubCommand::getName).collect(Collectors.toList());
    }

    public Set<String> getSubCommandNamesToSet() {
        return commands.stream().map(SubCommand::getName).collect(Collectors.toSet());
    }
}
