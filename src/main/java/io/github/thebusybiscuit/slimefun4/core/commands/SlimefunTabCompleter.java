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
    public static final List<String> COUNT_LIST = Arrays.asList("1", "2", "4", "8", "16", "32", "64");

    public SlimefunTabCompleter(SlimefunCommand command) {
    }

    public static List<String> onTabComplete(List<String> args) {
        int size = args.size();
        switch (size) {
            case 1 -> {
                return createReturnList(Slimefun.getCommand().getSubCommandNamesToSet(), args.get(0));
            }
            case 2 -> {
                String param = args.get(0).toLowerCase();
                if ("banitem".equalsIgnoreCase(param)) {
                    return createReturnList(getSlimefunItems(), args.get(1));
                }
                if ("unbanitem".equalsIgnoreCase(param)) {
                    Set<String> set = Slimefun.getRegistry().getDisabledSlimefunItemsToSet().stream()
                            .map(SlimefunItem::getItemNormalName)
                            .collect(Collectors.toSet());
                    return createReturnList(set, args.get(1));
                }
                if ("cleardata".equalsIgnoreCase(param)) {
                    Set<String> set = Bukkit.getWorlds().stream().map(WorldInfo::getName).collect(Collectors.toSet());
                    set.add("*");
                    return createReturnList(set, args.get(1));
                }
                if ("calc".equalsIgnoreCase(param)) {
                    return createReturnList(getSlimefunItems(), args.get(1));
                }
                return getPlayerList(args.get(1));
            }
            case 3 -> {
                String param = args.get(0);
                if ("give".equalsIgnoreCase(param)) {
                    return createReturnList(getSlimefunItems(), args.get(2));
                }
                if ("research".equalsIgnoreCase(param)) {
                    List<Research> researches = Slimefun.getRegistry().getResearches();
                    Set<String> suggestions = new HashSet<>();

                    suggestions.add("all");
                    suggestions.add("reset");

                    for (Research research : researches) {
                        suggestions.add(research.getNormalName());
                    }

                    return createReturnList(suggestions, args.get(2));
                }
                if ("cleardata".equalsIgnoreCase(param)) {
                    return createReturnList(Arrays.asList("block", "oil", "*"), args.get(2));
                }

                if ("calc".equalsIgnoreCase(param)) {
                    return createReturnList(COUNT_LIST, args.get(2));
                }
                if ("search".equalsIgnoreCase(param)) {
                    return createReturnList(BOOLEAN_LIST, args.get(2));
                }
            }
            case 4 -> {
                if ("give".equalsIgnoreCase(args.get(0))) {
                    return createReturnList(COUNT_LIST, args.get(3));
                }
            }
        }
        if (size > 0) {
            return TIPS;
        }
        return Slimefun.getCommand().getSubCommandNames();
    }

    public static final List<String> TIPS = Collections.singletonList("温馨提示： 若输入内容中有空格，请用双引号包含");

    public static final List<String> BOOLEAN_LIST = List.of("true", "false");

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
            set.add(item.getItemNormalName());
        }

        return set;
    }
}
