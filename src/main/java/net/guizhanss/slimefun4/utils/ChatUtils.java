package net.guizhanss.slimefun4.utils;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import me.qscbm.slimefun4.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 聊天相关方法
 * @author ybw0014
 */
public class ChatUtils {
    private ChatUtils() {}

    /**
     * 发送带 Slimefun 前缀的消息
     * @param sender 消息接收人
     * @param message 消息
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColors.color(Slimefun.getLocalization().getChatPrefix() + message));
        } else {
            sender.sendMessage(TextUtils.toPlainText(Slimefun.getLocalization().getChatPrefix() + message));
        }
    }

    /**
     * 发送带 Slimefun 前缀的消息
     * @param sender 消息接收人
     * @param message 消息
     * @param function 对消息进行处理的{@link Function}
     */
    public static void sendMessage(CommandSender sender, String message, UnaryOperator<String> function) {
        sendMessage(sender, function.apply(message));
    }
}
