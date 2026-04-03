package de.satsuya.ruinsCore.core.auction.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * InventoryHolder für das Auktions-Preis-GUI
 */
public final class AuctionPriceGuiHolder implements InventoryHolder {

    private final ItemStack itemToAuction;
    private double currentPrice;

    public AuctionPriceGuiHolder(ItemStack itemToAuction, double currentPrice) {
        this.itemToAuction = itemToAuction;
        this.currentPrice = currentPrice;
    }

    public ItemStack getItemToAuction() {
        return itemToAuction;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double price) {
        this.currentPrice = price;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}

