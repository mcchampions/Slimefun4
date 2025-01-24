package io.github.thebusybiscuit.slimefun4.core.config;

import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.IDataSourceAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.mysql.MysqlAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.mysql.MysqlConfig;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.postgresql.PostgreSqlAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.postgresql.PostgreSqlConfig;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlite.SqliteAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlite.SqliteConfig;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ChunkDataLoadMode;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ControllerHolder;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.ProfileDataController;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.StorageType;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.annotation.Nullable;

public class SlimefunDatabaseManager {
    private static final String PROFILE_CONFIG_FILE_NAME = "profile-storage.yml";
    private static final String BLOCK_STORAGE_FILE_NAME = "block-storage.yml";
    private final Slimefun plugin;
    private final Config profileConfig;
    private final Config blockStorageConfig;
    @Getter
    private StorageType profileStorageType;
    @Getter
    private StorageType blockDataStorageType;
    private IDataSourceAdapter<?> profileAdapter;
    private IDataSourceAdapter<?> blockStorageAdapter;

    public SlimefunDatabaseManager(Slimefun plugin) {
        this.plugin = plugin;

        if (!new File(plugin.getDataFolder(), PROFILE_CONFIG_FILE_NAME).exists()) {
            plugin.saveResource(PROFILE_CONFIG_FILE_NAME, false);
        }

        if (!new File(plugin.getDataFolder(), BLOCK_STORAGE_FILE_NAME).exists()) {
            plugin.saveResource(BLOCK_STORAGE_FILE_NAME, false);
        }

        profileConfig = new Config(plugin, PROFILE_CONFIG_FILE_NAME);
        blockStorageConfig = new Config(plugin, BLOCK_STORAGE_FILE_NAME);
    }

    public void init() {
        initDefaultVal();

        try {
            blockDataStorageType = StorageType.valueOf(blockStorageConfig.getString("storageType"));
            int readExecutorThread = blockStorageConfig.getInt("readExecutorThread");
            int writeExecutorThread = blockStorageConfig.getInt("writeExecutorThread");

            initAdapter(blockDataStorageType, DataType.BLOCK_STORAGE, blockStorageConfig);

            BlockDataController blockDataController =
                    ControllerHolder.createController(BlockDataController.class, blockDataStorageType);
            blockDataController.init(blockStorageAdapter, readExecutorThread, writeExecutorThread);

            if (blockStorageConfig.getBoolean("delayedWriting.enable")) {
                plugin.getLogger().log(Level.INFO, "已启用延时写入功能");
                blockDataController.initDelayedSaving(
                        plugin,
                        blockStorageConfig.getInt("delayedWriting.delayedSecond"),
                        blockStorageConfig.getInt("delayedWriting.forceSavePeriod"));
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "加载 Slimefun 方块存储适配器失败", e);
            return;
        }

        try {
            profileStorageType = StorageType.valueOf(profileConfig.getString("storageType"));
            int readExecutorThread = profileConfig.getInt("readExecutorThread");
            int writeExecutorThread =
                    profileStorageType == StorageType.SQLITE ? 1 : profileConfig.getInt("writeExecutorThread");

            initAdapter(profileStorageType, DataType.PLAYER_PROFILE, profileConfig);
            ProfileDataController profileController = ControllerHolder.createController(ProfileDataController.class, profileStorageType);
            profileController.init(profileAdapter, readExecutorThread, writeExecutorThread);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "加载玩家档案适配器失败", e);
        }
    }

    private void initAdapter(StorageType storageType, DataType dataType, Config databaseConfig) throws IOException {
        switch (storageType) {
            case MYSQL -> {
                MysqlAdapter adapter = new MysqlAdapter();

                adapter.prepare(new MysqlConfig(
                        databaseConfig.getString("mysql.host"),
                        databaseConfig.getInt("mysql.port"),
                        databaseConfig.getString("mysql.database"),
                        databaseConfig.getString("mysql.tablePrefix"),
                        databaseConfig.getString("mysql.user"),
                        databaseConfig.getString("mysql.password"),
                        databaseConfig.getBoolean("mysql.useSSL"),
                        databaseConfig.getInt("mysql.maxConnection")));

                switch (dataType) {
                    case BLOCK_STORAGE -> blockStorageAdapter = adapter;
                    case PLAYER_PROFILE -> profileAdapter = adapter;
                }
            }
            case SQLITE -> {
                SqliteAdapter adapter = new SqliteAdapter();

                File databasePath = null;

                switch (dataType) {
                    case PLAYER_PROFILE -> {
                        databasePath = new File("data-storage/Slimefun", "profile.db");
                        profileAdapter = adapter;
                    }
                    case BLOCK_STORAGE -> {
                        databasePath = new File("data-storage/Slimefun", "block-storage.db");
                        blockStorageAdapter = adapter;
                    }
                }
                adapter.prepare(new SqliteConfig(
                        databasePath.getAbsolutePath(), databaseConfig.getInt("sqlite.maxConnection")));
            }
            case POSTGRESQL -> {
                PostgreSqlAdapter adapter = new PostgreSqlAdapter();

                adapter.prepare(new PostgreSqlConfig(
                        databaseConfig.getString("postgresql.host"),
                        databaseConfig.getInt("postgresql.port"),
                        databaseConfig.getString("postgresql.database"),
                        databaseConfig.getString("postgresql.tablePrefix"),
                        databaseConfig.getString("postgresql.user"),
                        databaseConfig.getString("postgresql.password"),
                        databaseConfig.getBoolean("postgresql.useSSL"),
                        databaseConfig.getInt("postgresql.maxConnection")));

                switch (dataType) {
                    case BLOCK_STORAGE -> blockStorageAdapter = adapter;
                    case PLAYER_PROFILE -> profileAdapter = adapter;
                }
            }
            default -> throw new IOException("错误的存储类型:" + storageType.name());
        }
    }

    @Nullable
    public ProfileDataController getProfileDataController() {
        return ControllerHolder.getController(ProfileDataController.class, profileStorageType);
    }

    public BlockDataController getBlockDataController() {
        return ControllerHolder.getController(BlockDataController.class, blockDataStorageType);
    }

    public void shutdown() {
        if (getProfileDataController() != null) {
            getProfileDataController().shutdown();
        }

        if (getBlockDataController() != null) {
            getBlockDataController().shutdown();
        }

        if (blockStorageAdapter != null) {
            blockStorageAdapter.shutdown();
        }

        if (profileAdapter != null) {
            profileAdapter.shutdown();
        }

        ControllerHolder.clearControllers();
    }

    public boolean isBlockDataBase64Enabled() {
        return blockStorageConfig.getBoolean("base64EncodeVal");
    }

    public boolean isProfileDataBase64Enabled() {
        return profileConfig.getBoolean("base64EncodeVal");
    }

    public ChunkDataLoadMode getChunkDataLoadMode() {
        return ChunkDataLoadMode.valueOf(blockStorageConfig.getString("dataLoadMode"));
    }

    private void initDefaultVal() {
        profileConfig.setDefaultValue("sqlite.maxConnection", 5);
        profileConfig.save();
        blockStorageConfig.setDefaultValue("sqlite.maxConnection", 5);
        blockStorageConfig.setDefaultValue("dataLoadMode", "LOAD_WITH_CHUNK");
        blockStorageConfig.save();
    }
}
