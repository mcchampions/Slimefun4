package io.github.thebusybiscuit.slimefun4.utils.compatibility;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.lang.reflect.Field;
import javax.annotation.Nullable;
import org.bukkit.enchantments.Enchantment;

// https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/legacy/FieldRename.java?until=2a6207fe150b6165722fce94c83cc1f206620ab5&untilPath=src%2Fmain%2Fjava%2Forg%2Fbukkit%2Fcraftbukkit%2Flegacy%2FFieldRename.java#86-110
public class VersionedEnchantment {
    public static final Enchantment EFFICIENCY;
    public static final Enchantment UNBREAKING;
    public static final Enchantment PROTECTION;
    public static final Enchantment SHARPNESS;
    public static final Enchantment LUCK_OF_THE_SEA;
    public static final Enchantment AQUA_AFFINITY;
    public static final Enchantment FORTUNE;

    static {
        MinecraftVersion version = Slimefun.getMinecraftVersion();

        // DIG_SPEED is renamed to EFFICIENCY in 1.20.5
        EFFICIENCY =
                !version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Enchantment.DIG_SPEED : getKey("EFFICIENCY");

        // DURABILITY is renamed to UNBREAKING in 1.20.5
        UNBREAKING =
                !version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Enchantment.DURABILITY : getKey("UNBREAKING");

        // PROTECTION_ENVIRONMENTAL is renamed to PROTECTION in 1.20.5
        PROTECTION = !version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)
                ? Enchantment.PROTECTION_ENVIRONMENTAL
                : getKey("PROTECTION");

        // DAMAGE_ALL is renamed to SHARPNESS in 1.20.5
        SHARPNESS = !version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Enchantment.DAMAGE_ALL : getKey("SHARPNESS");

        // LUCK is renamed to LUCK_OF_THE_SEA in 1.20.5
        LUCK_OF_THE_SEA =
                !version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Enchantment.LUCK : getKey("LUCK_OF_THE_SEA");

        // WATER_WORKER is renamed to AQUA_AFFINITY in 1.20.5
        AQUA_AFFINITY = !version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)
                ? Enchantment.WATER_WORKER
                : getKey("AQUA_AFFINITY");

        // LOOT_BONUS_BLOCKS is renamed to FORTUNE in 1.20.5
        FORTUNE = !version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)
                ? Enchantment.LOOT_BONUS_BLOCKS
                : getKey("FORTUNE");
    }

    @Nullable private static Enchantment getKey(String key) {
        try {
            Field field = Enchantment.class.getDeclaredField(key);
            return (Enchantment) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
