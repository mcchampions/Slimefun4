package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

import java.util.Collection;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

/**
 * This is our class for the /sf versions subcommand.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 */
class VersionsCommand extends SubCommand {

    VersionsCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "versions", false);
    }

    final int version = NumberUtils.getJavaVersion();


    final String serverSoftware = Bukkit.getName();
    final ComponentBuilder<TextComponent, TextComponent.Builder> component = Component.text()
            .append(Component.text("Slimefun 运行的服务器环境:").color(NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text(serverSoftware).color(NamedTextColor.GREEN))
            .append(Component.text(" " + Bukkit.getVersion()).color(NamedTextColor.DARK_GREEN))
            .appendNewline()
            .append(Component.text("Slimefun ").color(NamedTextColor.GREEN))
            .append(Component.text(Slimefun.getVersion()).color(NamedTextColor.DARK_GREEN))
            .appendNewline()
            .append(Component.text("Java").color(NamedTextColor.GREEN))
            .append(Component.text(version).color(NamedTextColor.DARK_GREEN))
            .appendNewline()
            .appendNewline()
            .append(Component.text("Slimefun检测到的MC版本为" + Slimefun.getMinecraftVersion().getName() + "\n").color(NamedTextColor.RED))
            .appendNewline()
            .append(Component.text("""
                    由 StarWishsama 汉化

                    由 qscbm187531 魔改
                    """).color(NamedTextColor.DARK_RED))
            .append(Component.text("""

                    请不要将此版本信息截图到 Discord/Github 反馈 Bug
                    也不要将此版本信息截图到 汉化版Github 反馈 Bug
                    优先到魔改版本页面反馈
                    """).color(NamedTextColor.GREEN))
            .appendNewline();

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender.hasPermission("slimefun.command.versions") || sender instanceof ConsoleCommandSender) {
            Slimefun.runBukkitTaskAsync(() -> {
                Collection<Plugin> addons = Slimefun.getInstalledAddons();

                if (addons.isEmpty()) {
                    TextComponent c = component.append(Component.text("没有安装任何附属插件"))
                            .color(NamedTextColor.GRAY).build();
                    sender.sendMessage(c);
                    return;
                }
                // @formatter:off

                TextComponent c = component.append(Component.text("安装的附属插件").color(NamedTextColor.GRAY))
                        .append(Component.text("(" + addons.size() + ")").color(NamedTextColor.DARK_GRAY)).build();

                for (Plugin plugin : addons) {
                    String v = plugin.getDescription().getVersion();
                    HoverEvent<Component> hoverEvent;
                    ClickEvent clickEvent = null;
                    TextColor primaryColor;
                    if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
                        primaryColor = NamedTextColor.GREEN;
                        String authors = String.join(", ", plugin.getDescription().getAuthors());

                        if (plugin instanceof SlimefunAddon addon && addon.getBugTrackerURL() != null) {
                            hoverEvent = HoverEvent.showText(Component.text("作者: " + authors)
                                    .append(Component.text("\n> 单击打开反馈页面").color(NamedTextColor.YELLOW)));

                            clickEvent = ClickEvent.openUrl(addon.getBugTrackerURL());
                        } else {
                            hoverEvent = HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.text("作者: " + authors));

                        }
                    } else {
                        primaryColor = NamedTextColor.RED;

                        if (plugin instanceof SlimefunAddon addon && addon.getBugTrackerURL() != null) {
                            hoverEvent = HoverEvent.showText(
                                    Component.text("此插件已被禁用.\n检查后台是否有报错.").color(NamedTextColor.RED)
                                            .append(Component.text("\n> 单击打开反馈页面")
                                                    .color(NamedTextColor.DARK_RED)));

                            if (addon.getBugTrackerURL() != null) {
                                clickEvent = ClickEvent.openUrl(addon.getBugTrackerURL());
                            }
                        } else {
                            hoverEvent = HoverEvent.showText(Component.text("插件已被禁用. 可以看看后台是否有报错."));
                        }
                    }

                    // We need to reset the hover event or it's added to all components
                    c = c.append(Component.text("\n  " + plugin.getName())
                            .color(primaryColor)
                            .hoverEvent(hoverEvent)
                            .clickEvent(clickEvent)
                            .append(Component.text(" v" + v))
                            .appendSpace());
                }
                sender.sendMessage(c);
            });
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
        }
    }
}
