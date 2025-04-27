package com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * 这个属性用于声明 {@link SlimefunItem} 使用了 {@link SlimefunUniversalData}
 * <p>
 * 当这个 {@link SlimefunItem} 作为机器时, 对应材质需要支持
 * 使用 PDC 存储容器 (用于识别 UUID).
 * 否则无法将这个物品/机器绑定到一个通用数据上.
 *
 * 查看此处了解支持 PDC 的物品材质:
 * <a href="https://jd.papermc.io/paper/1.21/org/bukkit/block/TileState.html">Paper Doc</a>
 *
 * @author NoRainCity
 *
 * @see SlimefunUniversalData
 * @see SlimefunUniversalBlockData
 */
public interface UniversalBlock {}
