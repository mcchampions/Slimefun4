package me.qscbm.slimefun4.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    public static String toPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String toPlainText(String text) {
        final char[] chars = text.toCharArray();
        final int length = chars.length;
        int readPos = 0;
        int writePos = 0;

        while (readPos < length) {
            if (readPos + 1 < length) {
                if ((chars[readPos] == '§' || chars[readPos] == '&') && isColorCodeChar(chars[readPos + 1])) {
                    readPos += 2;
                    continue;
                }
            }

            chars[writePos++] = chars[readPos++];
        }

        return new String(chars, 0, writePos);
    }

    public static String toPlainText(String text, int index) {
        final char[] chars = text.toCharArray();
        final int length = chars.length;
        int readPos = 0;
        int writePos = 0;

        while (readPos <= index && readPos < length) {
            if (readPos + 1 < length) {
                if ((chars[readPos] == '§' || chars[readPos] == '&') && isColorCodeChar(chars[readPos + 1])) {
                    readPos += 2;
                    index += 2;
                    continue;
                }
            }

            chars[writePos++] = chars[readPos++];
        }

        return new String(chars, 0, writePos);
    }

    public static boolean isColorCodeChar(char c) {
        if (c < '0' || c > 'x') {
            return false;
        }
        if (c <= '9') {
            return true;
        }

        final int mask = 0b11011111;
        final int uc = c & mask;
        return (uc >= 'A' && uc <= 'F')
               || (uc >= 'K' && uc <= 'O')
               || uc == 'R'
               || uc == 'X';
    }

    public static String toLegacyText(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static List<String> split(String str, String character) {
        int off = 0;
        int next;
        List<String> list = new ArrayList<>(3);
        while ((next = str.indexOf(character, off)) != -1) {
            list.add(str.substring(off, next));
            off = next + 1;
        }
        if (off == 0)
            return List.of(str);

        list.add(str.substring(off));

        int resultSize = list.size();
        while (resultSize > 0 && list.get(resultSize - 1).isEmpty()) {
            resultSize--;
        }
        return list.subList(0, resultSize);
    }

    public static String translateAlternateColorCodes(String text) {
        final char[] chars = text.toCharArray();
        int i = 0;
        while (i < chars.length - 1) {
            if (chars[i] == '&' && isColorCodeChar(chars[i + 1])) {
                chars[i] = '§';
            }
            i++;
        }

        return new String(chars);
    }

    public static TextComponent fromText(String text) {
        return LegacyComponentSerializer.legacySection().deserialize(translateAlternateColorCodes(text));
    }

    public static TextComponent fromText(char character, String text) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        if (character == '&') {
            serializer = LegacyComponentSerializer.legacyAmpersand();
        }
        return serializer.deserialize(text);
    }

    public static boolean isNumber(String text) {
        char[] chars = text.toCharArray();
        for (char c : chars) {
            switch (c) {
                case '1', '2', '3', '4', '5',
                     '6', '7', '8', '9', '0' -> {
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasNumber(String text) {
        char[] chars = text.toCharArray();
        for (char c : chars) {
            switch (c) {
                case '1', '2', '3', '4', '5',
                     '6', '7', '8', '9', '0' -> {
                    return true;
                }
                default -> {
                }
            }
        }
        return false;
    }
}
