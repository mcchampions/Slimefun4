package me.qscbm.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.pinyin.PinyinHelper;

import java.util.*;
import java.util.logging.Level;

public class PinyinUtils {
    public static Map<SlimefunItem, String> ITEM_MAPPING;

    public static void init() {
        Slimefun.logger().log(Level.INFO, "正在加载拼音搜索支持");
        List<SlimefunItem> list = Slimefun.getRegistry().getEnabledSlimefunItems();
        try {
            ITEM_MAPPING = new HashMap<>(list.size());
            for (SlimefunItem slimefunItem : list) {
                ITEM_MAPPING.put(slimefunItem, getPinyin(slimefunItem.getBaseName()));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Slimefun.logger().log(Level.INFO, "拼音搜索支持加载完毕");
    }

    public static String getPinyin(SlimefunItem item) {
        return ITEM_MAPPING.get(item);
    }

    public static String getPinyin(String hanzi) {
        if (hanzi == null || hanzi.isEmpty()) {
            return "";
        }

        String trimmed = hanzi.trim();

        List<Set<String>> optionsList = new ArrayList<>();
        char[] chars = trimmed.toCharArray();

        for (char c : chars) {
            if (isChineseCharacter(c)) {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
                    Set<String> charOptions = new HashSet<>((int) (pinyins.length * 1.6d + 3));
                    for (String py : pinyins) {
                        charOptions.add(String.valueOf(py.charAt(0)));
                    }
                    charOptions.addAll(Arrays.asList(pinyins));
                    charOptions.add(String.valueOf(c));
                    optionsList.add(charOptions);

                } catch (Exception e) {
                    e.printStackTrace();
                    optionsList.add(Collections.singleton(String.valueOf(c)));
                }
            } else {
                optionsList.add(Collections.singleton(String.valueOf(c)));
            }
        }

        try {
            return generateCombinations(optionsList);
        } catch (Throwable e) {
            e.printStackTrace();
            Slimefun.logger().warning("加载拼音时出现错误, 出现错误的物品:" + hanzi);
        }
        return "";
    }

    private static String generateCombinations(List<Set<String>> optionsList) {
        StringBuilder result = new StringBuilder(optionsList.size() * 6);
        generateCombinationsHelper(optionsList, 0, new StringBuilder(), result);
        return result.toString();
    }

    private static void generateCombinationsHelper(List<Set<String>> optionsList, int depth, StringBuilder current, StringBuilder results) {
        if (depth == optionsList.size()) {
            results.append(current.toString()).append(" ;");
            return;
        }

        for (String option : optionsList.get(depth)) {
            int lengthBefore = current.length();
            current.append(option);
            generateCombinationsHelper(optionsList, depth + 1, current, results);
            current.setLength(lengthBefore);
        }
    }


    private static boolean isChineseCharacter(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;
    }
}
