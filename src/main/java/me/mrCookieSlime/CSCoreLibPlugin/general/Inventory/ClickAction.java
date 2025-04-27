package me.mrCookieSlime.CSCoreLibPlugin.general.Inventory;

import lombok.Getter;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * An old remnant of CS-CoreLib.
 * This will be removed once we updated everything.
 * Don't look at the code, it will be gone soon, don't worry.
 */
public class ClickAction {
    private final boolean right;
    private final boolean shift;
    @Getter
    private final boolean numberKey;

    public ClickAction(boolean rightClicked, boolean shiftClicked) {
        this.right = rightClicked;
        this.shift = shiftClicked;
        this.numberKey = false;
    }

    public ClickAction(InventoryClickEvent e) {
        this.right = e.isRightClick();
        this.shift = e.isShiftClick();
        this.numberKey = e.getClick() == ClickType.NUMBER_KEY;
    }

    public boolean isRightClicked() {
        return right;
    }

    public boolean isShiftClicked() {
        return shift;
    }
}
