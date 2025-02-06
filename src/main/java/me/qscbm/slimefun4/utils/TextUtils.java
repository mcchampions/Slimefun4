package me.qscbm.slimefun4.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

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
}
