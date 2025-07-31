package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ClearDataCommand extends SubCommand {
    public static final List<String> ValidClearTypes = List.of("block", "oil");

    public ClearDataCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "cleardata", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("slimefun.command.cleardata") && !(sender instanceof ConsoleCommandSender)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }
        if (args.length == 3) {
            Slimefun.getLocalization().sendMessage(sender, "commands.cleardata.confirm", true);
            return;
        }
        if (args.length < 3 || !"confirm".equalsIgnoreCase(args[3])) {
            Slimefun.getLocalization()
                    .sendMessage(
                            sender,
                            "messages.usage",
                            true,
                            msg -> msg.replace("%usage%", "/sf cleardata <World> <Type>"));
            return;
        }
        List<World> worlds = new ArrayList<>();
        List<String> clearTypes = new ArrayList<>();
        String block = Slimefun.getLocalization().getMessage("commands.cleardata.block");
        String oil = Slimefun.getLocalization().getMessage("commands.cleardata.oil");
        BlockDataController controller = Slimefun.getDatabaseManager().getBlockDataController();
        if ("*".equals(args[1])) {
            worlds.addAll(Bukkit.getWorlds());
        } else {
            World toAdd = Bukkit.getWorld(args[1]);
            if (toAdd == null) {
                Slimefun.getLocalization().sendMessage(sender, "commands.cleardata.worldNotFound", true);
                return;
            }

            worlds.add(toAdd);
        }

        if ("*".equals(args[2])) {
            clearTypes.addAll(ValidClearTypes);
        } else if (ValidClearTypes.contains(args[2])) {
            clearTypes.add(args[2]);
        }

        for (World world : worlds) {
            for (String cleartype : clearTypes) {
                if ("block".equals(cleartype)) {
                    controller.removeAllDataInWorldAsync(
                            world,
                            () -> Slimefun.runSync(() -> Slimefun.getLocalization()
                                    .sendMessage(sender, "commands.cleardata.success", true, msg -> msg.replace(
                                                    "{0}", world.getName())
                                            .replace("{1}", block))));
                    continue;
                }
                if ("oil".equals(cleartype)) {
                    GEOResource oilresource = null;
                    for (GEOResource resource :
                            Slimefun.getRegistry().getGEOResources().values()) {
                        if (resource.getKey()
                                .toString()
                                .equals(new NamespacedKey(Slimefun.instance(), "oil").toString())) {
                            oilresource = resource;
                        }
                    }
                    controller.removeFromAllChunkInWorldAsync(
                            world,
                            oilresource.getKey().toString().replace(":", "-"),
                            () -> Slimefun.runSync(() -> Slimefun.getLocalization()
                                    .sendMessage(sender, "commands.cleardata.success", true, msg -> msg.replace(
                                                    "{0}", world.getName())
                                            .replace("{1}", oil))));
                }
            }
        }
    }

    @Override
    public String getDescription() {
        return "commands.cleardata.description";
    }
}
