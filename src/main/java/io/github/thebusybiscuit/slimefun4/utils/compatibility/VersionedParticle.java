package io.github.thebusybiscuit.slimefun4.utils.compatibility;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.lang.reflect.Field;
import javax.annotation.Nullable;
import org.bukkit.Particle;

// https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/legacy/FieldRename.java?until=2a6207fe150b6165722fce94c83cc1f206620ab5&untilPath=src%2Fmain%2Fjava%2Forg%2Fbukkit%2Fcraftbukkit%2Flegacy%2FFieldRename.java#281-318
public class VersionedParticle {
    public static final Particle DUST;
    public static final Particle SMOKE;
    public static final Particle HAPPY_VILLAGER;
    public static final Particle ENCHANTED_HIT;
    public static final Particle EXPLOSION;
    public static final Particle WITCH;
    public static final Particle FIREWORK;
    public static final Particle ENCHANT;

    static {
        MinecraftVersion version = Slimefun.getMinecraftVersion();

        // REDSTONE is renamed to DUST in 1.20.5
        DUST = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Particle.REDSTONE : getKey("DUST");

        // SMOKE_NORMAL is renamed to SMOKE in 1.20.5
        SMOKE = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Particle.SMOKE_NORMAL : getKey("SMOKE");

        // VILLAGER_HAPPY is renamed to HAPPY_VILLAGER in 1.20.5
        HAPPY_VILLAGER = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)
                ? Particle.VILLAGER_HAPPY
                : getKey("HAPPY_VILLAGER");

        // CRIT_MAGIC is renamed to ENCHANTED_HIT in 1.20.5
        ENCHANTED_HIT =
                version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Particle.CRIT_MAGIC : getKey("ENCHANTED_HIT");

        // EXPLOSION_LARGE is renamed to EXPLOSION in 1.20.5
        EXPLOSION =
                version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Particle.EXPLOSION_LARGE : getKey("EXPLOSION");

        // SPELL_WITCH is renamed to WITCH in 1.20.5
        WITCH = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Particle.SPELL_WITCH : getKey("WITCH");

        // FIREWORKS_SPARK is renamed to FIREWORK in 1.20.5
        FIREWORK = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Particle.FIREWORKS_SPARK : getKey("FIREWORK");

        // ENCHANTMENT_TABLE is renamed to ENCHANT in 1.20.5
        ENCHANT = version.isAtLeast(MinecraftVersion.MINECRAFT_1_20_5) ? Particle.ENCHANTMENT_TABLE : getKey("ENCHANT");
    }

    @Nullable private static Particle getKey(String key) {
        try {
            Field field = Particle.class.getDeclaredField(key);
            return (Particle) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
