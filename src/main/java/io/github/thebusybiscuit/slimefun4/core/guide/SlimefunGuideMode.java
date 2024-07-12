package io.github.thebusybiscuit.slimefun4.core.guide;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import lombok.Getter;

/**
 * This enum holds the different designs a {@link SlimefunGuide} can have.
 * Each constant corresponds to a {@link SlimefunGuideImplementation}.
 *
 * @author TheBusyBiscuit
 *
 * @see SlimefunGuide
 * @see SlimefunGuideImplementation
 *
 */
@Getter
public enum SlimefunGuideMode {
    /**
     * This design is the standard layout used in survival mode.
     */
    SURVIVAL_MODE("普通模式"),

    /**
     * This is an admin-only design which creates a {@link SlimefunGuide} that allows
     * you to spawn in any {@link SlimefunItem}
     */
    CHEAT_MODE("作弊模式");

    /**
     * -- GETTER --
     *  获取指南书样式的显示名称
     *
     */
    private final String displayName;

    SlimefunGuideMode(String displayName) {
        this.displayName = displayName;
    }

}
