package io.github.thebusybiscuit.slimefun4.implementation.tasks.armor;

import io.github.thebusybiscuit.slimefun4.api.items.HashedArmorpiece;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.RainbowArmorPiece;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * The {@link RainbowArmorTask} is responsible for handling the change in color of any Rainbow Armor piece.
 *
 * @author martinbrom
 */
public class RainbowArmorTask extends AbstractArmorTask {
    private long currentColorIndex;

    @Override
    protected void onTick() {
        currentColorIndex++;
    }

    @Override
    protected void onPlayerTick(Player p, PlayerProfile profile) {
        for (int i = 0; i < 4; i++) {
            ItemStack item = p.getInventory().getArmorContents()[i];

            if (item != null && item.hasItemMeta()) {
                HashedArmorpiece armorPiece = profile.getArmor()[i];

                armorPiece.getItem().ifPresent(sfArmorPiece -> {
                    if (sfArmorPiece instanceof RainbowArmorPiece rainbowArmorPiece
                            && rainbowArmorPiece.canUse(p, true)) {
                        updateRainbowArmor(item, rainbowArmorPiece);
                    }
                });
            }
        }
    }

    private void updateRainbowArmor(ItemStack itemStack, RainbowArmorPiece armorPiece) {
        Color[] colors = armorPiece.getColors();
        Color newColor = colors[(int) (currentColorIndex % colors.length)];

        if (itemStack.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(newColor);
            itemStack.setItemMeta(leatherArmorMeta);
        }
    }
}
