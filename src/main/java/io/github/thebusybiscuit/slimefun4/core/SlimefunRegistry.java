package io.github.thebusybiscuit.slimefun4.core;

import io.github.bakedlibs.dough.collections.KeyMap;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import me.mrCookieSlime.Slimefun.api.BlockInfoConfig;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class houses a lot of instances of {@link Map} and {@link List} that hold
 * various mappings and collections related to {@link SlimefunItem}.
 *
 * @author TheBusyBiscuit
 *
 */
public final class SlimefunRegistry {

    private final Map<String, SlimefunItem> slimefunIds = new HashMap<>();
    private final List<SlimefunItem> slimefunItems = new ArrayList<>();
    private final List<SlimefunItem> enabledItems = new ArrayList<>();

    private final List<ItemGroup> categories = new ArrayList<>();
    private final List<MultiBlock> multiblocks = new LinkedList<>();

    private final List<Research> researches = new LinkedList<>();
    private final List<String> researchRanks = new ArrayList<>();
    private final Set<UUID> researchingPlayers = Collections.synchronizedSet(new HashSet<>());

    private final Set<String> tickers = new HashSet<>();
    private final Set<SlimefunItem> radioactive = new HashSet<>();
    private final Set<ItemStack> barterDrops = new HashSet<>();

    private NamespacedKey soulboundKey;
    private NamespacedKey itemChargeKey;
    private NamespacedKey guideKey;

    private final KeyMap<GEOResource> geoResources = new KeyMap<>();

    private final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<>();
    private final Map<String, BlockInfoConfig> chunks = new HashMap<>();
    private final Map<SlimefunGuideMode, SlimefunGuideImplementation> guides = new EnumMap<>(SlimefunGuideMode.class);
    private final Map<EntityType, Set<ItemStack>> mobDrops = new EnumMap<>(EntityType.class);

    private final Map<String, BlockMenuPreset> blockMenuPresets = new HashMap<>();

    private final Map<Class<? extends ItemHandler>, Set<ItemHandler>> globalItemHandlers = new HashMap<>();

    public void load(Slimefun plugin) {
        Validate.notNull(plugin, "The Plugin cannot be null!");

        soulboundKey = new NamespacedKey(plugin, "soulbound");
        itemChargeKey = new NamespacedKey(plugin, "item_charge");
        guideKey = new NamespacedKey(plugin, "slimefun_guide_mode");

        guides.put(SlimefunGuideMode.SURVIVAL_MODE, new SurvivalSlimefunGuide());
        guides.put(SlimefunGuideMode.CHEAT_MODE, new CheatSheetSlimefunGuide());

        var cfg = Slimefun.getConfigManager().getPluginConfig();
        researchRanks.addAll(cfg.getStringList("research-ranks"));
    }

    /**
     * This returns a {@link List} containing every enabled {@link ItemGroup}.
     *
     * @return {@link List} containing every enabled {@link ItemGroup}
     */
    
    public List<ItemGroup> getAllItemGroups() {
        return categories;
    }

    /**
     * This {@link List} contains every {@link SlimefunItem}, even disabled items.
     *
     * @return A {@link List} containing every {@link SlimefunItem}
     */
    public List<SlimefunItem> getAllSlimefunItems() {
        return slimefunItems;
    }

    /**
     * This {@link List} contains every disabled {@link SlimefunItem}.
     *
     * @return A {@link List} containing every disabled{@link SlimefunItem}
     */
    public List<SlimefunItem> getDisabledSlimefunItems() {
        List<SlimefunItem> allItems = new ArrayList<>(getAllSlimefunItems());
        List<SlimefunItem> enabledItems = getEnabledSlimefunItems();
        allItems.removeAll(enabledItems);
        return allItems;
    }

    /**
     * This {@link List} contains every <strong>enabled</strong> {@link SlimefunItem}.
     *
     * @return A {@link List} containing every enabled {@link SlimefunItem}
     */
    
    public List<SlimefunItem> getEnabledSlimefunItems() {
        return enabledItems;
    }

    /**
     * This returns a {@link List} containing every enabled {@link Research}.
     *
     * @return A {@link List} containing every enabled {@link Research}
     */
    
    public List<Research> getResearches() {
        return researches;
    }

    /**
     * This method returns a {@link Set} containing the {@link UUID} of every
     * {@link Player} who is currently unlocking a {@link Research}.
     *
     * @return A {@link Set} holding the {@link UUID} from every {@link Player}
     *         who is currently unlocking a {@link Research}
     */
    
    public Set<UUID> getCurrentlyResearchingPlayers() {
        return researchingPlayers;
    }

    
    public List<String> getResearchRanks() {
        return researchRanks;
    }

    /**
     * This method returns a {@link List} of every enabled {@link MultiBlock}.
     *
     * @return A {@link List} containing every enabled {@link MultiBlock}
     */
    
    public List<MultiBlock> getMultiBlocks() {
        return multiblocks;
    }

    /**
     * This returns the corresponding {@link SlimefunGuideImplementation} for a certain
     * {@link SlimefunGuideMode}.
     * <p>
     * This mainly only exists for internal purposes, if you want to open a certain section
     * using the {@link SlimefunGuide}, then please use the static methods provided in the
     * {@link SlimefunGuide} class.
     *
     * @param mode
     *            The {@link SlimefunGuideMode}
     *
     * @return The corresponding {@link SlimefunGuideImplementation}
     */
    
    public SlimefunGuideImplementation getSlimefunGuide(SlimefunGuideMode mode) {
        Validate.notNull(mode, "The Guide mode cannot be null");

        SlimefunGuideImplementation guide = guides.get(mode);

        if (guide == null) {
            throw new IllegalStateException("Slimefun Guide '" + mode + "' has no registered implementation.");
        }

        return guide;
    }

    /**
     * This returns a {@link Map} connecting the {@link EntityType} with a {@link Set}
     * of {@link ItemStack ItemStacks} which would be dropped when an {@link Entity} of that type was killed.
     *
     * @return The {@link Map} of custom mob drops
     */
    
    public Map<EntityType, Set<ItemStack>> getMobDrops() {
        return mobDrops;
    }

    /**
     * This returns a {@link Set} of {@link ItemStack ItemStacks} which can be obtained by bartering
     * with {@link Piglin Piglins}.
     *
     * @return A {@link Set} of bartering drops
     */
    
    public Set<ItemStack> getBarteringDrops() {
        return barterDrops;
    }

    
    public Set<SlimefunItem> getRadioactiveItems() {
        return radioactive;
    }

    
    public Set<String> getTickerBlocks() {
        return tickers;
    }

    
    public Map<String, SlimefunItem> getSlimefunItemIds() {
        return slimefunIds;
    }

    
    public Map<String, BlockMenuPreset> getMenuPresets() {
        return blockMenuPresets;
    }

    
    public Map<UUID, PlayerProfile> getPlayerProfiles() {
        return profiles;
    }

    
    public Map<Class<? extends ItemHandler>, Set<ItemHandler>> getGlobalItemHandlers() {
        return globalItemHandlers;
    }

    
    public Set<ItemHandler> getGlobalItemHandlers(Class<? extends ItemHandler> identifier) {
        Validate.notNull(identifier, "The identifier for an ItemHandler cannot be null!");

        return globalItemHandlers.computeIfAbsent(identifier, c -> new HashSet<>());
    }

    
    public Map<String, BlockInfoConfig> getChunks() {
        return chunks;
    }

    
    public KeyMap<GEOResource> getGEOResources() {
        return geoResources;
    }

    
    public NamespacedKey getSoulboundDataKey() {
        return soulboundKey;
    }

    
    public NamespacedKey getItemChargeDataKey() {
        return itemChargeKey;
    }

    
    public NamespacedKey getGuideDataKey() {
        return guideKey;
    }

    @Deprecated
    public boolean isFreeCreativeResearchingEnabled() {
        return Slimefun.getConfigManager().isFreeCreativeResearchingEnabled();
    }
}
