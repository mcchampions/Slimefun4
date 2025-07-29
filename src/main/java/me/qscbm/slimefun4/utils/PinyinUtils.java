package me.qscbm.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.*;
import java.util.logging.Level;

public class PinyinUtils {
    public static final HanyuPinyinOutputFormat ALL_PINYIN_FORMAT = new HanyuPinyinOutputFormat();

    public static Map<SlimefunItem,String> ITEM_MAPPING;

    static {
        ALL_PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        ALL_PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        ALL_PINYIN_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public static void init() {
        Slimefun.logger().log(Level.INFO, "正在加载拼音搜索支持");
        List<SlimefunItem> list = Slimefun.getRegistry().getEnabledSlimefunItems();
        try {
            ITEM_MAPPING = new HashMap<>(list.size());
            for (SlimefunItem slimefunItem : list) {
                String name = slimefunItem.getItemNormalName().toLowerCase();
                StringBuilder builder = new StringBuilder();
                for (String s : getPinyin(name)) {
                    builder.append(s).append(" ;");
                }
                ITEM_MAPPING.put(slimefunItem, builder.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Slimefun.logger().log(Level.INFO, "拼音搜索支持加载完毕");
    }

    public static String getPinyin(SlimefunItem item) {
        return ITEM_MAPPING.get(item);
    }

    public static Set<String> getPinyin(String hanzi) {
        if (hanzi == null || hanzi.isEmpty()) {
            return Collections.emptySet();
        }

        String trimmed = hanzi.trim();

        List<List<String>> optionsList = new ArrayList<>();
        char[] chars = trimmed.toCharArray();

        for (char c : chars) {
            if (isChineseCharacter(c)) {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, ALL_PINYIN_FORMAT);
                    if (pinyins != null && pinyins.length > 0) {
                        List<String> charOptions = new ArrayList<>(Arrays.asList(pinyins));
                        for (String py : pinyins) {
                            charOptions.add(String.valueOf(py.charAt(0)));
                        }
                        charOptions.add(String.valueOf(c));
                        optionsList.add(charOptions);
                    } else {
                        optionsList.add(Collections.singletonList(String.valueOf(c)));
                    }
                } catch (Exception e) {
                    optionsList.add(Collections.singletonList(String.valueOf(c)));
                }
            } else {
                optionsList.add(Collections.singletonList(String.valueOf(c)));
            }
        }

        return generateCombinations(optionsList);
    }

    private static Set<String> generateCombinations(List<List<String>> optionsList) {
        Set<String> results = new HashSet<>();
        generateCombinationsHelper(optionsList, 0, new StringBuilder(), results);
        return results;
    }

    private static void generateCombinationsHelper(List<List<String>> optionsList, int depth, StringBuilder current, Set<String> results) {
        if (depth == optionsList.size()) {
            results.add(current.toString());
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
