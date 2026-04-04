package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.core.debug.Debug;
import io.github.thebusybiscuit.slimefun4.core.debug.TestCase;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import javax.annotation.Nonnull;
import org.bukkit.command.CommandSender;

/**
 * The debug command will allow server owners to get information for us developers.
 * We can put debug messages in the code and they can trigger it for us to see what exactly is going on.
 *
 * @author WalshyDev
 */
public class DebugCommand extends SubCommand {

    protected DebugCommand(@Nonnull Slimefun plugin, @Nonnull SlimefunCommand cmd) {
        super(plugin, cmd, "debug", true);
    }

    @Override
    protected @Nonnull String getDescription() {
        return "commands.debug.description";
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (!sender.hasPermission("slimefun.command.debug")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }

        if (args.length == 1) {
            Slimefun.getLocalization().sendMessage(sender, "commands.debug.none-running", true);
            return;
        }

        String test = args[1];

        switch (test.toLowerCase()) {
            case "disable", "off" -> {
                Slimefun.getLocalization().sendMessage(sender, "commands.debug.disabled");
            }
            default -> {
                if (TestCase.DATABASE.toString().equals(test)) {
                    Slimefun.getLocalization().sendMessage(sender, "sf-cn.timings.started");
                }
                Slimefun.getLocalization()
                        .sendMessage(sender, "commands.debug.running", msg -> msg.replace("%test%", test));
            }
        }
    }
}
