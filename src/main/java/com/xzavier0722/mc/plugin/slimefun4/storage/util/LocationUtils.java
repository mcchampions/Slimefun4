package com.xzavier0722.mc.plugin.slimefun4.storage.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {
    /**
     * 将 {@link Location} 转换为数据库中使用的
     * 标准格式位置信息
     *
     * @param l {@link Location}
     * @return 标准化后的位置信息字符串
     */
    public static String getLocKey(Location l) {
        return l.getWorld().getName() + ";" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    /**
     * 将 {@link Chunk} 转换为数据库中使用的
     * 标准格式区块信息
     *
     * @param chunk {@link Chunk}
     * @return 标准化后的区块信息字符串
     */
    public static String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ";" + chunk.getX() + ":" + chunk.getZ();
    }

    public static String getChunkKey(Location loc) {
        return loc.getWorld().getName() + ";" + (loc.getBlockX() >> 4) + ":" + (loc.getBlockZ() >> 4);
    }

    public static Location toLocation(String lKey) {
        if (lKey == null || lKey.isEmpty()) {
            return null;
        }

        String[] strArr = lKey.split(";");
        String[] loc = strArr[1].split(":");
        return new Location(
                Bukkit.getWorld(strArr[0]),
                Double.parseDouble(loc[0]),
                Double.parseDouble(loc[1]),
                Double.parseDouble(loc[2]));
    }

    public static boolean isSameChunk(Chunk c1, Chunk c2) {
        return c1 == c2
               || (isSameWorld(c1.getWorld(), c2.getWorld()) && c1.getX() == c2.getX() && c1.getZ() == c2.getZ());
    }

    public static boolean isSameLoc(Location l1, Location l2) {
        return l1 == l2
               || (isSameChunk(l1.getChunk(), l2.getChunk())
                   && l1.getBlockX() == l2.getBlockX()
                   && l1.getBlockY() == l2.getBlockY()
                   && l1.getBlockZ() == l2.getBlockZ());
    }

    public static Chunk toChunk(World w, String cKey) {
        String[] loc = cKey.split(";")[1].split(":");
        return w.getChunkAtAsync(Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), false).join();
    }

    public static boolean isSameWorld(World w1, World w2) {
        return w1.getName().equals(w2.getName());
    }

    /**
     * 将位置转换为易读的文本
     *
     * 注意: 请不要将其用于数据库转换过程中!
     *
     * @param location 位置
     * @return 易读的位置文本
     */
    public static String locationToString(Location location) {
        if (location == null) {
            return "null";
        }

        return "[world="
               + location.getWorld().getName()
               + ",x="
               + location.getX()
               + ",y="
               + location.getY()
               + ",z="
               + location.getZ()
               + "]";
    }
}
