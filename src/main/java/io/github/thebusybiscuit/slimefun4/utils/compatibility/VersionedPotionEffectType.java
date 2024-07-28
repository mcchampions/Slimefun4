package io.github.thebusybiscuit.slimefun4.utils.compatibility;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.lang.reflect.Field;
import javax.annotation.Nullable;
import org.bukkit.potion.PotionEffectType;

// https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/legacy/FieldRename.java?until=2a6207fe150b6165722fce94c83cc1f206620ab5&untilPath=src%2Fmain%2Fjava%2Forg%2Fbukkit%2Fcraftbukkit%2Flegacy%2FFieldRename.java#216-228
public class VersionedPotionEffectType {
    public static final PotionEffectType SLOWNESS;
    public static final PotionEffectType HASTE;
    public static final PotionEffectType MINING_FATIGUE;
    public static final PotionEffectType STRENGTH;
    public static final PotionEffectType INSTANT_HEALTH;
    public static final PotionEffectType INSTANT_DAMAGE;
    public static final PotionEffectType JUMP_BOOST;
    public static final PotionEffectType NAUSEA;
    public static final PotionEffectType RESISTANCE;

    static {
        MinecraftVersion version = Slimefun.getMinecraftVersion();

        SLOWNESS = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? PotionEffectType.SLOW : getKey("SLOWNESS");

        HASTE = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? PotionEffectType.FAST_DIGGING : getKey("HASTE");

        MINING_FATIGUE = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)
                ? PotionEffectType.SLOW_DIGGING
                : getKey("MINING_FATIGUE");

        STRENGTH = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)
                ? PotionEffectType.INCREASE_DAMAGE
                : getKey("STRENGTH");

        INSTANT_HEALTH =
                version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? PotionEffectType.HEAL : getKey("INSTANT_HEALTH");

        INSTANT_DAMAGE =
                version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? PotionEffectType.HARM : getKey("INSTANT_DAMAGE");

        JUMP_BOOST =
                version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? PotionEffectType.JUMP : getKey("JUMP_BOOST");

        NAUSEA = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? PotionEffectType.CONFUSION : getKey("NAUSEA");

        RESISTANCE = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)
                ? PotionEffectType.DAMAGE_RESISTANCE
                : getKey("RESISTANCE");
    }

    @Nullable private static PotionEffectType getKey(String key) {
        try {
            Field field = PotionEffectType.class.getDeclaredField(key);
            return (PotionEffectType) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
