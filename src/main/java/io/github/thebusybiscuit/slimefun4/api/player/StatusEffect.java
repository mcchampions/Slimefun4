package io.github.thebusybiscuit.slimefun4.api.player;

import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.TimeUnit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

/**
 * A very simple API that is meant for adding/getting/clearing custom status effects
 * to/from players.
 * <p>
 * The effects are stored via {@link PersistentDataAPI} and use NBT data that is
 * saved across server restarts.
 * <p>
 * You can specify a level for your status effect too.
 *
 * @author TheBusyBiscuit
 *
 */
public record StatusEffect(NamespacedKey key) implements Keyed {
    @Override
    public NamespacedKey getKey() {
        return key();
    }

    /**
     * This applies this {@link StatusEffect} to the given {@link Player}.
     * You can specify a duration, this will reference
     * {@link StatusEffect#add(Player, int, int, TimeUnit)} with a level of 1.
     *
     * @param p
     *            The {@link Player} whom to apply the effect to
     * @param duration
     *            The duration of how long that status effect shall last
     * @param unit
     *            The {@link TimeUnit} for the given duration
     */
    public void add(Player p, int duration, TimeUnit unit) {
        add(p, 1, duration, unit);
    }

    /**
     * This applies this {@link StatusEffect} to the given {@link Player}.
     *
     * @param p
     *            The {@link Player} whom to apply the effect to
     * @param level
     *            The level of this effect
     * @param duration
     *            The duration of how long that status effect shall last
     * @param unit
     *            The {@link TimeUnit} for the given duration
     */
    public void add(Player p, int level, int duration, TimeUnit unit) {
        PersistentDataAPI.setString(p, getKey(), level + ";" + System.currentTimeMillis() + unit.toMillis(duration));
    }

    /**
     * This applies this {@link StatusEffect} to the given {@link Player}.
     * This will apply it permanently, there is no duration.
     *
     * @param p
     *            The {@link Player} whom to apply the effect to
     * @param level
     *            The level of this effect
     */
    public void addPermanent(Player p, int level) {
        PersistentDataAPI.setString(p, getKey(), level + ";0");
    }

    /**
     * This will check whether this {@link StatusEffect} is currently applied
     * to that {@link Player}.
     * If the effect has expired, it will automatically remove all associated
     * NBT data of this effect.
     *
     * @param p
     *            The {@link Player} to check for
     * @return Whether this {@link StatusEffect} is currently applied
     */
    public boolean isPresent(Player p) {
        Optional<String> optional = PersistentDataAPI.getOptionalString(p, getKey());

        if (optional.isPresent()) {
            String[] data = optional.get().split(";");
            long timestamp = Long.parseLong(data[1]);

            if (timestamp == 0 || timestamp >= System.currentTimeMillis()) {
                return true;
            } else {
                clear(p);
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * This method returns an {@link OptionalInt} describing the level of this status
     * effect on that player.
     *
     * @param p
     *            The {@link Player} to check for
     * @return An {@link OptionalInt} that describes the result
     */
    public OptionalInt getLevel(Player p) {
        Optional<String> optional = PersistentDataAPI.getOptionalString(p, getKey());

        if (optional.isPresent()) {
            String[] data = optional.get().split(";");
            return OptionalInt.of(Integer.parseInt(data[0]));
        } else {
            return OptionalInt.empty();
        }
    }

    /**
     * This will remove this {@link StatusEffect} from the given {@link Player}.
     *
     * @param p
     *            The {@link Player} to clear it from
     */
    public void clear(Player p) {
        PersistentDataAPI.remove(p, getKey());
    }
}
