package me.qscbm.slimefun4.listeners;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunTabCompleter;
import me.qscbm.slimefun4.utils.QuotedStringTokenizer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncTabCompleteListener implements Listener {
    public static final Pattern COMMAND_PREFIX = Pattern.compile("slimefun:");

    @EventHandler(ignoreCancelled = true)
    public static void onAsyncTabCompleteEvent(AsyncTabCompleteEvent e) {
        if (!e.isCommand()) {
            return;
        }
        String buffer = e.getBuffer();
        if (buffer.isEmpty()) {
            return;
        }
        if (buffer.charAt(0) == '/') {
            buffer = buffer.substring(1);
        }

        int firstPlace = buffer.indexOf(' ');
        if (firstPlace < 0) {
            return;
        }
        String commandLabel = buffer.substring(0, firstPlace).toLowerCase();
        Matcher matcher = COMMAND_PREFIX.matcher(commandLabel);
        if (matcher.find()) {
            if (matcher.start() == 0) {
                commandLabel = commandLabel.substring(matcher.end());
            }
        }
        if (!SlimefunCommand.COMMAND_ALIASES.contains(commandLabel) && !"slimefun".equals(commandLabel)) {
            return;
        }
        List<String> args = new QuotedStringTokenizer(buffer.substring(firstPlace + 1)).tokenize(false);
        List<String> suggests = SlimefunTabCompleter.onTabComplete(args);
        e.setCompletions(suggests);
        e.setHandled(true);
    }
}
