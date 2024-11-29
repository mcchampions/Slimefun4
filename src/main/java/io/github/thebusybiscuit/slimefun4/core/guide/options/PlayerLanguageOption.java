package io.github.thebusybiscuit.slimefun4.core.guide.options;

import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import java.util.Optional;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Deprecated
class PlayerLanguageOption implements SlimefunGuideOption<String> {
    @Override
    public SlimefunAddon getAddon() {
        return Slimefun.instance();
    }

    @Override
    public NamespacedKey getKey() {
        return Slimefun.getLocalization().getKey();
    }

    @Override
    public Optional<ItemStack> getDisplayItem(Player p, ItemStack guide) {
        return Optional.empty();
    }

    @Override
    public void onClick(Player p, ItemStack guide) {
        openLanguageSelection(p);
    }

    @Override
    public Optional<String> getSelectedOption(Player p, ItemStack guide) {
        return Optional.of(Slimefun.getLocalization().getLanguage(p).getId());
    }

    @Override
    public void setSelectedOption(Player p, ItemStack guide, String value) {
        if (value == null) {
            PersistentDataAPI.remove(p, getKey());
        } else {
            PersistentDataAPI.setString(p, getKey(), value);
        }
    }

    private static void openLanguageSelection(Player p) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.languages"));

        menu.open(p);
    }
}
