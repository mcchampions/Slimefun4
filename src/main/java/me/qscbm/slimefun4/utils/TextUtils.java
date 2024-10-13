package me.qscbm.slimefun4.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.regex.Pattern;

public class TextUtils {
    private static final Pattern COLOR_PATTERN = Pattern.compile("&([\\da-zA-Z])");
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)[§&][0-9A-FK-ORX]");

    public static String toPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String toPlainText(String text) {
        return STRIP_COLOR_PATTERN.matcher(text).replaceAll("");
    }

    public static String toLegacyText(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static TextComponent fromText(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(COLOR_PATTERN.matcher(text).replaceAll("§$1"));
    }
}
