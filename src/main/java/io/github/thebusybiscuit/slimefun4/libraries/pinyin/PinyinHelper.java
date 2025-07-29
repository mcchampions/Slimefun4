/**
 * This file is part of pinyin4j (http://sourceforge.net/projects/pinyin4j/) and distributed under
 * GNU GENERAL PUBLIC LICENSE (GPL).
 * <p>
 * pinyin4j is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * <p>
 * pinyin4j is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with pinyin4j.
 */

package io.github.thebusybiscuit.slimefun4.libraries.pinyin;

import io.github.thebusybiscuit.slimefun4.libraries.pinyin.format.HanyuPinyinOutputFormat;
import io.github.thebusybiscuit.slimefun4.libraries.pinyin.format.exception.BadHanyuPinyinOutputFormatCombination;
import io.github.thebusybiscuit.slimefun4.libraries.pinyin.multipinyin.Trie;

/**
 * A class provides several utility functions to convert Chinese characters
 * (both Simplified and Tranditional) into various Chinese Romanization
 * representations
 *
 * @author Li Min (xmlerlimin@gmail.com)
 */
public class PinyinHelper {

    private static final String[] ARR_EMPTY = {};

    /**
     * Get all unformmatted Hanyu Pinyin presentations of a single Chinese
     * character (both Simplified and Tranditional)
     * <p>
     * <p>
     * For example, <br/> If the input is '间', the return will be an array with
     * two Hanyu Pinyin strings: <br/> "jian1" <br/> "jian4" <br/> <br/> If the
     * input is '李', the return will be an array with single Hanyu Pinyin
     * string: <br/> "li3"
     * <p>
     * <p>
     * <b>Special Note</b>: If the return is "none0", that means the input
     * Chinese character exists in Unicode CJK talbe, however, it has no
     * pronounciation in Chinese
     *
     * @param ch the given Chinese character
     * @return a String array contains all unformmatted Hanyu Pinyin
     * presentations with tone numbers; null for non-Chinese character
     */
    static public String[] toHanyuPinyinStringArray(char ch) {
        return getUnformattedHanyuPinyinStringArray(ch);
    }


    /**
     * Delegate function
     *
     * @param ch the given Chinese character
     * @return unformatted Hanyu Pinyin strings; null if the record is not found
     */
    private static String[] getUnformattedHanyuPinyinStringArray(char ch) {
        return ChineseToPinyinResource.getInstance().getHanyuPinyinStringArray(ch);
    }

    /**
     * Get a string which all Chinese characters are replaced by corresponding
     * main (first) Hanyu Pinyin representation.
     * <p>
     * <p>
     * <b>Special Note</b>: If the return contains "none0", that means that
     * Chinese character is in Unicode CJK talbe, however, it has not
     * pronounciation in Chinese. <b> This interface will be removed in next
     * release. </b>
     *
     * @param str          A given string contains Chinese characters
     * @param outputFormat Describes the desired format of returned Hanyu Pinyin string
     * @param separate     The string is appended after a Chinese character (excluding
     *                     the last Chinese character at the end of sentence). <b>Note!
     *                     Separate will not appear after a non-Chinese character</b>
     * @param retain       Retain the characters that cannot be converted into pinyin characters
     * @return a String identical to the original one but all recognizable
     * Chinese characters are converted into main (first) Hanyu Pinyin
     * representation
     */
    static public String toHanYuPinyinString(String str, HanyuPinyinOutputFormat outputFormat,
                                             String separate, boolean retain) throws BadHanyuPinyinOutputFormatCombination {
        ChineseToPinyinResource resource = ChineseToPinyinResource.getInstance();
        StringBuilder resultPinyinStrBuf = new StringBuilder();

        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            String result = null;//匹配到的最长的结果
            char ch = chars[i];
            Trie currentTrie = resource.getUnicodeToHanyuPinyinTable();
            int success = i;
            int current = i;
            do {
                String hexStr = Integer.toHexString(ch).toUpperCase();
                currentTrie = currentTrie.get(hexStr);
                if (currentTrie != null) {
                    if (currentTrie.getPinyin() != null) {
                        result = currentTrie.getPinyin();
                        success = current;
                    }
                    currentTrie = currentTrie.getNextTire();
                }
                current++;
                if (current < chars.length)
                    ch = chars[current];
                else
                    break;
            }
            while (currentTrie != null);

            if (result == null) {//如果在前缀树中没有匹配到，那么它就不能转换为拼音，直接输出或者去掉
                if (retain) resultPinyinStrBuf.append(chars[i]);
            } else {
                String[] pinyinStrArray = resource.parsePinyinString(result);
                if (pinyinStrArray != null) {
                    for (int j = 0; j < pinyinStrArray.length; j++) {
                        resultPinyinStrBuf.append(PinyinFormatter.formatHanyuPinyin(pinyinStrArray[j], outputFormat));
                        if (current < chars.length || (j < pinyinStrArray.length - 1 && i != success)) {//不是最后一个,(也不是拼音的最后一个,并且不是最后匹配成功的)
                            resultPinyinStrBuf.append(separate);
                        }
                        if (i == success)
                            break;
                    }
                }
            }
            i = success;
        }

        return resultPinyinStrBuf.toString();
    }

    // ! Hidden constructor
    private PinyinHelper() {
    }
}
