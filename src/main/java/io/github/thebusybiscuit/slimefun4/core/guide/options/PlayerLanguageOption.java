package io.github.thebusybiscuit.slimefun4.core.guide.options;

import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerLanguageChangeEvent;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.ChatColor;
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
        openLanguageSelection(p, guide);
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

    private void openLanguageSelection(Player p, ItemStack guide) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.title.languages"));

        menu.open(p);
    }
}
