package de.satsuya.ruinsCore.listeners.report;

import de.satsuya.ruinsCore.core.report.ReportGuiHolder;
import de.satsuya.ruinsCore.core.report.ReportService;
import de.satsuya.ruinsCore.core.report.ReportGuiService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Listener für Report-GUI Interaktionen
 */
public final class ReportGuiListener implements Listener {

    private final ReportService reportService;
    private final ReportGuiService reportGuiService;

    public ReportGuiListener(ReportService reportService, ReportGuiService reportGuiService) {
        this.reportService = reportService;
        this.reportGuiService = reportGuiService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof ReportGuiHolder)) {
            return;
        }

        event.setCancelled(true);
        ReportGuiHolder reportHolder = (ReportGuiHolder) holder;
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // Übersichts-GUI
        if (!reportHolder.isDetailGui()) {
            handleOverviewGui(event, player, slot);
            return;
        }

        // Detail-GUI
        handleDetailGui(event, player, slot, reportHolder.getReportId());
    }

    private void handleOverviewGui(InventoryClickEvent event, Player player, int slot) {
        // Zurück Button (letzer Slot)
        if (event.getInventory().getSize() - 1 == slot) {
            player.closeInventory();
            return;
        }

        // Report-Item anklicken
        if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
            String name = event.getCurrentItem().getItemMeta().getDisplayName();

            // Extrahiere Report-ID aus der Lore
            if (event.getCurrentItem().getItemMeta().hasLore()) {
                var lore = event.getCurrentItem().getItemMeta().getLore();
                for (String loreLine : lore) {
                    if (loreLine.startsWith("§7ID: §f")) {
                        try {
                            int reportId = Integer.parseInt(loreLine.replace("§7ID: §f", ""));
                            reportGuiService.openReportDetailGui(player, reportId);
                            return;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
    }

    private void handleDetailGui(InventoryClickEvent event, Player player, int slot, int reportId) {
        // Slot 24: Als bearbeitet markieren
        if (slot == 24) {
            reportService.closeReport(reportId);
            player.sendMessage("§a✓ Report #" + reportId + " gelöscht!");
            reportGuiService.openReportOverviewGui(player);
            return;
        }

        // Slot 26: Zurück zur Übersicht
        if (slot == 26) {
            reportGuiService.openReportOverviewGui(player);
        }
    }
}

