package io.github.thebusybiscuit.slimefun4.core.guide;

import city.norain.slimefun4.VaultIntegration;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This interface is used for the different implementations that add behaviour
 * to the {@link SlimefunGuide}.
 *
 * @author TheBusyBiscuit
 * @see SlimefunGuideMode
 * @see SurvivalSlimefunGuide
 */
public interface SlimefunGuideImplementation {
    /**
     * Every {@link SlimefunGuideImplementation} can be associated with a
     * {@link SlimefunGuideMode}.
     *
     * @return The mode this {@link SlimefunGuideImplementation} represents
     */

    SlimefunGuideMode getMode();

    /**
     * Returns the {@link ItemStack} representation for this {@link SlimefunGuideImplementation}.
     * In other words: The {@link ItemStack} you hold in your hand and that you use to
     * open your {@link SlimefunGuide}
     *
     * @return The {@link ItemStack} representation for this {@link SlimefunGuideImplementation}
     */

    ItemStack getItem();

    void openMainMenu(PlayerProfile profile, int page);

    void openItemGroup(PlayerProfile profile, ItemGroup group, int page);

    void openSearch(PlayerProfile profile, String input, boolean addToHistory, boolean usePinyin);

    void displayItem(PlayerProfile profile, ItemStack item, int index, boolean addToHistory);

    void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory);

    default void unlockItem(Player p, SlimefunItem sfitem, Consumer<Player> callback) {
        Research research = sfitem.getResearch();

        if (VaultIntegration.isEnabled()) {
            VaultIntegration.withdrawPlayer(p, research.getCurrencyCost());
        } else {
            p.setLevel(p.getLevel() - research.getLevelCost());
        }

        boolean skipLearningAnimation = Slimefun.getConfigManager().isLearningAnimationDisabled()
                                        || !SlimefunGuideSettings.hasLearningAnimationEnabled(p);
        research.unlock(p, skipLearningAnimation, callback);
    }
}
