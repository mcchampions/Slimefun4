package io.github.thebusybiscuit.slimefun4.core.guide.options;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import java.util.Optional;

import me.qscbm.slimefun4.utils.QsConstants;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class GuideModeOption implements SlimefunGuideOption<SlimefunGuideMode> {
    @Override
    public SlimefunAddon getAddon() {
        return Slimefun.instance();
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(Slimefun.instance(), "guide_mode");
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        if (!p.hasPermission("slimefun.cheat.items")) {
            // Only Players with the appropriate permission can access the cheat sheet
            return Optional.empty();
        }

        Optional<SlimefunGuideMode> current = getSelectedOption(p, guide);

        if (current.isPresent()) {
            SlimefunGuideMode selectedMode = current.get();
            ItemStack item;
            if (selectedMode == SlimefunGuideMode.SURVIVAL_MODE) {
                item = QsConstants.GUIDE_SURVIVAL_MODE_OPTION.clone();
            } else {
                item = QsConstants.GUIDE_CHEAT_MODE_OPTION.clone();
            }

            return Optional.of(item);
        }

        return Optional.empty();
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        Optional<SlimefunGuideMode> current = getSelectedOption(p, guide);

        if (current.isPresent()) {
            SlimefunGuideMode next = getNextMode(p, current.get());
            setSelectedOption(p, guide, next);
        }

        SlimefunGuideSettings.openSettings(p, guide);
    }

    private static SlimefunGuideMode getNextMode(Player p, SlimefunGuideMode mode) {
        if (p.hasPermission("slimefun.cheat.items")) {
            if (mode == SlimefunGuideMode.SURVIVAL_MODE) {
                return SlimefunGuideMode.CHEAT_MODE;
            } else {
                return SlimefunGuideMode.SURVIVAL_MODE;
            }
        } else {
            return SlimefunGuideMode.SURVIVAL_MODE;
        }
    }

    @Override
    public Optional<SlimefunGuideMode> getSelectedOption(Player p, ItemStack guide) {
        if (SlimefunUtils.isItemSimilar(guide, SlimefunGuide.getItem(SlimefunGuideMode.CHEAT_MODE), true, false)) {
            return Optional.of(SlimefunGuideMode.CHEAT_MODE);
        } else {
            return Optional.of(SlimefunGuideMode.SURVIVAL_MODE);
        }
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, SlimefunGuideMode value) {
        guide.setItemMeta(SlimefunGuide.getItem(value).getItemMeta());
    }
}
