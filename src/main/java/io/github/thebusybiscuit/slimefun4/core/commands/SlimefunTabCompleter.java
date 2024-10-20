package io.github.thebusybiscuit.slimefun4.core.commands;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

public class SlimefunTabCompleter {
    public SlimefunTabCompleter(SlimefunCommand command) {
    }

    public static List<String> onTabComplete(List<String> args) {
        if (args.size() == 1) {
            return createReturnList(Slimefun.getCommand().getSubCommandNamesToSet(), args.get(0));
        } else if (args.size() == 2) {
            if (args.get(0).equalsIgnoreCase("banitem")) {
                return createReturnList(getSlimefunItems(), args.get(1));
            } else if (args.get(0).equalsIgnoreCase("unbanitem")) {
                Set<String> set = Slimefun.getRegistry().getDisabledSlimefunItemsToSet().stream()
                        .map(SlimefunItem::getItemNormalName)
                        .collect(Collectors.toSet());
                return createReturnList(set, args.get(1));
            } else if (args.get(0).equalsIgnoreCase("cleardata")) {
                Set<String> set = Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toSet());
                set.add("*");
                return createReturnList(set, args.get(1));
            }
            return getPlayerList(args.get(1));
        } else if (args.size() == 3) {
            if (args.get(0).equalsIgnoreCase("give")) {
                return createReturnList(getSlimefunItems(), args.get(2));
            } else if (args.get(0).equalsIgnoreCase("research")) {
                List<Research> researches = Slimefun.getRegistry().getResearches();
                Set<String> suggestions = new HashSet<>();

                suggestions.add("all");
                suggestions.add("reset");

                for (Research research : researches) {
                    suggestions.add(research.getNormalName());
                }

                return createReturnList(suggestions, args.get(2));
            } else if (args.get(0).equalsIgnoreCase("cleardata")) {
                return createReturnList(Arrays.asList("block", "oil", "*"), args.get(2));
            }
            return Collections.emptyList();
        } else if (args.size() == 4 && args.get(0).equalsIgnoreCase("give")) {
            return createReturnList(Arrays.asList("1", "2", "4", "8", "16", "32", "64"), args.get(3));
        }
        return Slimefun.getCommand().getSubCommandNames();
    }

    @SuppressWarnings("deprecation")
    public static List<String> getPlayerList(String input) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName)
                .filter(s -> s.contains(input)).collect(Collectors.toList());
    }

    public static List<String> createReturnList(Set<String> set, String string) {
        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new LinkedList<>();

        for (String item : set) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);
            } else if (item.equalsIgnoreCase(input)) {
                return Collections.emptyList();
            }
        }

        return returnList;
    }

    public static List<String> createReturnList(List<String> list, String string) {
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


    public static Set<String> getSlimefunItems() {
        List<SlimefunItem> items = Slimefun.getRegistry().getEnabledSlimefunItems();
        Set<String> set = new HashSet<>(items.size());

        for (SlimefunItem item : items) {
            set.add(item.getItemNormalName());
        }

        return set;
    }
}
