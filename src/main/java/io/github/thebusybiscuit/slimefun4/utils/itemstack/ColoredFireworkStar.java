package io.github.thebusybiscuit.slimefun4.utils.itemstack;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.compatibility.VersionedItemFlag;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;

/**
 * This simple {@link ItemStack} implementation allows us to obtain
 * a colored {@code Material.FIREWORK_STAR} {@link ItemStack} quickly.
 *
 * @author TheBusyBiscuit
 *
 */
public class ColoredFireworkStar extends CustomItemStack {
    public ColoredFireworkStar(Color color, String name, String... lore) {
        super(Material.FIREWORK_STAR, im -> {
            im.setDisplayName(ChatColors.color(name));

            ((FireworkEffectMeta) im)
                    .setEffect(FireworkEffect.builder()
                            .with(Type.BURST)
                            .withColor(color)
                            .build());

            if (lore.length > 0) {
                List<String> lines = new ArrayList<>();

                for (String line : lore) {
                    lines.add(ChatColors.color(line));
                }

                im.setLore(lines);
            }

            im.addItemFlags(VersionedItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        });
    }
}
