package io.github.thebusybiscuit.slimefun4.implementation;

import city.norain.slimefun4.SlimefunExtended;
import city.norain.slimefun4.timings.SQLProfiler;
import com.xzavier0722.mc.plugin.slimefun4.chat.PlayerChatCatcher;
import com.xzavier0722.mc.plugin.slimefuncomplib.ICompatibleSlimefun;
import io.github.bakedlibs.dough.config.Config;
import io.github.bakedlibs.dough.protection.ProtectionManager;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.exceptions.TagMisconfigurationException;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.gps.GPSNetwork;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.SlimefunRegistry;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.config.SlimefunConfigManager;
import io.github.thebusybiscuit.slimefun4.core.config.SlimefunDatabaseManager;
import io.github.thebusybiscuit.slimefun4.core.networks.NetworkManager;
import io.github.thebusybiscuit.slimefun4.core.services.*;
import io.github.thebusybiscuit.slimefun4.core.services.holograms.HologramsService;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.SlimefunProfiler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundService;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.Cooler;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.BeeWings;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GrapplingHook;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.SeismicAxe;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.*;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting.*;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.*;
import io.github.thebusybiscuit.slimefun4.implementation.resources.GEOResourcesSetup;
import io.github.thebusybiscuit.slimefun4.implementation.setup.PostSetup;
import io.github.thebusybiscuit.slimefun4.implementation.setup.ResearchSetup;
import io.github.thebusybiscuit.slimefun4.implementation.setup.SlimefunItemSetup;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.SlimefunStartupTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.armor.RadiationTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.armor.RainbowArmorTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.armor.SlimefunArmorTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.armor.SolarHelmetTask;
import io.github.thebusybiscuit.slimefun4.integrations.IntegrationsManager;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import lombok.Getter;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.MenuListener;
import me.qscbm.slimefun4.integrations.GeyserIntegration;
import me.qscbm.slimefun4.listeners.GuideListener;
import me.qscbm.slimefun4.utils.PinyinUtils;
import me.qscbm.slimefun4.utils.QsConstants;
import me.qscbm.slimefun4.utils.QsItemUtils;
import me.qscbm.slimefun4.services.LanguageService;
import me.qscbm.slimefun4.tasks.CargoTickerTask;
import me.qscbm.slimefun4.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This is the main class of Slimefun.
 * This is where all the magic starts, take a look around.
 *
 * @author TheBusyBiscuit
 */
public final class Slimefun extends JavaPlugin implements SlimefunAddon, ICompatibleSlimefun {
    private static final ThreadPoolExecutor MISC_EXECUTOR = new ThreadPoolExecutor(
            4,
            6,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>()
    );

    /**
     * Our static instance of {@link Slimefun}.
     * Make sure to clean this up in {@link #onDisable()}!
     */
    @SuppressWarnings("FieldHasSetterButNoGetter")
    private static Slimefun instance;

    /**
     * Keep track of which {@link MinecraftVersion} we are on.
     */
    private MinecraftVersion minecraftVersion = MinecraftVersion.UNKNOWN;

    /**
     * Keep track of whether this is a fresh install or a regular boot up.
     */
    private boolean isNewlyInstalled;

    // Various things we need
    private final SlimefunConfigManager cfgManager = new SlimefunConfigManager(this);
    private final SlimefunDatabaseManager databaseManager = new SlimefunDatabaseManager(this);
    private final SlimefunRegistry registry = new SlimefunRegistry();
    private final SlimefunCommand command = new SlimefunCommand(this);
    private final TickerTask ticker = new TickerTask();
    private PlayerChatCatcher chatCatcher;

    // Services - Systems that fulfill certain tasks, treat them as a black box
    private final CustomItemDataService itemDataService = new CustomItemDataService(this, "slimefun_item");
    private final BlockDataService blockDataService = new BlockDataService(this, "slimefun_block");
    private final CustomTextureService textureService = new CustomTextureService(new Config(this, "item-models.yml"));
    private final BackupService backupService = new BackupService();
    private final PermissionsService permissionsService = new PermissionsService(this);
    private final PerWorldSettingsService worldSettingsService = new PerWorldSettingsService(this);
    private final MinecraftRecipeService recipeService = new MinecraftRecipeService(this);
    private final HologramsService hologramsService = new HologramsService(this);
    private final SoundService soundService = new SoundService(this);

    // Some other things we need
    private final IntegrationsManager integrations = new IntegrationsManager(this);
    private final SlimefunProfiler profiler = new SlimefunProfiler();
    private final SQLProfiler sqlProfiler = new SQLProfiler();
    private final GPSNetwork gpsNetwork = new GPSNetwork(this);

    // Even more things we need
    private NetworkManager networkManager;
    private LocalizationService local;

    // Important config files for Slimefun
    private final Config items = new Config(this, "Items.yml");
    private final Config researches = new Config(this, "Researches.yml");

    // Listeners that need to be accessed elsewhere
    private final GrapplingHookListener grapplingHookListener = new GrapplingHookListener();
    private final BackpackListener backpackListener = new BackpackListener();
    private final SlimefunBowListener bowListener = new SlimefunBowListener();

    private static BukkitScheduler bukkitScheduler;

    // fork
    @Getter
    private final CargoTickerTask cargoTickerTask = new CargoTickerTask();

    /**
     * Our default constructor for {@link Slimefun}.
     */
    public Slimefun() {
    }

    private boolean initialized;

    /**
     * This is called when the {@link Plugin} has been loaded and enabled on a {@link Server}.
     */
    @Override
    public void onEnable() {
        instance = this;
        logger().info("Slimefun开始加载");
        Bukkit.getScheduler().runTask(this, QsConstants::init);
        if (initialized) {
            getLogger().log(Level.WARNING, "不支持热重载, 请重启服务器");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        initialized = true;
        int version = VersionUtils.getMinecraftVersion();
        int patchVersion = VersionUtils.getMinecraftPatchVersion();

        if (version > 0) {
            // Check all supported versions of Minecraft
            for (MinecraftVersion supportedVersion : MinecraftVersion.values()) {
                if (supportedVersion.isMinecraftVersion(version, patchVersion)) {
                    minecraftVersion = supportedVersion;
                    break;
                }
            }
        }
        if (minecraftVersion == MinecraftVersion.UNKNOWN) {
            throw new RuntimeException("不支持的版本1." + version + "." + patchVersion);
        }
        if (!SlimefunExtended.checkEnvironment(this)) {
            // We want to ensure that the Server uses a compatible server software and have no
            // incompatible plugins
            getServer().getPluginManager().disablePlugin(this);
        } else {
            // The Environment has been validated.
            onPluginStart();
        }
        bukkitScheduler = getServer().getScheduler();
    }

    /**
     * This is our start method for a correct Slimefun installation.
     */
    private void onPluginStart() {
        long timestamp = System.nanoTime();
        Logger logger = getLogger();

        // If the server has no "data-storage" folder, it's _probably_ a new install. So mark it for
        // metrics.
        isNewlyInstalled = !new File("data-storage/Slimefun").exists();

        // Creating all necessary Folders
        logger.log(Level.INFO, "正在创建文件夹...");
        createDirectories();

        // Load various config settings into our cache
        cfgManager.load();
        registry.load(this);

        logger.log(Level.INFO, "正在加载数据库...");
        databaseManager.init();

        // Set up localization
        logger.log(Level.INFO, "正在加载语言文件...");

        Config config = cfgManager.getPluginConfig();
        String chatPrefix = config.getString("options.chat-prefix");
        String serverDefaultLanguage = "zh-CN";
        local = new LocalizationService(this, chatPrefix, serverDefaultLanguage);

        int networkSize = config.getInt("networks.max-size");

        // Make sure that the network size is a valid input
        if (networkSize < 1) {
            logger.log(Level.WARNING, "'networks.max-size' 大小设置错误! 它必须大于1, 而你设置的是: {0}", networkSize);
            networkSize = 1;
        }

        networkManager = new NetworkManager(
                networkSize,
                config.getBoolean("networks.enable-visualizer"),
                config.getBoolean("networks.delete-excess-items"));
        // load language cache
        LanguageService.load();
        // Registering all GEO Resources

        logger.log(Level.INFO, "加载矿物资源...");
        GEOResourcesSetup.setup();

        logger.log(Level.INFO, "加载自定义标签...");
        loadTags();

        logger.log(Level.INFO, "加载物品...");
        loadItems();

        logger.log(Level.INFO, "加载研究项目...");
        loadResearches();

        PostSetup.setupWiki();

        // All Slimefun Listeners
        logger.log(Level.INFO, "正在注册监听器...");

        // Inject downstream extra staff
        SlimefunExtended.init(this);

        registerListeners();

        runAsync(PinyinUtils::init);

        // Initiating various Stuff and all items with a slight delay (0ms after the Server finished
        // loading)
        runSync(
                new SlimefunStartupTask(this, () -> {
                    textureService.register(registry.getAllSlimefunItems(), true);
                    permissionsService.update(registry.getAllSlimefunItems(), true);
                    soundService.reload(true);

                    // This try/catch should prevent buggy Spigot builds from blocking item loading
                    try {
                        recipeService.refresh();
                    } catch (RuntimeException | LinkageError x) {
                        logger.log(
                                Level.SEVERE,
                                x,
                                () -> "An Exception occured while iterating through the Recipe list on Minecraft"
                                        + " Version "
                                        + minecraftVersion.getName()
                                        + " (Slimefun v"
                                        + getVersion()
                                        + ")");
                    }
                }),
                0);

        // Setting up our commands
        try {
            command.register();
        } catch (RuntimeException | LinkageError x) {
            logger.log(Level.SEVERE, "An Exception occurred while registering the /slimefun command", x);
        }

        // Armor Update Task
        if (config.getBoolean("options.enable-armor-effects")) {
            new SlimefunArmorTask().schedule(this, config.getInt("options.armor-update-interval") * 20L);
            if (config.getBoolean("options.enable-radiation")) {
                new RadiationTask().schedule(this, config.getInt("options.radiation-update-interval") * 20L);
            }
            new RainbowArmorTask().schedule(this, config.getInt("options.rainbow-armor-update-interval") * 20L);
            new SolarHelmetTask().schedule(this, config.getInt("options.armor-update-interval"));
        } else if (config.getBoolean("options.enable-radiation")) {
            logger.log(Level.WARNING, "Cannot enable radiation while armor effects are disabled.");
        }

        // Starting our tasks
        AutoSavingService.start(this, config.getInt("options.auto-save-delay-in-minutes"));
        hologramsService.start();
        ticker.start(this);
        cargoTickerTask.start(this);
        logger.log(Level.INFO, "正在加载第三方插件支持...");
        integrations.start();
        logger.log(Level.INFO, "正在映射原版物品名称...");
        QsItemUtils.load();
        logger.log(Level.INFO, "共映射 {0} 个原版物品名称:", QsItemUtils.ITEM_NAME_MAPPER.keySet().size());
        logger.log(Level.INFO, QsItemUtils.getItemName(new ItemStack(Material.GRASS_BLOCK)) + "...");

        // Geyser Integration (custom skulls and items)
        new GeyserIntegration().register();
        // Hooray!
        logger.log(Level.INFO, "Slimefun 完成加载, 耗时 {0}", getStartupTime(timestamp));
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/mcchampions/Slimefun4/issues";
    }

    @Override
    public String getWikiURL() {
        return "https://slimefun-wiki.guizhanss.cn/{0}";
    }

    /**
     * This method gets called when the {@link Plugin} gets disabled.
     * Most often it is called when the {@link Server} is shutting down or reloading.
     */
    @Override
    public void onDisable() {
        // Slimefun never loaded successfully, so we don't even bother doing stuff here

        SlimefunExtended.shutdown();

        // Cancel all tasks from this plugin immediately
        Bukkit.getScheduler().cancelTasks(this);

        // Finishes all started movements/removals of block data
        ticker.setPaused(true);
        ticker.halt();
        cargoTickerTask.setPaused(true);
        cargoTickerTask.halt();

        // Save all Player Profiles that are still in memory
        PlayerProfile.iterator().forEachRemaining(profile -> {
            if (profile.isDirty()) {
                profile.save();
            }
        });

        databaseManager.shutdown();

        // Create a new backup zip
        if (cfgManager.getPluginConfig().getBoolean("options.backup-data")) {
            backupService.run();
        }

        // Terminate our Plugin instance
        instance = null;

        /*
          Close all inventories on the server to prevent item dupes
          (Incase some idiot uses /reload)
         */
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.closeInventory();
        }
    }

    /**
     * This is a private internal method to set the de-facto instance of {@link Slimefun}.
     * Having this as a seperate method ensures the seperation between static and non-static fields.
     * It also makes sonarcloud happy :)
     * Only ever use it during {@link #onEnable()} or {@link #onDisable()}.
     *
     * @param pluginInstance Our instance of {@link Slimefun} or null
     */
    private static void setInstance(@Nullable Slimefun pluginInstance) {
        instance = pluginInstance;
    }

    /**
     * This private method gives us a {@link Collection} of every {@link MinecraftVersion}
     * that Slimefun is compatible with (as a {@link String} representation).
     * <p>
     * Example:
     *
     * <pre>
     * { 1.14.x, 1.15.x, 1.16.x }
     * </pre>
     *
     * @return A {@link Collection} of all compatible minecraft versions as strings
     */
    static Collection<String> getSupportedVersions() {
        List<String> list = new ArrayList<>();

        for (MinecraftVersion version : MinecraftVersion.values()) {
            if (!version.isVirtual()) {
                list.add(version.getName());
            }
        }
        return list;
    }

    /**
     * This returns the {@link Logger} instance that Slimefun uses.
     * <p>
     * <strong>Any {@link SlimefunAddon} should use their own {@link Logger} instance!</strong>
     *
     * @return Our {@link Logger} instance
     */
    public static Logger logger() {
        return instance.getLogger();
    }

    /**
     * This returns our {@link GPSNetwork} instance.
     * The {@link GPSNetwork} is responsible for handling any GPS-related
     * operations and for managing any {@link GEOResource}.
     *
     * @return Our {@link GPSNetwork} instance
     */
    public static GPSNetwork getGPSNetwork() {
        return instance.gpsNetwork;
    }

    /**
     * This method creates all necessary directories (and sub directories) for Slimefun.
     */
    private static void createDirectories() {
        String[] storageFolders = {"waypoints", "block-backups"};
        String[] pluginFolders = {"scripts", "error-reports", "cache/github", "world-settings"};

        for (String folder : storageFolders) {
            File file = new File("data-storage/Slimefun", folder);

            if (!file.exists()) {
                file.mkdirs();
            }
        }

        for (String folder : pluginFolders) {
            File file = new File("plugins/Slimefun", folder);

            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * This method registers all of our {@link Listener Listeners}.
     */
    private void registerListeners() {
        chatCatcher = new PlayerChatCatcher(this);
        // Old deprecated CS-CoreLib Listener
        new MenuListener(this);

        new GuideListener(this);
        new SlimefunBootsListener(this);
        new SlimefunItemInteractListener(this);
        new SlimefunItemConsumeListener(this);
        new BlockPhysicsListener(this);
        new CargoNodeListener(this);
        new MultiBlockListener(this);
        new GadgetsListener(this);
        new DispenserListener(this);
        new BlockListener(this);
        new EnhancedFurnaceListener(this);
        new ItemPickupListener(this);
        new ItemDropListener(this);
        new DeathpointListener(this);
        new ExplosionsListener(this);
        new FireworksListener(this);
        new WitherListener(this);
        new IronGolemListener(this);
        new EntityInteractionListener(this);
        new MobDropListener(this);
        new VillagerTradingListener(this);
        new ElytraImpactListener(this);
        new CraftingTableListener(this);
        new AnvilListener(this);
        new BrewingStandListener(this);
        new CauldronListener(this);
        new GrindstoneListener(this);
        new CartographyTableListener(this);
        new ButcherAndroidListener(this);
        new MiningAndroidListener(this);
        new NetworkListener(this, networkManager);
        new HopperListener(this);
        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_19)) {
            new TalismanListener(this);
        }
        new SoulboundListener(this);
        new AutoCrafterListener(this);
        new SlimefunItemHitListener(this);
        new MiddleClickListener(this);
        new BeeListener(this);
        new BeeWingsListener(this, (BeeWings) SlimefunItems.BEE_WINGS.getItem());
        new PiglinListener(this);
        new SmithingTableListener(this);
        new VanillaCrafterListener(this);
        new JoinListener(this);

        // Item-specific Listeners
        new CoolerListener(this, (Cooler) SlimefunItems.COOLER.getItem());
        new SeismicAxeListener(this, (SeismicAxe) SlimefunItems.SEISMIC_AXE.getItem());
        new RadioactivityListener(this);
        new AncientAltarListener(this, (AncientAltar) SlimefunItems.ANCIENT_ALTAR.getItem(), (AncientPedestal)
                SlimefunItems.ANCIENT_PEDESTAL.getItem());
        grapplingHookListener.register(this, (GrapplingHook) SlimefunItems.GRAPPLING_HOOK.getItem());
        bowListener.register(this);
        backpackListener.register(this);

        // Handle Slimefun Guide being given on Join
        new SlimefunGuideListener(this, cfgManager.getPluginConfig().getBoolean("guide.receive-on-first-join"));

        // Clear the Slimefun Guide History upon Player Leaving
        new PlayerProfileListener(this);
    }

    /**
     * This (re)loads every {@link SlimefunTag}.
     */
    private void loadTags() {
        for (SlimefunTag tag : SlimefunTag.values()) {
            try {
                // Only reload "empty" (or unloaded) Tags
                if (tag.isEmpty()) {
                    tag.reload();
                }
            } catch (TagMisconfigurationException e) {
                getLogger().log(Level.SEVERE, e, () -> "无法加载Tag: " + tag.name());
            }
        }
    }

    /**
     * This loads all of our items.
     */
    private void loadItems() {
        try {
            SlimefunItemSetup.setup(this);
        } catch (RuntimeException | LinkageError x) {
            getLogger()
                    .log(
                            Level.SEVERE,
                            x,
                            () -> "一个 错误 发生了 在 初始化 粘液科技物品 中 ");
        }
    }

    /**
     * This loads our researches.
     */
    private void loadResearches() {
        try {
            ResearchSetup.setupResearches();
        } catch (RuntimeException | LinkageError x) {
            getLogger()
                    .log(
                            Level.SEVERE,
                            x,
                            () -> "一个 错误 发生了 在 初始化 粘液科技物品研究 中 "
                                    + getVersion());
        }
    }

    /**
     * This returns the global instance of {@link Slimefun}.
     * This may return null if the {@link Plugin} was disabled.
     *
     * @return The {@link Slimefun} instance
     */
    public static Slimefun instance() {
        return instance;
    }

    /**
     * This method returns out {@link MinecraftRecipeService} for Slimefun.
     * This service is responsible for finding/identifying {@link Recipe Recipes}
     * from vanilla Minecraft.
     *
     * @return Slimefun's {@link MinecraftRecipeService} instance
     */
    public static MinecraftRecipeService getMinecraftRecipeService() {
        return instance.recipeService;
    }

    /**
     * This returns the version of Slimefun that is currently installed.
     *
     * @return The currently installed version of Slimefun
     */
    public static String getVersion() {
        return instance.getDescription().getVersion();
    }

    public static Config getCfg() {
        return instance.cfgManager.getPluginConfig();
    }

    public static Config getResearchCfg() {
        return instance.researches;
    }

    public static Config getItemCfg() {
        return instance.items;
    }

    /**
     * This method returns out world settings service.
     * That service is responsible for managing item settings per
     * {@link World}, such as disabling a {@link SlimefunItem} in a
     * specific {@link World}.
     *
     * @return Our instance of {@link PerWorldSettingsService}
     */
    public static PerWorldSettingsService getWorldSettingsService() {
        return instance.worldSettingsService;
    }

    public static TickerTask getTickerTask() {
        return instance.ticker;
    }

    /**
     * This returns the {@link LocalizationService} of Slimefun.
     *
     * @return The {@link LocalizationService} of Slimefun
     */
    public static LocalizationService getLocalization() {
        return instance.local;
    }

    /**
     * This returns our {@link HologramsService} which handles the creation and
     * cleanup of any holograms.
     *
     * @return Our instance of {@link HologramsService}
     */
    public static HologramsService getHologramsService() {
        return instance.hologramsService;
    }

    public static CustomItemDataService getItemDataService() {
        return instance.itemDataService;
    }

    public static CustomTextureService getItemTextureService() {
        return instance.textureService;
    }

    public static PermissionsService getPermissionsService() {
        return instance.permissionsService;
    }

    public static BlockDataService getBlockDataService() {
        return instance.blockDataService;
    }

    /**
     * This returns our instance of {@link IntegrationsManager}.
     * This is responsible for managing any integrations with third party {@link Plugin plugins}.
     *
     * @return Our instance of {@link IntegrationsManager}
     */
    public static IntegrationsManager getIntegrations() {
        return instance.integrations;
    }

    /**
     * This returns out instance of the {@link ProtectionManager}.
     * This bridge is used to hook into any third-party protection {@link Plugin}.
     *
     * @return Our instanceof of the {@link ProtectionManager}
     */
    public static ProtectionManager getProtectionManager() {
        return getIntegrations().getProtectionManager();
    }

    /**
     * This returns our {@link  SoundService} which handles the configuration of all sounds used in Slimefun
     *
     * @return Our instance of {@link SoundService}
     */

    public static SoundService getSoundService() {
        return instance.soundService;
    }

    /**
     * This returns our {@link NetworkManager} which is responsible
     * for handling the Cargo and Energy networks.
     *
     * @return Our {@link NetworkManager} instance
     */
    public static NetworkManager getNetworkManager() {
        return instance.networkManager;
    }

    /**
     * This returns the time it took to load Slimefun (given a starting point).
     *
     * @param timestamp The time at which we started to load Slimefun.
     * @return The total time it took to load Slimefun (in ms or s)
     */
    private static String getStartupTime(long timestamp) {
        long ms = (System.nanoTime() - timestamp) / 1000000;

        if (ms > 1000) {
            return NumberUtils.roundDecimalNumber(ms / 1000.0) + 's';
        } else {
            return NumberUtils.roundDecimalNumber(ms) + "ms";
        }
    }

    public static SlimefunConfigManager getConfigManager() {
        return instance.cfgManager;
    }

    public static SlimefunDatabaseManager getDatabaseManager() {
        return instance.databaseManager;
    }

    public static SlimefunRegistry getRegistry() {
        return instance.registry;
    }

    public static GrapplingHookListener getGrapplingHookListener() {
        return instance.grapplingHookListener;
    }

    public static BackpackListener getBackpackListener() {
        return instance.backpackListener;
    }

    public static SlimefunBowListener getBowListener() {
        return instance.bowListener;
    }

    /**
     * The {@link Command} that was added by Slimefun.
     *
     * @return Slimefun's command
     */
    public static SlimefunCommand getCommand() {
        return instance.command;
    }

    /**
     * This returns our instance of the {@link SlimefunProfiler}, a tool that is used
     * to analyse performance and lag.
     *
     * @return The {@link SlimefunProfiler}
     */
    @Deprecated
    public static SlimefunProfiler getProfiler() {
        return instance.profiler;
    }

    @Deprecated
    public static SQLProfiler getSQLProfiler() {
        return instance.sqlProfiler;
    }

    /**
     * This returns the currently installed version of Minecraft.
     *
     * @return The current version of Minecraft
     */
    public static MinecraftVersion getMinecraftVersion() {
        return instance.minecraftVersion;
    }

    /**
     * This method returns whether this version of Slimefun was newly installed.
     * It will return true if this {@link Server} uses Slimefun for the very first time.
     *
     * @return Whether this is a new installation of Slimefun
     */
    public static boolean isNewlyInstalled() {
        return instance.isNewlyInstalled;
    }

    /**
     * This method returns a {@link Set} of every {@link Plugin} that lists Slimefun
     * as a required or optional dependency.
     * <p>
     * We will just assume this to be a list of our addons.
     *
     * @return A {@link Set} of every {@link Plugin} that is dependent on Slimefun
     */
    public static Set<Plugin> getInstalledAddons() {
        String pluginName = instance.getName();

        return Arrays.stream(instance.getServer().getPluginManager().getPlugins())
                .filter(plugin -> {
                    PluginDescriptionFile description = plugin.getDescription();
                    return description.getDepend().contains(pluginName)
                            || description.getSoftDepend().contains(pluginName);
                })
                .collect(Collectors.toSet());

    }

    /**
     * This method schedules a delayed synchronous task for Slimefun.
     * <strong>For Slimefun only, not for addons.</strong>
     * <p>
     * This method should only be invoked by Slimefun itself.
     * Addons must schedule their own tasks using their own {@link Plugin} instance.
     *
     * @param runnable The {@link Runnable} to run
     * @param delay    The delay for this task
     * @return The resulting {@link BukkitTask} or null if Slimefun was disabled
     */
    public static BukkitTask runSync(Runnable runnable, long delay) {
        return instance.getServer().getScheduler().runTaskLater(instance, runnable, delay);
    }

    /**
     * This method schedules a synchronous task for Slimefun.
     * <strong>For Slimefun only, not for addons.</strong>
     * <p>
     * This method should only be invoked by Slimefun itself.
     * Addons must schedule their own tasks using their own {@link Plugin} instance.
     *
     * @param runnable The {@link Runnable} to run
     * @return The resulting {@link BukkitTask} or null if Slimefun was disabled
     */
    public static BukkitTask runSync(Runnable runnable) {
        return bukkitScheduler.runTask(instance, runnable);
    }

    public File getFile() {
        return super.getFile();
    }

    public static PlayerChatCatcher getChatCatcher() {
        return instance.chatCatcher;
    }

    public static BukkitTask runBukkitTaskAsync(Runnable runnable) {
        return bukkitScheduler.runTaskAsynchronously(instance, runnable);
    }

    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, MISC_EXECUTOR);
    }
}
