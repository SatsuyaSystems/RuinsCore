package de.satsuya.ruinsCore.core.auction.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * InventoryHolder für das Auktions-Übersicht-GUI
 */
public final class AuctionOverviewGuiHolder implements InventoryHolder {

    private final int currentPage;
    private final int maxPages;

    public AuctionOverviewGuiHolder(int currentPage, int maxPages) {
        this.currentPage = currentPage;
        this.maxPages = maxPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getMaxPages() {
        return maxPages;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}

