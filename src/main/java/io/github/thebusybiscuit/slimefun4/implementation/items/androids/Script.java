package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import io.github.bakedlibs.dough.config.Config;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import lombok.Getter;
import me.qscbm.slimefun4.utils.QsConstants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

/**
 * A {@link Script} represents runnable code for a {@link ProgrammableAndroid}.
 *
 * @author TheBusyBiscuit
 *
 */
public final class Script {
    private final Config config;
    /**
     * -- GETTER --
     *  This returns the name of this
     * .
     *
     */
    @Getter
    private final String name;
    /**
     * -- GETTER --
     *  This returns the author of this
     * .
     *  The author is the person who initially created and uploaded this
     * .
     *
     */
    @Getter
    private final String author;
    private final String code;

    /**
     * This constructs a new {@link Script} from the given {@link Config}.
     *
     * @param config
     *            The {@link Config}
     */
    private Script(Config config) {
        this.config = config;
        this.name = config.getString("name");
        this.code = config.getString("code");
        String uuid = config.getString("author");
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        this.author = player.getName() != null ? player.getName() : config.getString("author_name");
    }

    /**
     * This method returns the actual code of this {@link Script}.
     * It is basically a {@link String} describing the order of {@link Instruction Instructions} that
     * shall be executed.
     *
     * @return The code for this {@link Script}
     */

    public String getSourceCode() {
        return code;
    }

    /**
     * This method determines whether the given {@link OfflinePlayer} is the author of
     * this {@link Script}.
     *
     * @param p
     *            The {@link OfflinePlayer} to check for
     *
     * @return Whether the given {@link OfflinePlayer} is the author of this {@link Script}.
     */
    public boolean isAuthor(OfflinePlayer p) {
        return p.getUniqueId().equals(config.getUUID("author"));
    }

    /**
     * This method checks whether a given {@link Player} is able to leave a rating for this {@link Script}.
     * A {@link Player} is unable to rate his own {@link Script} or a {@link Script} he already rated before.
     *
     * @param p
     *            The {@link Player} to check for
     *
     * @return Whether the given {@link Player} is able to rate this {@link Script}
     */
    public boolean canRate(Player p) {
        if (isAuthor(p)) {
            return false;
        }

        List<String> upvoters = config.getStringList("rating.positive");
        List<String> downvoters = config.getStringList("rating.negative");
        return !upvoters.contains(p.getUniqueId().toString())
                && !downvoters.contains(p.getUniqueId().toString());
    }

    ItemStack getAsItemStack(ProgrammableAndroid android, Player p) {
        List<String> lore = new LinkedList<>();
        lore.add("&7作者 &f" + author);
        lore.add("");
        lore.add("&7下载量: &f" + getDownloads());
        lore.add("&7评分: " + getScriptRatingPercentage());
        lore.add("&a" + getUpvotes() + " \u263A &7| §4\u2639 " + getDownvotes());
        lore.add("");
        lore.add("&e左键 &f下载脚本");
        lore.add("§4(将会覆盖你现有的脚本!)");

        if (canRate(p)) {
            lore.add("");
            lore.add("&eShift + 左键 &f好评");
            lore.add("&eShift + 右键 &f差评");
        }

        return new CustomItemStack(android.getItem(), "&b" + name, lore.toArray(QsConstants.EMPTY_STRINGS));
    }

    private String getScriptRatingPercentage() {
        float percentage = getRating();
        return NumberUtils.getColorFromPercentage(percentage) + String.valueOf(percentage) + ChatColor.WHITE + "% ";
    }

    /**
     * This method returns the amount of upvotes this {@link Script} has received.
     *
     * @return The amount of upvotes
     */
    public int getUpvotes() {
        return config.getStringList("rating.positive").size();
    }

    /**
     * This method returns the amount of downvotes this {@link Script} has received.
     *
     * @return The amount of downvotes
     */
    public int getDownvotes() {
        return config.getStringList("rating.negative").size();
    }

    /**
     * This returns how often this {@link Script} has been downloaded.
     *
     * @return The amount of downloads for this {@link Script}.
     */
    public int getDownloads() {
        return config.getInt("downloads");
    }

    /**
     * This returns the "rating" of this {@link Script}.
     * This value is calculated from the up- and downvotes this {@link Script} received.
     *
     * @return The rating for this {@link Script}
     */
    public float getRating() {
        int positive = getUpvotes() + 1;
        int negative = getDownvotes();
        return Math.round((positive / (float) (positive + negative)) * 100.0F) / 100.0F;
    }

    /**
     * This method increases the amount of downloads by one.
     */
    public void download() {
        config.reload();
        config.setValue("downloads", getDownloads() + 1);
        config.save();
    }

    public void rate(Player p, boolean positive) {
        config.reload();

        String path = "rating." + (positive ? "positive" : "negative");
        List<String> list = config.getStringList(path);
        list.add(p.getUniqueId().toString());

        config.setValue(path, list);
        config.save();
    }

    public static List<Script> getUploadedScripts(AndroidType androidType) {
        List<Script> scripts = new LinkedList<>();

        loadScripts(scripts, androidType);

        if (androidType != AndroidType.NONE) {
            loadScripts(scripts, AndroidType.NONE);
        }

        scripts.sort(Comparator.comparingInt(script -> -script.getUpvotes() + 1 - script.getDownvotes()));
        return scripts;
    }

    private static void loadScripts(List<Script> scripts, AndroidType type) {
        File directory = new File("plugins/Slimefun/scripts/" + type.name());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".sfs")) {
                try {
                    Config config = new Config(file);

                    // Some older versions somehow allowed null values to slip in here sometimes
                    // So we need this check for compatibility with older scripts
                    if (config.contains("code") && config.contains("author")) {
                        scripts.add(new Script(config));
                    }
                } catch (RuntimeException x) {
                    Slimefun.logger()
                            .log(
                                    Level.SEVERE,
                                    x,
                                    () -> "An Exception occurred while trying to load Android Script '"
                                            + file.getName()
                                            + "'");
                }
            }
        }
    }

    public static void upload(Player p, AndroidType androidType, int id, String name, String code) {
        Config config =
                new Config("plugins/Slimefun/scripts/" + androidType.name() + '/' + p.getName() + ' ' + id + ".sfs");

        config.setValue("author", p.getUniqueId().toString());
        config.setValue("author_name", p.getName());
        config.setValue("name", ChatUtils.removeColorCodes(name));
        config.setValue("code", code);
        config.setValue("downloads", 0);
        config.setValue("android", androidType.name());
        config.setValue("rating.positive", new ArrayList<String>());
        config.setValue("rating.negative", new ArrayList<String>());
        config.save();
    }
}
