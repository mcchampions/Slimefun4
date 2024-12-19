package io.github.thebusybiscuit.slimefun4.utils;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * This is a simple utility class for spawning random and colorful {@link Firework} rockets.
 *
 * @author TheBusyBiscuit
 */
public final class FireworkUtils {
    private static final Color[] COLORS = {
        Color.AQUA,
        Color.BLACK,
        Color.BLUE,
        Color.FUCHSIA,
        Color.GRAY,
        Color.GREEN,
        Color.LIME,
        Color.MAROON,
        Color.NAVY,
        Color.OLIVE,
        Color.ORANGE,
        Color.PURPLE,
        Color.RED,
        Color.SILVER,
        Color.TEAL,
        Color.WHITE,
        Color.YELLOW
    };

    private static final EntityType firework;

    static {
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)) {
            firework = EntityType.valueOf("FIREWORK_ROCKET");
        } else {
            firework = EntityType.FIREWORK;
        }
    }

    private FireworkUtils() {
    }

    public static void launchFirework(Location l, Color color) {
        createFirework(l, color);
    }

    public static Firework createFirework(Location l, Color color) {
        Firework fw = (Firework) l.getWorld().spawnEntity(l, firework);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.setDisplayName(ChatColor.GREEN + "Slimefun Research");
        FireworkEffect effect = getRandomEffect(ThreadLocalRandom.current(), color);
        meta.addEffect(effect);
        meta.setPower(ThreadLocalRandom.current().nextInt(2) + 1);
        fw.setFireworkMeta(meta);

        return fw;
    }

    public static void launchRandom(Entity n, int amount) {
        Random random = ThreadLocalRandom.current();

        for (int i = 0; i < amount; i++) {
            Location l = n.getLocation().clone();
            l.setX(l.getX() + random.nextInt(amount << 1) - amount);
            l.setZ(l.getZ() + random.nextInt(amount << 1) - amount);

            launchFirework(l, getRandomColor());
        }
    }

    /**
     * This returns a randomized {@link FireworkEffect} using the given {@link Color}.
     *
     * @param random The {@link Random} instance to use
     * @param color  The {@link Color} of this {@link FireworkEffect}
     * @return A randomized {@link FireworkEffect}
     */
    public static FireworkEffect getRandomEffect(Random random, Color color) {
        return FireworkEffect.builder()
                .flicker(random.nextBoolean())
                .withColor(color)
                .with(random.nextBoolean() ? Type.BALL : Type.BALL_LARGE)
                .trail(random.nextBoolean())
                .build();
    }

    /**
     * This returns a random {@link Color} for our {@link FireworkEffect}.
     *
     * @return A random {@link Color}
     */
    private static Color getRandomColor() {
        return COLORS[ThreadLocalRandom.current().nextInt(COLORS.length)];
    }
}
