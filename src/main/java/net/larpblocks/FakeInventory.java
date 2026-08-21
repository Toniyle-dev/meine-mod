package net.larpblocks;

import net.minecraft.item.ItemStack;

/**
 * A purely local 9-slot "hotbar" of ItemStacks. Nothing in here is ever
 * given to the real player inventory and nothing is ever sent to the
 * server - it only exists so we have something to draw on screen.
 */
public class FakeInventory {
    private final ItemStack[] slots = new ItemStack[9];
    private int selected = 0;

    public FakeInventory() {
        clear();
    }

    public ItemStack getSlot(int index) {
        return slots[index];
    }

    /** Puts a stack into whichever slot is currently selected in the picker UI. */
    public void setSelectedSlot(ItemStack stack) {
        slots[selected] = stack;
    }

    public int getSelectedForDisplay() {
        return selected;
    }

    public void setSelectedForDisplay(int index) {
        if (index >= 0 && index < 9) {
            selected = index;
        }
    }

    public void clear() {
        for (int i = 0; i < 9; i++) {
            slots[i] = ItemStack.EMPTY;
        }
        selected = 0;
    }
}
