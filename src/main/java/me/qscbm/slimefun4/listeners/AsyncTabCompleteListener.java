package me.qscbm.slimefun4.listeners;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunTabCompleter;
import me.qscbm.slimefun4.utils.QuotedStringTokenizer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class AsyncTabCompleteListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onAsyncTabCompleteEvent(AsyncTabCompleteEvent e) {
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
        String[] parts = commandLabel.split(":");
        if (parts.length > 1) {
            if (!parts[0].equals("slimefun")) {
                return;
            }
            commandLabel = parts[1];
        }
        if (!SlimefunCommand.COMMAND_ALIASES.contains(commandLabel) && !commandLabel.equals("slimefun")) {
            return;
        }
        List<String> args = new QuotedStringTokenizer(buffer.substring(firstPlace + 1)).tokenize(false);
        List<String> suggests = SlimefunTabCompleter.onTabComplete(args);
        e.setCompletions(suggests);
        e.setHandled(true);
    }
}
