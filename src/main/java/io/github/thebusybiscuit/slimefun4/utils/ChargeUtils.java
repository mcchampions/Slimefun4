package io.github.thebusybiscuit.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.qscbm.slimefun4.message.QsTextComponentImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * This is just a simple helper class to provide static methods to the {@link Rechargeable}
 * interface.
 *
 * @author TheBusyBiscuit
 * @author WalshyDev
 * @see Rechargeable
 */
public final class ChargeUtils {
    public static final String LORE_PREFIX = "§8\u21E8 §e\u26A1 §7";
    public static final String NUMBER_REGEX = "([+-]?\\d+([.]\\d+)?([Ee][+-]?\\d+)?)";
    public static final Pattern REGEX =
            Pattern.compile("(§c§o)?" + LORE_PREFIX + NUMBER_REGEX + " / " + NUMBER_REGEX + " J", Pattern.CASE_INSENSITIVE);

    public static final Pattern REGEX_NEW =
            Pattern.compile("([+-]?\\d+\\.?\\d*([eE][+-]?\\d+)?)\\s/\\s([+-]?\\d+\\.?\\d*([eE][+-]?\\d+)?)\\sJ");

    private ChargeUtils() {
    }

    public static final QsTextComponentImpl CHARGE_LORE_MODULE = new QsTextComponentImpl("\u21E8").color(NamedTextColor.DARK_GRAY)
            .append(new QsTextComponentImpl("\u26A1").color(NamedTextColor.YELLOW));

    public static void setCharge(ItemMeta meta, float charge, float capacity) {
        BigDecimal decimal = BigDecimal.valueOf(charge).setScale(2, RoundingMode.HALF_UP);
        float value = decimal.floatValue();

        NamespacedKey key = Slimefun.getRegistry().getItemChargeDataKey();
        meta.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, value);

        List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
        Component newLine = CHARGE_LORE_MODULE.clone()
                .append(new QsTextComponentImpl(value + " / " + capacity + " J")
                        .color(NamedTextColor.GRAY));
        for (int i = 0; i < lore.size(); i++) {
            Component line = lore.get(i);
            if (line instanceof TextComponent c) {
                if ("\u21E8".equals(c.content())) {
                    List<Component> children = c.children();
                    if (children.size() < 2) {
                        continue;
                    }
                    TextComponent tc = (TextComponent) children.get(1);
                    String content = tc.content();
                    if (REGEX_NEW.matcher(content).matches()) {
                        lore.set(i, newLine);
                        meta.lore(lore);
                        return;
                    }
                } else {
                    if (c.content().isEmpty()) {
                        List<Component> children = c.children();
                        if (children.size() < 3) {
                            continue;
                        }

                        TextComponent fc = (TextComponent) children.get(0);
                        if ("\u21E8 ".equals(fc.content())) {
                            TextComponent tc = (TextComponent) children.get(2);
                            String content = tc.content();
                            if (REGEX_NEW.matcher(content).matches()) {
                                lore.set(i, newLine);
                                meta.lore(lore);
                                return;
                            }
                        }
                    } else {
                        if (REGEX.matcher(c.content()).matches()) {
                            lore.set(i, newLine);
                            meta.lore(lore);
                            return;
                        }
                    }
                }
            }
        }

        lore.add(newLine);
        meta.lore(lore);
    }

    public static float getCharge(ItemMeta meta) {
        NamespacedKey key = Slimefun.getRegistry().getItemChargeDataKey();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Float value = container.get(key, PersistentDataType.FLOAT);

        // If persistent data is available, we just return this value
        if (value != null) {
            return value;
        }

        // If no persistent data exists, we will just fall back to the lore
        if (meta.hasLore()) {
            for (String line : meta.getLore()) {
                Matcher matcher = REGEX.matcher(line);
                if (matcher.matches()) {
                    String data = matcher.group(2);

                    if (data == null) {
                        return 0;
                    }

                    float loreValue = Float.parseFloat(data);
                    container.set(key, PersistentDataType.FLOAT, loreValue);
                    return loreValue;
                }
            }
        }

        return 0;
    }
}
