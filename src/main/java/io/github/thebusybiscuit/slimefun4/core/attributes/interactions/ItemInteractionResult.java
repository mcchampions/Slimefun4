package io.github.thebusybiscuit.slimefun4.core.attributes.interactions;

import io.github.thebusybiscuit.slimefun4.core.attributes.ExternallyInteractable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents the result of an interaction on an {@link ExternallyInteractable} item.
 */
@Getter
public class ItemInteractionResult extends InteractionResult {
    private final Set<ItemStack> resultItems = new HashSet<>();

    /**
     * Creates a new InteractionResult.
     *
     * @param successful Whether the interaction was successful or not.
     */
    public ItemInteractionResult(boolean successful) {
        super(successful);
    }

    /**
     * Creates a new InteractionResult.
     *
     * @param successful Whether the interaction was successful or not.
     * @param itemStacks The {@link ItemStack}(s) that would be returned due to this interaction.
     */
    public ItemInteractionResult(boolean successful, ItemStack... itemStacks) {
        super(successful);
        addResultItems(itemStacks);
    }

    /**
     * Adds an or several {@link ItemStack}'s into the result.
     *
     * @param itemStacks The {@link ItemStack}(s) that would be returned due to this interaction.
     */
    public void addResultItems(ItemStack... itemStacks) {
        Collections.addAll(resultItems, itemStacks);
    }

    /**
     * This returned whether items are included as part of the result.
     *
     * @return True if items are included in the result.
     */
    public boolean resultedInItems() {
        return !this.resultItems.isEmpty();
    }

}
