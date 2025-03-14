package io.github.thebusybiscuit.slimefun4.core.commands;

import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.entity.Player;

/**
 * This class represents a {@link SubCommand}, it is a {@link Command} that starts with
 * {@code /sf ...} and is followed by the name of this {@link SubCommand}.
 *
 * @author TheBusyBiscuit
 *
 * @see SlimefunCommand
 *
 */
public abstract class SubCommand {
    protected final Slimefun plugin;
    protected final SlimefunCommand cmd;

    private final String name;
    private final boolean hidden;

    protected SubCommand(Slimefun plugin, SlimefunCommand cmd, String name, boolean hidden) {
        this.plugin = plugin;
        this.cmd = cmd;

        this.name = name;
        this.hidden = hidden;
    }

    /**
     * This returns the name of this {@link SubCommand}, the name is equivalent to the
     * first argument given to the actual command.
     *
     * @return The name of this {@link SubCommand}
     */

    public final String getName() {
        return name;
    }

    /**
     * This method returns whether this {@link SubCommand} is hidden from the {@link HelpCommand}.
     *
     * @return Whether to hide this {@link SubCommand}
     */
    public final boolean isHidden() {
        return hidden;
    }

    protected void recordUsage(Map<SubCommand, Integer> commandUsage) {
        commandUsage.merge(this, 1, Integer::sum);
    }

    public abstract void onExecute(CommandSender sender, String[] args);

    protected String getDescription() {
        return "commands." + name;
    }

    /**
     * This returns a description for this {@link SubCommand}.
     * If the given {@link CommandSender} is a {@link Player}, the description
     * will be localized with the currently selected {@link Language} of that {@link Player}.
     *
     * @param sender The {@link CommandSender} who requested the description
     * @return A possibly localized description of this {@link SubCommand}
     */
    public String getDescription(CommandSender sender) {
        return Slimefun.getLocalization().getMessage(getDescription());
    }
}
