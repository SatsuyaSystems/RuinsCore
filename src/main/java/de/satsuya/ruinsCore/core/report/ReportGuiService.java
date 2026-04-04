package de.satsuya.ruinsCore.core.report;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.List;

/**
 * Service für Report-GUI
 */
public final class ReportGuiService {

    private final ReportService reportService;

    public ReportGuiService(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Öffne das Report-Übersichts-GUI
     */
    public void openReportOverviewGui(Player player) {
        List<ReportService.Report> reports = reportService.getOpenReports();
        int size = Math.min(54, ((reports.size() + 8) / 9) * 9); // Runde auf nächste 9er
        if (size == 0) size = 9;

        Inventory gui = Bukkit.createInventory(new ReportGuiHolder(-1), size, "§c📋 Reports (" + reports.size() + ")");

        int slot = 0;
        for (ReportService.Report report : reports) {
            if (slot >= size - 1) break;

            String reporterName = report.reporterName;
            String reportedName = report.reportedName;

            ItemStack reportItem = createReportItem(
                Material.PAPER,
                "§e" + reportedName,
                "§7Von: §f" + reporterName,
                "§7Grund: §f" + (report.reason.length() > 20 ? report.reason.substring(0, 20) + "..." : report.reason),
                "§7ID: §f" + report.id,
                "§8§oKlick zum Einsehen"
            );

            gui.setItem(slot, reportItem);
            slot++;
        }

        // Wenn keine Reports
        if (reports.isEmpty()) {
            ItemStack noReports = createReportItem(Material.EMERALD_BLOCK, "§a✓ Keine offenen Reports!");
            gui.setItem(0, noReports);
        }

        // Schließe-Button
        ItemStack closeButton = createReportItem(Material.BARRIER, "§c← Zurück");
        gui.setItem(size - 1, closeButton);

        player.openInventory(gui);
    }

    /**
     * Öffne das Detail-GUI für einen Report
     */
    public void openReportDetailGui(Player player, int reportId) {
        ReportService.Report report = reportService.getReportById(reportId);
        if (report == null) {
            player.sendMessage("§c✗ Report nicht gefunden!");
            return;
        }

        Inventory gui = Bukkit.createInventory(new ReportGuiHolder(reportId), 27, "§e📜 Report Details");

        // Slot 10: Reporter Info
        ItemStack reporterItem = createReportItem(
            Material.PLAYER_HEAD,
            "§b" + report.reporterName,
            "§7Reporter",
            "§7UUID: §f" + report.reporterUuid
        );
        gui.setItem(10, reporterItem);

        // Slot 12: Pfeiltrenner
        ItemStack arrow = createReportItem(Material.ARROW, "§7→");
        gui.setItem(12, arrow);

        // Slot 14: Reported Info
        ItemStack reportedItem = createReportItem(
            Material.PLAYER_HEAD,
            "§c" + report.reportedName,
            "§7Beschuldigt",
            "§7UUID: §f" + report.reportedUuid
        );
        gui.setItem(14, reportedItem);

        // Slot 19: Grund
        ItemStack reasonItem = createReportItem(
            Material.BOOK,
            "§6Grund",
            "§f" + report.reason
        );
        gui.setItem(19, reasonItem);

        // Slot 23: Zeit
        ItemStack timeItem = createReportItem(
            Material.CLOCK,
            "§eErstellt am",
            "§f" + report.createdAt
        );
        gui.setItem(23, timeItem);

        // Slot 24: Als bearbeitet markieren (Grüner Block)
        ItemStack doneButton = createReportItem(
            Material.EMERALD_BLOCK,
            "§a✓ Bearbeitet",
            "§7Klick um zu löschen"
        );
        gui.setItem(24, doneButton);

        // Slot 26: Zurück Button
        ItemStack backButton = createReportItem(Material.BARRIER, "§c← Zurück");
        gui.setItem(26, backButton);

        player.openInventory(gui);
    }

    /**
     * Erstelle einen Report-Item
     */
    private ItemStack createReportItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }

        return item;
    }
}

