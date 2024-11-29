package com.xzavier0722.mc.plugin.slimefun4.storage.adapter.sqlcommon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Supplier;

public class ConnectionPool {
    private final Supplier<Connection> connCreator;
    private final int maxConnCount;
    private final Deque<Connection> freeConn;
    private final Set<Connection> usingConn;

    private boolean destroyed;
    private int currConnCount;
    private int waitingCount;

    public ConnectionPool(Supplier<Connection> connCreator, int maxConnCount) {
        this.connCreator = connCreator;
        this.maxConnCount = maxConnCount;
        this.freeConn = new LinkedList<>();
        this.usingConn = new HashSet<>();
    }

    public synchronized Connection getConn() throws InterruptedException {
        checkDestroy();

        if (freeConn.isEmpty()) {
            if (currConnCount >= maxConnCount) {
                waitingCount++;
                wait();
                return getConn();
            }

            var re = connCreator.get();
            currConnCount++;
            usingConn.add(re);
            return re;
        } else {
            var re = freeConn.poll();
            if (!testConn(re)) {
                currConnCount--;
                return getConn();
            }
            usingConn.add(re);
            return re;
        }
    }

    public synchronized void releaseConn(Connection conn) {
        checkDestroy();

        if (!usingConn.remove(conn)) {
            return;
        }

        freeConn.add(conn);

        if (waitingCount > 0) {
            notify();
            waitingCount--;
        }
    }

    public synchronized void destroy() {
        checkDestroy();

        destroyed = true;
        freeConn.forEach(ConnectionPool::tryClose);
        usingConn.forEach(ConnectionPool::tryClose);
    }

    private static boolean testConn(Connection conn) {
        try (var stmt = conn.createStatement()) {
            stmt.execute("/* ping */ SHOW DATABASES");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void checkDestroy() {
        if (destroyed) {
            throw new IllegalStateException("Connection pool cannot be accessed after destroy() called");
        }
    }

    private static void tryClose(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
