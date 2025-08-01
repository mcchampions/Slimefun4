package io.github.thebusybiscuit.slimefun4.core.guide;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;

import java.util.Deque;
import java.util.LinkedList;
import javax.annotation.Nullable;

import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * {@link GuideHistory} represents the browsing history of a {@link Player} through the
 * {@link SlimefunGuide}.
 *
 * @author TheBusyBiscuit
 * @see SlimefunGuide
 * @see PlayerProfile
 */
public class GuideHistory {
    private final PlayerProfile profile;
    private final Deque<GuideEntry<?>> queue = new LinkedList<>();
    /**
     * -- SETTER --
     * This method sets the page of the main menu of this
     */
    @Setter
    @Getter
    private int mainMenuPage = 1;

    /**
     * This creates a new {@link GuideHistory} for the given {@link PlayerProfile}
     *
     * @param profile The {@link PlayerProfile} this {@link GuideHistory} was made for
     */
    public GuideHistory(PlayerProfile profile) {
        this.profile = profile;
    }

    /**
     * This method will clear this {@link GuideHistory} and remove all entries.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * This method adds a {@link ItemGroup} to this {@link GuideHistory}.
     * Should the {@link ItemGroup} already be the last element in this {@link GuideHistory},
     * then the entry will be overridden with the new page.
     *
     * @param itemGroup The {@link ItemGroup} that should be added to this {@link GuideHistory}
     * @param page      The current page of the {@link ItemGroup} that should be stored
     */
    public void add(ItemGroup itemGroup, int page) {
        refresh(itemGroup, page);
    }

    /**
     * This method adds a {@link ItemStack} to this {@link GuideHistory}.
     * Should the {@link ItemStack} already be the last element in this {@link GuideHistory},
     * then the entry will be overridden with the new page.
     *
     * @param item The {@link ItemStack} that should be added to this {@link GuideHistory}
     * @param page The current page of the recipes of this {@link ItemStack}
     */
    public void add(ItemStack item, int page) {
        refresh(item, page);
    }

    /**
     * This method stores the given {@link SlimefunItem} in this {@link GuideHistory}.
     *
     * @param item The {@link SlimefunItem} that should be added to this {@link GuideHistory}
     */
    public void add(SlimefunItem item) {
        queue.add(new GuideEntry<>(item, 0));
    }

    /**
     * This method stores the given search term in this {@link GuideHistory}.
     *
     * @param searchTerm The term that the {@link Player} searched for
     */
    public void add(String searchTerm, boolean usePinyin) {
        queue.add(new GuideEntry<>(new GuideEntry<>(searchTerm, (usePinyin ? 0 : 1)), 0));
    }

    private <T> void refresh(T object, int page) {
        GuideEntry<?> lastEntry = getLastEntry(false);

        if (lastEntry != null && lastEntry.getIndexedObject().equals(object)) {
            lastEntry.setPage(page);
        } else {
            queue.add(new GuideEntry<>(object, page));
        }
    }

    /**
     * This returns the amount of elements in this {@link GuideHistory}.
     *
     * @return The size of this {@link GuideHistory}
     */
    public int size() {
        return queue.size();
    }

    /**
     * Retrieves the last page in the {@link SlimefunGuide} that was visited by a {@link Player}.
     * Optionally also rewinds the history back to that entry.
     *
     * @param remove Whether to remove the current entry so it moves back to the entry returned.
     * @return The last Guide Entry that was saved to the given Players guide history.
     */
    @Nullable
    private GuideEntry<?> getLastEntry(boolean remove) {
        if (remove && !queue.isEmpty()) {
            queue.removeLast();
        }

        return queue.isEmpty() ? null : queue.getLast();
    }

    /**
     * This method opens the last opened entry to the associated {@link PlayerProfile}
     * of this {@link GuideHistory}.
     *
     * @param guide The {@link SlimefunGuideImplementation} to use
     */
    public void openLastEntry(SlimefunGuideImplementation guide) {
        GuideEntry<?> entry = getLastEntry(false);
        open(guide, entry);
    }

    /**
     * This method opens the previous entry to the associated {@link PlayerProfile}.
     * More precisely, it will remove the last entry and open the second-last entry
     * to the {@link Player}.
     * <p>
     * It can be thought of as a "back" button. Since that is what this is used for.
     *
     * @param guide The {@link SlimefunGuideImplementation} to use
     */
    public void goBack(SlimefunGuideImplementation guide) {
        GuideEntry<?> entry = getLastEntry(true);
        open(guide, entry);
    }

    private <T> void open(SlimefunGuideImplementation guide, @Nullable GuideEntry<T> entry) {
        if (entry == null) {
            guide.openMainMenu(profile, mainMenuPage);
        } else if (entry.getIndexedObject() instanceof ItemGroup group) {
            guide.openItemGroup(profile, group, entry.getPage());
        } else if (entry.getIndexedObject() instanceof SlimefunItem item) {
            guide.displayItem(profile, item, false);
        } else if (entry.getIndexedObject() instanceof ItemStack stack) {
            guide.displayItem(profile, stack, entry.getPage(), false);
        } else if (entry.getIndexedObject() instanceof GuideEntry<?> qe) {
            guide.openSearch(profile, (String) qe.getIndexedObject(), false, qe.getPage() == 0);
        } else {
            throw new IllegalStateException("Unknown GuideHistory entry: " + entry.getIndexedObject());
        }
    }
}
