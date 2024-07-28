package me.qscbm.slimefun4.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class TextUtils {
    public static String toPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String toLegacyText(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static TextComponent fromText(String text) {
       return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
