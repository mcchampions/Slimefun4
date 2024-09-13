package com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlcommon;

import city.norain.slimefun4.SlimefunExtended;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class SqlCommonConfig implements ISqlCommonConfig {
    protected final String host;
    protected final int port;
    protected final String database;
    protected final String tablePrefix;
    protected final String user;
    protected final String passwd;
    protected final boolean useSsl;
    protected final int maxConnection;

    public SqlCommonConfig(
            String host,
            int port,
            String database,
            String tablePrefix,
            String user,
            String passwd,
            boolean useSsl,
            int maxConnection) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.tablePrefix = tablePrefix;
        this.user = user;
        this.passwd = passwd;
        this.useSsl = useSsl;
        this.maxConnection = maxConnection;
    }

    public HikariDataSource createDataSource() {
        var config = new HikariConfig();
        config.setDriverClassName(driver());
        config.setJdbcUrl(jdbcUrl());
        config.setPoolName("SlimefunHikariPool");

        if (!user.isEmpty()) {
            config.setUsername(user);
        }

        if (!passwd.isEmpty()) {
            config.setPassword(passwd);
        }

        config.setMaximumPoolSize(maxConnection);

        /* ===================
         * DATABASE DEBUG MODE
         * ===================
         */
        if (SlimefunExtended.isDatabaseDebugMode()) {
            config.setLeakDetectionThreshold(5000);
        }

        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        return new HikariDataSource(config);
    }

    public String tablePrefix() {
        return tablePrefix;
    }
}
