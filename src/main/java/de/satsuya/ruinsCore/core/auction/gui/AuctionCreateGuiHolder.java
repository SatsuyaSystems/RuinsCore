package de.satsuya.ruinsCore.core.auction.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * InventoryHolder für das Auktions-Erstellungs-GUI
 */
public final class AuctionCreateGuiHolder implements InventoryHolder {

    private ItemStack selectedItem;
    private int selectedSlot = -1;

    public AuctionCreateGuiHolder() {
    }

    public ItemStack getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ItemStack item) {
        this.selectedItem = item;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        this.selectedSlot = slot;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}

