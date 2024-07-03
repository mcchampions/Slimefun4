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

    protected DebugCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "debug", true);
    }

    @Override
    protected String getDescription() {
        return "commands.debug.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("slimefun.command.debug")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }

        if (args.length == 1) {
            String currentCase = String.join(", ", Debug.getTestCase());
            if (!currentCase.isEmpty()) {
                Slimefun.getLocalization()
                        .sendMessage(
                                sender, "commands.debug.current", true, msg -> msg.replace("%test_case%", currentCase));
            } else {
                Slimefun.getLocalization().sendMessage(sender, "commands.debug.none-running", true);
            }
            return;
        }

        String test = args[1];

        switch (test.toLowerCase()) {
            case "disable", "off" -> {
                if (Slimefun.getSQLProfiler().isProfiling()) {
                    Slimefun.getSQLProfiler().stop();
                }

                Debug.disableTestCase();
                Slimefun.getLocalization().sendMessage(sender, "commands.debug.disabled");
            }
            default -> {
                if (TestCase.DATABASE.toString().equals(test)) {
                    if (Slimefun.getSQLProfiler().isProfiling()) {
                        Slimefun.getLocalization().sendMessage(sender, "sf-cn.timings.running");
                    } else {
                        Slimefun.getSQLProfiler().start();
                        Slimefun.getSQLProfiler().subscribe(sender);
                        Slimefun.getLocalization().sendMessage(sender, "sf-cn.timings.started");
                    }
                }
                Debug.addTestCase(test);
                Slimefun.getLocalization()
                        .sendMessage(sender, "commands.debug.running", msg -> msg.replace("%test%", test));
            }
        }
    }
}
