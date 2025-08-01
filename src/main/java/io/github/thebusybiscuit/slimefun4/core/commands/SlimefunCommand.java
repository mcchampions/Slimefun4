package io.github.thebusybiscuit.slimefun4.core.commands;

import io.github.thebusybiscuit.slimefun4.core.commands.subcommands.SlimefunSubCommands;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import me.qscbm.slimefun4.listeners.AsyncTabCompleteListener;
import me.qscbm.slimefun4.utils.QsConstants;
import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

/**
 * This {@link CommandExecutor} holds the functionality of our {@code /slimefun} command.
 *
 * @author TheBusyBiscuit
 */
public class SlimefunCommand implements CommandExecutor, Listener {
    public static Set<String> COMMAND_ALIASES;
    @Getter
    private boolean registered;
    @Getter
    private final Slimefun plugin;
    private final List<SubCommand> commands = new ArrayList<>();
    @Getter
    private final Map<SubCommand, Integer> commandUsage = new HashMap<>();

    /**
     * Creates a new instance of {@link SlimefunCommand}
     *
     * @param plugin The instance of our {@link Slimefun}
     */
    public SlimefunCommand(Slimefun plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("DataFlowIssue")
    public void register() {
        if (registered) {
            return;
        }
        registered = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("slimefun").setExecutor(this);
        COMMAND_ALIASES = plugin.getServer().getPluginCommand("slimefun").getAliases().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        Slimefun.logger().info("已加载" + COMMAND_ALIASES.size() + "个命令别名:" + COMMAND_ALIASES);
        commands.addAll(SlimefunSubCommands.getAllCommands(this));
        plugin.getServer().getPluginManager().registerEvents(new AsyncTabCompleteListener(), plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String argsStr = String.join(" ", args);
        args = TextUtils.tokenize(argsStr).toArray(QsConstants.EMPTY_STRINGS);
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
        sender.sendMessage("§aSlimefun §2v" + Slimefun.getVersion());
        sender.sendMessage("");

        for (SubCommand cmd : commands) {
            if (!cmd.isHidden()) {
                sender.sendMessage("§3/sf " + cmd.getName() + " §b" + cmd.getDescription(sender));
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
