package io.github.thebusybiscuit.slimefun4.utils;

import city.norain.slimefun4.SlimefunExtended;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.bakedlibs.dough.items.ItemMetaSnapshot;
import io.github.bakedlibs.dough.skins.PlayerHead;
import io.github.bakedlibs.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunItemSpawnEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.CapacitorTextureUpdateTask;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This utility class holds method that are directly linked to Slimefun.
 * It provides a very crucial method for {@link ItemStack} comparison, as well as a simple method
 * to check if an {@link ItemStack} is {@link Soulbound} or not.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 * @author Sfiguz7
 */
public final class SlimefunUtils {
    private static final String NO_PICKUP_METADATA = "no_pickup";
    private static final Component SOULBOUND_LORE = Component.text().color(NamedTextColor.GRAY).content("灵魂绑定").build();

    private static final String SOULBOUND_LORE_OLD = LegacyComponentSerializer.legacySection().serialize(SOULBOUND_LORE);

    private SlimefunUtils() {
    }

    /**
     * This method quickly returns whether an {@link Item} was marked as "no_pickup" by
     * a Slimefun device.
     *
     * @param item The {@link Item} to query
     * @return Whether the {@link Item} is excluded from being picked up
     */
    public static boolean hasNoPickupFlag(Item item) {
        return item.hasMetadata(NO_PICKUP_METADATA);
    }

    /**
     * This will prevent the given {@link Item} from being picked up.
     * This is useful for display items which the {@link AncientPedestal} uses.
     *
     * @param item    The {@link Item} to prevent from being picked up
     * @param context The context in which this {@link Item} was flagged
     */
    public static void markAsNoPickup(Item item, String context) {
        item.setMetadata(NO_PICKUP_METADATA, new FixedMetadataValue(Slimefun.instance(), context));
        /*
         * Max the pickup delay - This makes it so no Player can pick up items ever without need for an event.
         * It is also an indication used by third-party plugins to know if it's a custom item.
         * Fixes #3203
         */
        item.setPickupDelay(Short.MAX_VALUE);
    }

    /**
     * This method checks whether the given {@link ItemStack} is considered {@link Soulbound}.
     *
     * @param item The {@link ItemStack} to check for
     * @return Whether the given item is soulbound
     */
    public static boolean isSoulbound(@Nullable ItemStack item) {
        return isSoulbound(item, null);
    }

    /**
     * This method checks whether the given {@link ItemStack} is considered {@link Soulbound}.
     * If the provided item is a {@link SlimefunItem} then this method will also check that the item
     * is enabled in the provided {@link World}.
     * If the provided item is {@link Soulbound} through the {@link SlimefunItems#SOULBOUND_RUNE}, then this
     * method will also check that the {@link SlimefunItems#SOULBOUND_RUNE} is enabled in the provided {@link World}
     *
     * @param item  The {@link ItemStack} to check for
     * @param world The {@link World} to check if the {@link SlimefunItem} is enabled in if applicable.
     *              If {@code null} then this will not do a world check.
     * @return Whether the given item is soulbound
     */
    public static boolean isSoulbound(@Nullable ItemStack item, @Nullable World world) {
        if (item != null && item.getType() != Material.AIR) {
            ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : null;

            SlimefunItem rune = SlimefunItems.SOULBOUND_RUNE.getItem();
            if (rune != null && !rune.isDisabled() && (world == null || !rune.isDisabledIn(world)) && hasSoulboundFlag(meta)) {
                return true;
            }

            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem instanceof Soulbound) {
                if (world != null) {
                    return !sfItem.isDisabledIn(world);
                } else {
                    return !sfItem.isDisabled();
                }
            } else if (meta != null) {
                return meta.hasLore() && meta.lore().contains(SOULBOUND_LORE);
            }
        }
        return false;
    }

    private static boolean hasSoulboundFlag(@Nullable ItemMeta meta) {
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = Slimefun.getRegistry().getSoulboundDataKey();

            return container.has(key, PersistentDataType.BYTE);
        }

        return false;
    }

    /**
     * Toggles an {@link ItemStack} to be Soulbound.<br>
     * If true is passed, this will add the {@link #SOULBOUND_LORE} and
     * add a {@link NamespacedKey} to the item so it can be quickly identified
     * by {@link #isSoulbound(ItemStack)}.<br>
     * If false is passed, this property will be removed.
     *
     * @param item          The {@link ItemStack} you want to add/remove Soulbound from.
     * @param makeSoulbound If the item should be soulbound.
     * @see #isSoulbound(ItemStack)
     */
    public static void setSoulbound(@Nullable ItemStack item, boolean makeSoulbound) {
        boolean isSoulbound = isSoulbound(item);
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = Slimefun.getRegistry().getSoulboundDataKey();

        if (makeSoulbound && !isSoulbound) {
            container.set(key, PersistentDataType.BYTE, (byte) 1);
        }

        if (!makeSoulbound && isSoulbound) {
            container.remove(key);
        }

        List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();

        if (makeSoulbound && !isSoulbound) {
            lore.add(SOULBOUND_LORE);
        }

        if (!makeSoulbound && isSoulbound) {
            lore.remove(SOULBOUND_LORE);
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    /**
     * This method checks whether the given {@link ItemStack} is radioactive.
     *
     * @param item The {@link ItemStack} to check
     * @return Whether this {@link ItemStack} is radioactive or not
     */
    public static boolean isRadioactive(@Nullable ItemStack item) {
        return SlimefunItem.getByItem(item) instanceof Radioactive;
    }

    /**
     * This method returns an {@link ItemStack} for the given texture.
     * The result will be a Player Head with this texture.
     *
     * @param texture The texture for this head (base64 or hash)
     * @return An {@link ItemStack} with this Head texture
     */
    public static ItemStack getCustomHead(String texture) {
        String base64 = texture;

        if (CommonPatterns.HEXADECIMAL.matcher(texture).matches()) {
            base64 = Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + texture + "\"}}}").getBytes(StandardCharsets.UTF_8));
        }

        PlayerSkin skin = PlayerSkin.fromBase64(base64);
        return PlayerHead.getItemStack(skin);
    }

    public static boolean containsSimilarItem(Inventory inventory, ItemStack item, boolean checkLore) {
        if (inventory == null || item == null) {
            return false;
        }

        // Performance optimization
        if (!(item instanceof SlimefunItemStack)) {
            item = ItemStackWrapper.wrap(item);
        }

        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }

            if (isItemSimilar(stack, item, checkLore, false)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isItemSimilar(@Nullable ItemStack item, @Nullable ItemStack sfitem, boolean checkLore) {
        return isItemSimilar(item, sfitem, checkLore, true, true, true);
    }

    public static boolean isItemSimilar(@Nullable ItemStack item, @Nullable ItemStack sfitem, boolean checkLore, boolean checkAmount) {
        return isItemSimilar(item, sfitem, checkLore, checkAmount, true, true);
    }

    public static boolean isItemSimilar(@Nullable ItemStack item, @Nullable ItemStack sfitem, boolean checkLore, boolean checkAmount, boolean checkDistinctiveItem) {
        return isItemSimilar(item, sfitem, checkLore, checkAmount, checkDistinctiveItem, true);
    }

    public static boolean isSlimefunItemSimilar(SlimefunItemStack sfItem, ItemStack item, boolean checkLore) {
        SlimefunItem sfI = SlimefunItem.getByItem(item);
        if (sfI == null) {
            return false;
        }
        ItemStack tempStack = sfI.getItem();
        SlimefunItemStack sfItemStack = (SlimefunItemStack) tempStack;
        if (checkLore) {
            List<Component> lores1 = sfItem.lore();
            List<Component> lores2 = item.lore();
            if (lores1 == null || lores2 == null) {
                return false;
            }
            if (lores1.size() != lores2.size()) {
                return false;
            }
            for (int i = 0; i < lores1.size(); i++) {
                if (!lores1.get(i).equals(lores2.get(i))) {
                    return false;
                }
            }
        }
        if (sfItem.getItemId().equals(sfItemStack.getItemId())) {
            if (sfItem instanceof DistinctiveItem && sfItemStack instanceof DistinctiveItem distinctiveItem) {
                return distinctiveItem.canStack(sfItem.getItemMeta(), item.getItemMeta());
            }
            return true;
        }
        return false;
    }

    public static boolean isItemSimilar(@Nullable ItemStack item, @Nullable ItemStack sfitem, boolean checkLore, boolean checkAmount, boolean checkDistinctiveItem, boolean checkCustomModelData) {
        if (item == null) {
            return sfitem == null;
        } else if (sfitem == null || item.getType() != sfitem.getType() || checkAmount && item.getAmount() < sfitem.getAmount()) {
            return false;
        } else if (checkDistinctiveItem && sfitem instanceof SlimefunItemStack stackOne && item instanceof SlimefunItemStack stackTwo) {
            return isSlimefunItemSimilar(stackOne, stackTwo, checkLore);
        } else if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            if (sfitem instanceof SlimefunItemStack sfItemStack) {
                String id = Slimefun.getItemDataService().getItemData(itemMeta).orElse(null);

                if (id != null) {
                    // to fix issue #976
                    if (id.equals(sfItemStack.getItemId())) {
                        if (checkDistinctiveItem) {
                            /*
                             * PR #3417
                             *
                             * Some items can't rely on just IDs matching and will implement Distinctive Item
                             * in which case we want to use the method provided to compare
                             */
                            Optional<DistinctiveItem> optionalDistinctive = getDistinctiveItem(id);
                            if (optionalDistinctive.isPresent()) {
                                ItemMeta sfItemMeta = sfitem.getItemMeta();
                                return optionalDistinctive.get().canStack(sfItemMeta, itemMeta);
                            }
                        }
                        return true;
                    }
                    return false;
                }

                ItemMetaSnapshot meta = sfItemStack.getItemMetaSnapshot();
                return equalsItemMeta(itemMeta, meta, checkLore);
            } else if (sfitem instanceof ItemStackWrapper && sfitem.hasItemMeta()) {
                /*
                 * Cargo optimization (PR #3258)
                 *
                 * Slimefun items may be ItemStackWrapper's in the context of cargo
                 * so let's try to do an ID comparison before meta comparison
                 */
                ItemMeta sfItemMeta = sfitem.getItemMeta();
                String possibleItemId = Slimefun.getItemDataService().getItemData(itemMeta).orElse(null);
                String sfItemId = Slimefun.getItemDataService().getItemData(sfItemMeta).orElse(null);
                // Prioritize SlimefunItem id comparison over ItemMeta comparison
                if (possibleItemId != null) {
                    if (!(possibleItemId.equals(sfItemId))) {
                        return false;
                    }
                    /*
                     * PR #3417
                     *
                     * Some items can't rely on just IDs matching and will implement Distinctive Item
                     * in which case we want to use the method provided to compare
                     */
                    Optional<DistinctiveItem> optionalDistinctive = getDistinctiveItem(possibleItemId);
                    return optionalDistinctive.map(distinctiveItem -> distinctiveItem.canStack(sfItemMeta, itemMeta)).orElse(true);
                }
                return false;
            } else if (sfitem.hasItemMeta()) {
                ItemMeta sfItemMeta = sfitem.getItemMeta();

                return equalsItemMeta(itemMeta, sfItemMeta, checkLore, checkCustomModelData);
            } else {
                return false;
            }
        } else {
            return !sfitem.hasItemMeta();
        }
    }

    private static Optional<DistinctiveItem> getDistinctiveItem(String id) {
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem instanceof DistinctiveItem distinctiveItem) {
            return Optional.of(distinctiveItem);
        }
        return Optional.empty();
    }

    public static boolean equalsItemMeta(ItemMeta itemMeta, ItemMetaSnapshot itemMetaSnapshot, boolean checkLore) {
        Optional<Component> displayName = itemMetaSnapshot.displayName();

        if (itemMeta.hasDisplayName() != displayName.isPresent()) {
            return false;
        } else //noinspection DataFlowIssue
            if (itemMeta.hasDisplayName() && displayName.isPresent() && !itemMeta.displayName().equals(displayName.get())) {
                return false;
            } else if (checkLore) {
                Optional<List<String>> itemLore = itemMetaSnapshot.getLore();

                //noinspection DataFlowIssue
                if (itemMeta.hasLore() && itemLore.isPresent() && !equalsLore(itemMeta.getLore(), itemLore.get())) {
                    return false;
                } else if (itemMeta.hasLore() != itemLore.isPresent()) {
                    return false;
                }
            }

        // Fixes #3133: name and lore are not enough
        OptionalInt itemCustomModelData = itemMetaSnapshot.getCustomModelData();
        if (itemMeta.hasCustomModelData() && itemCustomModelData.isPresent() && itemMeta.getCustomModelData() != itemCustomModelData.getAsInt()) {
            return false;
        } else {
            return itemMeta.hasCustomModelData() == itemCustomModelData.isPresent();
        }
    }

    private static boolean equalsItemMeta(ItemMeta itemMeta, ItemMeta sfitemMeta, boolean checkLore, boolean checkCustomModelCheck) {
        if (itemMeta.hasDisplayName() != sfitemMeta.hasDisplayName()) {
            return false;
        } else //noinspection DataFlowIssue
            if (itemMeta.hasDisplayName() && sfitemMeta.hasDisplayName() && !itemMeta.displayName().equals(sfitemMeta.displayName())) {
                return false;
            } else if (checkLore) {
                boolean hasItemMetaLore = itemMeta.hasLore();
                boolean hasSfItemMetaLore = sfitemMeta.hasLore();

                if (hasItemMetaLore && hasSfItemMetaLore) {
                    //noinspection DataFlowIssue
                    if (!equalsLoreNew(itemMeta.lore(), sfitemMeta.lore())) {
                        return false;
                    }
                } else if (hasItemMetaLore != hasSfItemMetaLore) {
                    return false;
                }
            }

        if (checkCustomModelCheck) {
            // Fixes #3133: name and lore are not enough
            boolean hasItemMetaCustomModelData = itemMeta.hasCustomModelData();
            boolean hasSfItemMetaCustomModelData = sfitemMeta.hasCustomModelData();
            if (hasItemMetaCustomModelData && hasSfItemMetaCustomModelData && itemMeta.getCustomModelData() != sfitemMeta.getCustomModelData()) {
                return false;
            } else if (hasItemMetaCustomModelData != hasSfItemMetaCustomModelData) {
                return false;
            }
        }

        if (itemMeta instanceof PotionMeta potionMeta && sfitemMeta instanceof PotionMeta sfPotionMeta) {
            if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20_5)) {
                //noinspection ConstantValue
                if (potionMeta.getBasePotionType() == null) {
                    return true;
                }

                //noinspection ConstantValue
                return potionMeta.getBasePotionType() != null
                       && sfPotionMeta.getBasePotionType() != null
                       && potionMeta.getBasePotionType() == sfPotionMeta.getBasePotionType();
            } else if (SlimefunExtended.getMinecraftVersion().isAtLeast(1, 20, 2)) {
                return potionMeta.getBasePotionType() == sfPotionMeta.getBasePotionType();
            } else {
                //noinspection deprecation
                return potionMeta.getBasePotionData().equals(sfPotionMeta.getBasePotionData());
            }
        }
        return true;
    }

    /**
     * This checks if the two provided lores are equal.
     * This method will ignore any lines such as the soulbound one.
     *
     * @param lore1 The first lore
     * @param lore2 The second lore
     * @return Whether the two lores are equal
     */
    public static boolean equalsLoreNew(List<Component> lore1, List<Component> lore2) {
        List<Component> longerList = lore1.size() > lore2.size() ? lore1 : lore2;
        List<Component> shorterList = lore1.size() > lore2.size() ? lore2 : lore1;

        int a = 0;
        int b = 0;

        for (; a < longerList.size(); a++) {
            if (isLineIgnored(longerList.get(a))) {
                continue;
            }

            while (shorterList.size() > b && isLineIgnored(shorterList.get(b))) {
                b++;
            }

            if (b >= shorterList.size()) {
                return false;
            } else if (longerList.get(a).equals(shorterList.get(b))) {
                b++;
            } else {
                return false;
            }
        }

        while (shorterList.size() > b && isLineIgnored(shorterList.get(b))) {
            b++;
        }

        return b == shorterList.size();
    }

    /**
     * This checks if the two provided lores are equal.
     * This method will ignore any lines such as the soulbound one.
     *
     * @param lore1 The first lore
     * @param lore2 The second lore
     * @return Whether the two lores are equal
     */
    public static boolean equalsLore(List<String> lore1, List<String> lore2) {
        List<String> longerList = lore1.size() > lore2.size() ? lore1 : lore2;
        List<String> shorterList = lore1.size() > lore2.size() ? lore2 : lore1;

        //noinspection DuplicatedCode
        int a = 0;
        int b = 0;

        for (; a < longerList.size(); a++) {
            if (isLineIgnored(longerList.get(a))) {
                continue;
            }

            while (shorterList.size() > b && isLineIgnored(shorterList.get(b))) {
                b++;
            }

            if (b >= shorterList.size()) {
                return false;
            } else if (longerList.get(a).equals(shorterList.get(b))) {
                b++;
            } else {
                return false;
            }
        }

        while (shorterList.size() > b && isLineIgnored(shorterList.get(b))) {
            b++;
        }

        return b == shorterList.size();
    }

    private static boolean isLineIgnored(String line) {
        return line.equals(SOULBOUND_LORE_OLD);
    }

    private static boolean isLineIgnored(Component line) {
        return line.equals(SOULBOUND_LORE);
    }

    public static void updateCapacitorTexture(Location l, int charge, int capacity) {
        Slimefun.runSync(new CapacitorTextureUpdateTask(l, charge, capacity));
    }

    /**
     * This checks whether the {@link Player} is able to use the given {@link ItemStack}.
     * It will always return <code>true</code> for non-Slimefun items.
     * <p>
     * If you already have an instance of {@link SlimefunItem}, please use {@link SlimefunItem#canUse(Player, boolean)}.
     *
     * @param p           The {@link Player}
     * @param item        The {@link ItemStack} to check
     * @param sendMessage Whether to send a message response to the {@link Player}
     * @return Whether the {@link Player} is able to use that item.
     */
    public static boolean canPlayerUseItem(Player p, @Nullable ItemStack item, boolean sendMessage) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem != null) {
            return sfItem.canUse(p, sendMessage);
        } else {
            return true;
        }
    }

    /**
     * Helper method to spawn an {@link ItemStack}.
     * This method automatically calls a {@link SlimefunItemSpawnEvent} to allow
     * other plugins to catch the item being dropped.
     *
     * @param loc             The {@link Location} where to drop the item
     * @param item            The {@link ItemStack} to drop
     * @param reason          The {@link ItemSpawnReason} why the item is being dropped
     * @param addRandomOffset Whether a random offset should be added (see {@link World#dropItemNaturally(Location, ItemStack)})
     * @param player          The player that caused this {@link SlimefunItemSpawnEvent}
     * @return The dropped {@link Item} (or null if the {@link SlimefunItemSpawnEvent} was cancelled)
     */
    public static @Nullable Item spawnItem(Location loc, ItemStack item, ItemSpawnReason reason, boolean addRandomOffset, @Nullable Player player) {
        SlimefunItemSpawnEvent event = new SlimefunItemSpawnEvent(player, loc, item, reason);
        Slimefun.instance().getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            World world = event.getLocation().getWorld();

            if (addRandomOffset) {
                return world.dropItemNaturally(event.getLocation(), event.getItemStack());
            } else {
                return world.dropItem(event.getLocation(), event.getItemStack());
            }
        } else {
            return null;
        }
    }

    /**
     * Helper method to spawn an {@link ItemStack}.
     * This method automatically calls a {@link SlimefunItemSpawnEvent} to allow
     * other plugins to catch the item being dropped.
     *
     * @param loc             The {@link Location} where to drop the item
     * @param item            The {@link ItemStack} to drop
     * @param reason          The {@link ItemSpawnReason} why the item is being dropped
     * @param addRandomOffset Whether a random offset should be added (see {@link World#dropItemNaturally(Location, ItemStack)})
     * @return The dropped {@link Item} (or null if the {@link SlimefunItemSpawnEvent} was cancelled)
     */
    public static @Nullable Item spawnItem(Location loc, ItemStack item, ItemSpawnReason reason, boolean addRandomOffset) {
        return spawnItem(loc, item, reason, addRandomOffset, null);
    }

    /**
     * Helper method to spawn an {@link ItemStack}.
     * This method automatically calls a {@link SlimefunItemSpawnEvent} to allow
     * other plugins to catch the item being dropped.
     *
     * @param loc    The {@link Location} where to drop the item
     * @param item   The {@link ItemStack} to drop
     * @param reason The {@link ItemSpawnReason} why the item is being dropped
     * @return The dropped {@link Item} (or null if the {@link SlimefunItemSpawnEvent} was cancelled)
     */
    public static Item spawnItem(Location loc, ItemStack item, ItemSpawnReason reason) {
        return spawnItem(loc, item, reason, false);
    }

    /**
     * Helper method to check if an Inventory is empty (has no items in "storage").
     * If the MC version is 1.16 or above
     * this will call {@link Inventory#isEmpty()} (Which calls MC code resulting in a faster method).
     *
     * @param inventory The {@link Inventory} to check.
     * @return True if the inventory is empty and false otherwise
     */
    public static boolean isInventoryEmpty(Inventory inventory) {
        return inventory.isEmpty();
    }

    /**
     * Check whether the item is a kind of Dust or not.
     *
     * @param item The item need to check.
     * @return Is the item a kind of Dust
     */
    public static boolean isDust(ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        return sfItem != null && sfItem.getId().endsWith("_DUST");
    }
}
