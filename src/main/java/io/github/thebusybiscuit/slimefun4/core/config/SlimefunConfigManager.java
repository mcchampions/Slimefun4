package io.github.thebusybiscuit.slimefun4.core.config;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;

public class SlimefunConfigManager {
    /**
     * Slimefun plugin instance
     */
    private final Slimefun plugin;

    /**
     * Hold plugin config named "config.yml"
     */
    @Getter
    private final Config pluginConfig;

    /**
     * Hold item config named "Items.yml"
     */
    private final Config itemsConfig;

    /**
     * Hold research config named "Researches.yml"
     */
    private final Config researchesConfig;

    private boolean automaticallyLoadItems;
    private boolean enableResearches;
    private boolean researchFireworks;
    private boolean disableLearningAnimation;
    private boolean logDuplicateBlockEntries;
    private boolean talismanActionBarMessages;

    @Getter
    private boolean useMoneyUnlock;

    @Setter
    @Getter
    private boolean showVanillaRecipes;

    @Setter
    @Getter
    private boolean showHiddenItemGroupsInSearch;

    @Getter
    private double researchCurrencyCostConvertRate;

    @Getter
    private static final boolean researchAutoConvert = false;

    @Getter
    private static final boolean bypassEnvironmentCheck = true;

    @Getter
    private int cargoTickerDelay;

    @Getter
    private static String geyserMappingItemsFileName;

    public SlimefunConfigManager(Slimefun plugin) {
        this.plugin = plugin;
        pluginConfig = getConfig(plugin, "config", () -> new Config(plugin));
        itemsConfig = getConfig(plugin, "Items", () -> new Config(plugin, "Items.yml"));
        researchesConfig = getConfig(plugin, "Researches", () -> new Config(plugin, "Researches.yml"));
    }

    @Nullable private static Config getConfig(Slimefun plugin, String name, Supplier<Config> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException x) {
            plugin.getLogger()
                    .log(
                            Level.SEVERE,
                            x,
                            () -> "An Exception was thrown while loading the config file \""
                                    + name
                                    + ".yml\" for Slimefun v"
                                    + plugin.getDescription().getVersion());
            return null;
        }
    }

    public boolean load() {
        return load(false);
    }

    public boolean load(boolean reload) {
        boolean isSuccessful = true;

        try {
            pluginConfig.reload();
            itemsConfig.reload();
            researchesConfig.reload();

            researchesConfig.setDefaultValue("enable-researching", true);
            enableResearches = researchesConfig.getBoolean("enable-researching");
            researchFireworks = pluginConfig.getBoolean("researches.enable-fireworks");
            disableLearningAnimation = pluginConfig.getBoolean("researches.disable-learning-animation");
            logDuplicateBlockEntries = pluginConfig.getBoolean("options.log-duplicate-block-entries");
            talismanActionBarMessages = pluginConfig.getBoolean("talismans.use-actionbar");
            useMoneyUnlock = pluginConfig.getBoolean("researches.use-money-unlock");
            showVanillaRecipes = pluginConfig.getBoolean("guide.show-vanilla-recipes");
            showHiddenItemGroupsInSearch = pluginConfig.getBoolean("guide.show-hidden-item-groups-in-search");
            researchCurrencyCostConvertRate = pluginConfig.getDouble("researches.currency-cost-convert-rate");

            cargoTickerDelay = pluginConfig.getInt("networks.cargo-ticker-delay");
            geyserMappingItemsFileName = pluginConfig.getString("geyser.mapping-items-file-name");
        } catch (RuntimeException x) {
            pluginConfig.setDefaultValue("researches.currency-cost-convert-rate", 25.0);
            researchCurrencyCostConvertRate = pluginConfig.getDouble("researches.currency-cost-convert-rate");

            pluginConfig.setDefaultValue("researches.auto-convert", false);
            researchAutoConvert = pluginConfig.getBoolean("researches.auto-convert");
        } catch (Exception x) {
            plugin.getLogger()
                    .log(
                            Level.SEVERE,
                            x,
                            () -> "An Exception was caught while (re)loading the config files for Slimefun v"
                                    + plugin.getDescription().getVersion());
            isSuccessful = false;
        }

        if (!reload) {
            return true;
        }

        List<Research> researchSnapshot = new ArrayList<>(Slimefun.getRegistry().getResearches());

        // Reload Research costs
        for (Research research : researchSnapshot) {
            try {
                NamespacedKey key = research.getKey();
                int cost = researchesConfig.getInt(key.getNamespace() + '.' + key.getKey() + ".cost");
                research.setLevelCost(cost);

                research.setCurrencyCost(
                        researchesConfig.getDouble(key.getNamespace() + '.' + key.getKey() + ".currency-cost"));

                boolean status = researchesConfig.getBoolean(key.getNamespace() + '.' + key.getKey() + ".enabled");

                if (research.isEnabled() != status) {
                    if (status) {
                        research.register();
                    } else {
                        research.disable();
                    }
                }
            } catch (RuntimeException x) {
                plugin.getLogger()
                        .log(
                                Level.SEVERE,
                                x,
                                () -> "Something went wrong while trying to update the cost of a research: "
                                        + research);
                isSuccessful = false;
            }
        }

        List<SlimefunItem> enabledItemSnapshot = new ArrayList<>(Slimefun.getRegistry().getAllSlimefunItems());

        for (SlimefunItem item : enabledItemSnapshot) {
            ItemState newState = itemsConfig.getBoolean(item.getId() + ".enabled") ? ItemState.ENABLED : ItemState.DISABLED;

            if (item.getState() != newState) {
                switch (newState) {
                    case ENABLED -> item.enable();
                    case DISABLED -> {
                        item.disable();
                        continue;
                    }
                }
            }

            // Reload Item Settings
            try {
                for (ItemSetting<?> setting : item.getItemSettings()) {
                    setting.reload();
                }
            } catch (RuntimeException x) {
                item.error("Something went wrong while updating the settings for this item!", x);
                isSuccessful = false;
            }

            // Reload permissions
            try {
                Slimefun.getPermissionsService().update(item, false);
            } catch (RuntimeException x) {
                item.error("Something went wrong while updating the permission node for this item!", x);
                isSuccessful = false;
            }
        }

        return isSuccessful;
    }

    /**
     * This returns whether auto-loading is enabled.
     * Auto-Loading will automatically call {@link SlimefunItem#load()} when the item is registered.
     * Normally that method is called after the {@link Server} finished starting up.
     * But in the unusual scenario if a {@link SlimefunItem} is registered after that, this is gonna cover that.
     *
     * @return Whether auto-loading is enabled
     */
    public boolean isAutoLoadingEnabled() {
        return automaticallyLoadItems;
    }

    /**
     * This method will make any {@link SlimefunItem} which is registered automatically
     * call {@link SlimefunItem#load()}.
     * Normally this method call is delayed but when the {@link Server} is already running,
     * the method can be called instantaneously.
     *
     * @param mode Whether auto-loading should be enabled
     */
    public void setAutoLoadingMode(boolean mode) {
        automaticallyLoadItems = mode;
    }

    public void setResearchingEnabled(boolean enabled) {
        enableResearches = enabled;
    }

    public boolean isResearchingEnabled() {
        return enableResearches;
    }

    public void setFreeCreativeResearchingEnabled(boolean enabled) {}

    public static boolean isFreeCreativeResearchingEnabled() {
        return false;
    }

    public boolean isResearchFireworkEnabled() {
        return researchFireworks;
    }

    /**
     * Returns whether the research learning animations is disabled
     *
     * @return Whether the research learning animations is disabled
     */
    public boolean isLearningAnimationDisabled() {
        return disableLearningAnimation;
    }

    public boolean logDuplicateBlockEntries() {
        return logDuplicateBlockEntries;
    }

    public boolean useActionbarForTalismans() {
        return talismanActionBarMessages;
    }

}
