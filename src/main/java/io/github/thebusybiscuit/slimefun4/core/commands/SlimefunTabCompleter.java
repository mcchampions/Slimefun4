package io.github.thebusybiscuit.slimefun4.core.commands;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.generator.WorldInfo;

class SlimefunTabCompleter implements TabCompleter {
    private static final int MAX_SUGGESTIONS = 80;

    private final SlimefunCommand command;

    public SlimefunTabCompleter(SlimefunCommand command) {
        this.command = command;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return createReturnList(command.getSubCommandNamesToSet(), args[0]);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("banitem")) {
                return createReturnList(getSlimefunItems(), args[1]);
            } else if (args[0].equalsIgnoreCase("unbanitem")) {
                Set<String> set = Slimefun.getRegistry().getDisabledSlimefunItemsToSet().stream()
                        .map(SlimefunItem::getItemName)
                        .collect(Collectors.toSet());
                return createReturnList(set, args[1]);
            } else if (args[0].equalsIgnoreCase("cleardata")) {
                Set<String> set = Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toSet());
                set.add("*");
                return createReturnList(set, args[1]);
            }
            return null;
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return createReturnList(getSlimefunItems(), args[2]);
            } else if (args[0].equalsIgnoreCase("research")) {
                List<Research> researches = Slimefun.getRegistry().getResearches();
                List<String> suggestions = new LinkedList<>();

                suggestions.add("all");
                suggestions.add("reset");

                for (Research research : researches) {
                    suggestions.add(research.getName());
                }

                return createReturnList(suggestions, args[2]);
            } else if (args[0].equalsIgnoreCase("cleardata")) {
                return createReturnList(Arrays.asList("block", "oil", "*"), args[2]);
            } else {
                // Returning null will make it fallback to the default arguments (all online players)
                return null;
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            return createReturnList(Arrays.asList("1", "2", "4", "8", "16", "32", "64"), args[3]);
        } else {
            // Returning null will make it fallback to the default arguments (all online players)
            return null;
        }
    }

    private List<String> createReturnList(Set<String> set, String string) {
        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new LinkedList<>();

        for (String item : set) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);

                if (returnList.size() >= MAX_SUGGESTIONS) {
                    break;
                }
            } else if (item.equalsIgnoreCase(input)) {
                return Collections.emptyList();
            }
        }

        return returnList;
    }

    private List<String> createReturnList(List<String> list, String string) {
        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new LinkedList<>();

        for (String item : list) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);
            } else if (item.equalsIgnoreCase(input)) {
                return Collections.emptyList();
            }
        }

        return returnList;
    }


    protected Set<String> getSlimefunItems() {
        List<SlimefunItem> items = Slimefun.getRegistry().getEnabledSlimefunItems();
        Set<String> set = new HashSet<>(items.size());

        for (SlimefunItem item : items) {
            set.add(item.getItemName());
        }

        return set;
    }
}
