package de.satsuya.ruinsCore.core.report;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service zur Verwaltung von Reports
 */
public final class ReportService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;

    public ReportService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
    }

    /**
     * Erstelle einen neuen Report
     */
    public void createReport(UUID reporterUuid, String reporterName, UUID reportedUuid, String reportedName, String reason) {
        try {
            databaseManager.executeUpdate(
                "INSERT INTO reports (reporter_uuid, reporter_name, reported_uuid, reported_name, reason, status) VALUES (?, ?, ?, ?, ?, ?)",
                reporterUuid.toString(), reporterName, reportedUuid.toString(), reportedName, reason, "OPEN"
            );
            loggerUtil.info("Report erstellt: " + reporterName + " -> " + reportedName);
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Erstellen des Reports: " + e.getMessage());
        }
    }

    /**
     * Hole alle offenen Reports
     */
    public List<Report> getOpenReports() {
        List<Report> reports = new ArrayList<>();

        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT id, reporter_uuid, reporter_name, reported_uuid, reported_name, reason, created_at, status FROM reports WHERE status = ?",
                "OPEN"
            );

            if (resultSet != null) {
                while (resultSet.next()) {
                    reports.add(new Report(
                        resultSet.getInt("id"),
                        UUID.fromString(resultSet.getString("reporter_uuid")),
                        resultSet.getString("reporter_name"),
                        UUID.fromString(resultSet.getString("reported_uuid")),
                        resultSet.getString("reported_name"),
                        resultSet.getString("reason"),
                        resultSet.getString("created_at"),
                        resultSet.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Abrufen der Reports: " + e.getMessage());
        }

        return reports;
    }

    /**
     * Hole alle Reports
     */
    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();

        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT id, reporter_uuid, reporter_name, reported_uuid, reported_name, reason, created_at, status FROM reports ORDER BY created_at DESC"
            );

            if (resultSet != null) {
                while (resultSet.next()) {
                    reports.add(new Report(
                        resultSet.getInt("id"),
                        UUID.fromString(resultSet.getString("reporter_uuid")),
                        resultSet.getString("reporter_name"),
                        UUID.fromString(resultSet.getString("reported_uuid")),
                        resultSet.getString("reported_name"),
                        resultSet.getString("reason"),
                        resultSet.getString("created_at"),
                        resultSet.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Abrufen der Reports: " + e.getMessage());
        }

        return reports;
    }

    /**
     * Markiere einen Report als bearbeitet und lösche ihn
     */
    public void closeReport(int reportId) {
        try {
            databaseManager.executeUpdate(
                "DELETE FROM reports WHERE id = ?",
                reportId
            );
            loggerUtil.info("Report #" + reportId + " gelöscht.");
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Löschen des Reports: " + e.getMessage());
        }
    }

    /**
     * Hole einen Report nach ID
     */
    public Report getReportById(int reportId) {
        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT id, reporter_uuid, reporter_name, reported_uuid, reported_name, reason, created_at, status FROM reports WHERE id = ?",
                reportId
            );

            if (resultSet != null && resultSet.next()) {
                return new Report(
                    resultSet.getInt("id"),
                    UUID.fromString(resultSet.getString("reporter_uuid")),
                    resultSet.getString("reporter_name"),
                    UUID.fromString(resultSet.getString("reported_uuid")),
                    resultSet.getString("reported_name"),
                    resultSet.getString("reason"),
                    resultSet.getString("created_at"),
                    resultSet.getString("status")
                );
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Abrufen des Reports: " + e.getMessage());
        }

        return null;
    }

    /**
     * Innere Klasse für Report-Daten
     */
    public static class Report {
        public final int id;
        public final UUID reporterUuid;
        public final String reporterName;
        public final UUID reportedUuid;
        public final String reportedName;
        public final String reason;
        public final String createdAt;
        public final String status;

        public Report(int id, UUID reporterUuid, String reporterName, UUID reportedUuid, String reportedName, String reason, String createdAt, String status) {
            this.id = id;
            this.reporterUuid = reporterUuid;
            this.reporterName = reporterName;
            this.reportedUuid = reportedUuid;
            this.reportedName = reportedName;
            this.reason = reason;
            this.createdAt = createdAt;
            this.status = status;
        }
    }
}

