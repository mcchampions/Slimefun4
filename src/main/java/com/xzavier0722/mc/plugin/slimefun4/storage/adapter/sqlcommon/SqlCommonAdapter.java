package com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlcommon;

import com.xzavier0722.mc.plugin.slimefun4.storage.adapter.IDataSourceAdapter;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.DataScope;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.FieldKey;
import com.xzavier0722.mc.plugin.slimefun4.storage.common.RecordSet;
import com.xzavier0722.mc.plugin.slimefun4.storage.patch.DatabasePatch;
import com.xzavier0722.mc.plugin.slimefun4.storage.patch.DatabasePatchV1;
import com.zaxxer.hikari.HikariDataSource;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

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
    protected String tableInformationTable;
    protected T config;

    @Override
    public void prepare(T config) {
        this.config = config;
        ds = config.createDataSource();
    }

    protected void executeSql(String sql) {
        try (Connection conn = ds.getConnection()) {
            SqlUtils.execSql(conn, sql);
        } catch (Exception e) {
            Slimefun.logger().warning("执行SQL失败, 抛出异常.");
            e.printStackTrace();
        }
    }

    protected List<RecordSet> executeQuery(String sql) {
        try (Connection conn = ds.getConnection()) {
            return SqlUtils.execQuery(conn, sql);
        } catch (Exception e) {
            Slimefun.logger().warning("执行SQL失败, 抛出异常.");
            e.printStackTrace();
            return Collections.emptyList();
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
            case TABLE_INFORMATION -> tableInformationTable;
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

    public int getDatabaseVersion() {
        List<RecordSet> query = executeQuery("SELECT (" + SqlConstants.FIELD_TABLE_VERSION + ") FROM "
                                             + (tableInformationTable == null ? SqlConstants.TABLE_NAME_TABLE_INFORMATION : tableInformationTable));

        if (query.isEmpty()) {
            return 0;
        } else {
            return query.get(0).getInt(FieldKey.TABLE_VERSION);
        }
    }

    @Override
    public void patch() {
        DatabasePatch patch = null;

        if (getDatabaseVersion() == 0) {
            patch = new DatabasePatchV1();
        }

        if (patch == null) {
            return;
        }

        try (Connection conn = ds.getConnection()) {
            Slimefun.logger().log(Level.INFO, "正在更新数据库版本至 " + patch.getVersion() + ", 可能需要一段时间...");
            patch.patch(conn.createStatement(), config);
            Slimefun.logger().log(Level.INFO, "更新完成. ");
        } catch (SQLException e) {
            Slimefun.logger().log(Level.SEVERE, "更新数据库时出现问题!", e);
        }
    }
}
