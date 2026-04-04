package io.github.thebusybiscuit.slimefun4.core.commands;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;

public class SlimefunTabCompleter  {
    public static final List<String> COUNT_LIST = Arrays.asList("1", "2", "4", "8", "16", "32", "64");

    public SlimefunTabCompleter(SlimefunCommand command) {}

    public static List<String> onTabComplete(List<String> args) {
        int size = args.size();
        if (size == 2) {
            if (args.get(0).equalsIgnoreCase("banitem")) {
                return createReturnList(getSlimefunItems(), args.get(1));
            } else if (args.get(0).equalsIgnoreCase("unbanitem")) {
                List<String> list = Slimefun.getRegistry().getDisabledSlimefunItems().stream()
                        .map(SlimefunItem::getId)
                        .collect(Collectors.toList());
                return createReturnList(list, args.get(1));
            } else if (args.get(0).equalsIgnoreCase("cleardata")) {
                List<String> list = new ArrayList<>(
                        Bukkit.getWorlds().stream().map(WorldInfo::getName).toList());
                list.add("*");
                return createReturnList(list, args.get(1));
            }
            return createReturnList(getPlayerList(args.get(1)), args.get(1));
        }
        if (size == 3) {
            if (args.get(0).equalsIgnoreCase("give")) {
                return createReturnList(getSlimefunItems(), args.get(2));
            } else if (args.get(0).equalsIgnoreCase("research")) {
                List<Research> researches = Slimefun.getRegistry().getResearches();
                List<String> suggestions = new LinkedList<>();

                suggestions.add("all");
                suggestions.add("reset");

                for (Research research : researches) {
                    suggestions.add(research.getKey().toString().toLowerCase(Locale.ROOT));
                }

                return createReturnList(suggestions, args.get(2));
            } else if (args.get(0).equalsIgnoreCase("cleardata")) {
                return createReturnList(COUNT_LIST, args.get(2));
            } else {
                return createReturnList(getPlayerList(args.get(2)), args.get(2));
            }
        }
        if (size == 4 && args.get(0).equalsIgnoreCase("give")) {
            return createReturnList(COUNT_LIST, args.get(3));
        }
        if (size > 0) {
            return createReturnList(getPlayerList(args.get(size-1)), args.get(size-1));
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
                if (item.contains(" ")) {
                    item =  "\"" + item + "\"";
                }
                returnList.add(item);
            }
        }

        return returnList;
    }

    public static List<String> createReturnList(List<String> list, String string) {
        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new LinkedList<>();

        for (String item : list) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                if (item.contains(" ")) {
                    item =  "\"" + item + "\"";
                }
                returnList.add(item);
            }
        }

        return returnList;
    }

    public static Set<String> getSlimefunItems() {
        List<SlimefunItem> items = Slimefun.getRegistry().getEnabledSlimefunItems();
        Set<String> set = new HashSet<>(items.size());

        for (SlimefunItem item : items) {
            set.add(item.getId());
        }

        return set;
    }
}
