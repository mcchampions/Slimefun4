package io.github.thebusybiscuit.slimefun4.implementation.items.altar;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotHopperable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockDispenseHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.AncientAltarListener;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AncientAltarTask;
import io.github.thebusybiscuit.slimefun4.utils.ArmorStandUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.Optional;
import javax.annotation.Nullable;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 * The {@link AncientPedestal} is a part of the {@link AncientAltar}.
 * You can place any {@link ItemStack} onto the {@link AncientPedestal} to provide it to
 * the altar as a crafting ingredient.
 *
 * @author Redemption198
 * @author TheBusyBiscuit
 * @author JustAHuman
 *
 * @see AncientAltar
 * @see AncientAltarListener
 * @see AncientAltarTask
 *
 */
public class AncientPedestal extends SimpleSlimefunItem<BlockDispenseHandler> implements NotHopperable {
    public static final String ITEM_PREFIX = "§dALTAR §3Probe - §e";

    public AncientPedestal(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);

        addItemHandler(onBreak());
    }

    private BlockBreakHandler onBreak() {
        return new SimpleBlockBreakHandler() {
            @Override
            public void onBlockBreak(Block b) {
                Optional<Item> entity = getPlacedItem(b);
                ArmorStand armorStand = getArmorStand(b, false);

                if (entity.isPresent()) {
                    Item stack = entity.get();

                    if (stack.isValid()) {
                        stack.removeMetadata("no_pickup", Slimefun.instance());
                        b.getWorld().dropItem(b.getLocation(), getOriginalItemStack(stack));
                        stack.remove();
                    }
                }

                if (armorStand != null && armorStand.isValid()) {
                    armorStand.remove();
                }
            }
        };
    }

    @Override
    public BlockDispenseHandler getItemHandler() {
        return (e, d, block, machine) -> e.setCancelled(true);
    }

    public static Optional<Item> getPlacedItem(Block pedestal) {
        Location l = pedestal.getLocation().add(0.5, 1.2, 0.5);

        for (Entity n : l.getWorld().getNearbyEntities(l, 0.5, 0.5, 0.5, AncientPedestal::testItem)) {
            if (n instanceof Item item) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public static @Nullable ArmorStand getArmorStand(Block pedestal, boolean createIfNoneExists) {
        Optional<Item> entity = getPlacedItem(pedestal);

        if (entity.isPresent() && entity.get().getVehicle() instanceof ArmorStand armorStand) {
            return armorStand;
        }

        Location l = pedestal.getLocation().add(0.5, 1.2, 0.5);
        for (Entity n : l.getWorld().getNearbyEntities(l, 0.5, 0.5, 0.5, AncientPedestal::testArmorStand)) {
            if (n instanceof ArmorStand armorStand) {
                return armorStand;
            }
        }

        return createIfNoneExists ? ArmorStandUtils.spawnArmorStand(l) : null;
    }

    public static boolean testItem(@Nullable Entity n) {
        if (n instanceof Item item && n.isValid()) {
            ItemMeta meta = item.getItemStack().getItemMeta();

            return meta.hasDisplayName() && meta.getDisplayName().startsWith(ITEM_PREFIX);
        } else {
            return false;
        }
    }

    private static boolean testArmorStand(@Nullable Entity n) {
        if (n instanceof ArmorStand && n.isValid()) {
            String customName = n.getCustomName();
            return customName != null && customName.startsWith(ITEM_PREFIX);
        } else {
            return false;
        }
    }

    public static ItemStack getOriginalItemStack(Item item) {
        ItemStack stack = item.getItemStack().clone();
        ItemMeta im = stack.getItemMeta();
        String customName = item.getCustomName();
        im.setDisplayName(null);
        stack.setItemMeta(im);

        if (customName == null || !customName.equals(ItemUtils.getItemName(stack))) {
            im.setDisplayName(customName);
            stack.setItemMeta(im);
        }

        return stack;
    }

    public static void placeItem(Player p, Block b) {
        ItemStack hand = p.getInventory().getItemInMainHand();
        String displayName = ITEM_PREFIX + System.nanoTime();
        ItemStack displayItem = new CustomItemStack(hand, displayName);
        displayItem.setAmount(1);

        // Get the display name of the original Item in the Player's hand
        Component nametag = Bukkit.getItemFactory().displayName(hand);

        if (p.getGameMode() != GameMode.CREATIVE) {
            ItemUtils.consumeItem(hand, false);
        }

        Item entity = SlimefunUtils.spawnItem(
                b.getLocation().add(0.5, 1.2, 0.5), displayItem, ItemSpawnReason.ANCIENT_PEDESTAL_PLACE_ITEM, false, p);

        if (entity != null) {
            ArmorStand armorStand = getArmorStand(b, true);
            entity.setInvulnerable(true);
            entity.setVelocity(new Vector(0, 0.1, 0));
            entity.setCustomNameVisible(true);
            entity.customName(nametag);
            armorStand.setCustomName(displayName);
            armorStand.addPassenger(entity);
            SlimefunUtils.markAsNoPickup(entity, "altar_item");
            SoundEffect.ANCIENT_PEDESTAL_ITEM_PLACE_SOUND.playAt(b);
        }
    }
}
