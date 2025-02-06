package me.qscbm.slimefun4.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern COLOR_PATTERN = Pattern.compile("&([\\da-zA-Z])");

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
                if (chars[readPos] == '§' || (chars[readPos] == '&' && isColorCodeChar(chars[readPos + 1]))) {
                    readPos += 2;
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

    public static TextComponent fromText(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(COLOR_PATTERN.matcher(text).replaceAll("§$1"));
    }
}
