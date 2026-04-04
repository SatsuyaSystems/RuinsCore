package de.satsuya.ruinsCore.core.report;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * InventoryHolder für Report-GUI
 */
public final class ReportGuiHolder implements InventoryHolder {

    private final int reportId; // -1 für Übersichts-GUI

    public ReportGuiHolder(int reportId) {
        this.reportId = reportId;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public int getReportId() {
        return reportId;
    }

    public boolean isDetailGui() {
        return reportId != -1;
    }
}

