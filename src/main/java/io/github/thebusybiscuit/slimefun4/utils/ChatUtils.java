package io.github.thebusybiscuit.slimefun4.utils;

import io.github.bakedlibs.dough.chat.ChatInput;
import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.Locale;
import java.util.function.Consumer;

import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This utility class contains a few static methods that are all about {@link String} manipulation
 * or sending a {@link String} to a {@link Player}.
 *
 * @author TheBusyBiscuit
 *
 */
public final class ChatUtils {
    private ChatUtils() {}

    public static void sendURL(CommandSender sender, String url) {
        // If we get access to the URL prompt one day, we can just prompt the link to the Player that
        // way.
        sender.sendMessage("");
        Slimefun.getLocalization().sendMessage(sender, "messages.link-prompt", false);
        sender.sendMessage("§7§o" + url);
        sender.sendMessage("");
    }

    public static String removeColorCodes(String string) {
        return TextUtils.toPlainText(ChatColors.color(string));
    }

    public static String crop(ChatColor color, String string) {
        if (TextUtils.toPlainText(color + string).length() > 19) {
            return (color + TextUtils.toPlainText(string)).substring(0, 18) + "...";
        } else {
            return color + TextUtils.toPlainText(string);
        }
    }

    public static String christmas(String text) {
        return ChatColors.alternating(text, ChatColor.GREEN, ChatColor.RED);
    }

    public static void awaitInput(Player p, Consumer<String> callback) {
        ChatInput.waitForPlayer(Slimefun.instance(), p, callback);
    }

    /**
     * This converts a given {@link String} to a human-friendly version.
     * This can be used to convert enum constants to easier to read words with
     * spaces and upper case word starts.
     * <p>
     * For example:
     * {@code ENUM_CONSTANT: Enum Constant}
     *
     * @param string
     *            The {@link String} to convert
     *
     * @return A human-friendly version of the given {@link String}
     */
    public static String humanize(String string) {
        StringBuilder builder = new StringBuilder();
        String[] segments = string.toLowerCase(Locale.ROOT).split("_");

        builder.append(Character.toUpperCase(segments[0].charAt(0))).append(segments[0].substring(1));

        for (int i = 1; i < segments.length; i++) {
            String segment = segments[i];
            builder.append(' ').append(Character.toUpperCase(segment.charAt(0))).append(segment.substring(1));
        }

        return builder.toString();
    }

    /**
     * This method adds an s to a string if the supplied integer is not 1.
     *
     * @param string
     *      The string to potentially pluralize
     * @param count
     *      The amount of things
     * @return
     *      {@code string} if {@code count} is 1 else {@code string + "s"}
     *      if count is less than 0
     */
    public static String checkPlurality(String string, int count) {
        if (count == 1) {
            return string;
        }
        return string + "s";
    }
}
