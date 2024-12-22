package com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlcommon;

import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.IDataSourceAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataScope;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.RecordSet;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class SqlCommonAdapter<T extends ISqlCommonConfig> implements IDataSourceAdapter<T> {
    protected HikariDataSource ds;
    protected String profileTable, researchTable, backpackTable, bpInvTable;
    protected String blockRecordTable,
            blockDataTable,
            universalRecordTable,
            universalDataTable,
            chunkDataTable,
            blockInvTable,
            universalInvTable;
    protected T config;

    @Override
    public void prepare(T config) {
        this.config = config;
        ds = config.createDataSource();
    }

    protected void executeSql(String sql) {
        try (Connection conn = ds.getConnection()) {
            SqlUtils.execSql(conn, sql);
        } catch (SQLException e) {
            throw new IllegalStateException("An exception thrown while executing sql: " + sql, e);
        }
    }

    protected List<RecordSet> executeQuery(String sql) {
        try (Connection conn = ds.getConnection()) {
            return SqlUtils.execQuery(conn, sql);
        } catch (SQLException e) {
            throw new IllegalStateException("An exception thrown while executing sql: " + sql, e);
        }
    }

    protected String mapTable(DataScope scope) {
        return switch (scope) {
            case PLAYER_PROFILE -> profileTable;
            case BACKPACK_INVENTORY -> bpInvTable;
            case BACKPACK_PROFILE -> backpackTable;
            case PLAYER_RESEARCH -> researchTable;
            case BLOCK_INVENTORY -> blockInvTable;
            case CHUNK_DATA -> chunkDataTable;
            case BLOCK_DATA -> blockDataTable;
            case BLOCK_RECORD -> blockRecordTable;
            case UNIVERSAL_INVENTORY -> universalInvTable;
            case UNIVERSAL_RECORD -> universalRecordTable;
            case UNIVERSAL_DATA -> universalDataTable;
            case NONE -> throw new IllegalArgumentException("NONE cannot be a storage data scope!");
        };
    }

    @Override
    public void shutdown() {
        ds.close();
        ds = null;
        profileTable = null;
        researchTable = null;
        backpackTable = null;
        bpInvTable = null;
        blockDataTable = null;
        blockRecordTable = null;
        chunkDataTable = null;
        blockInvTable = null;
        universalInvTable = null;
        universalDataTable = null;
        universalRecordTable = null;
    }
}
