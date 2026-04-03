package de.satsuya.ruinsCore.core.warning;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Service zur Verwaltung von Spieler-Verwarnungen
 */
public final class WarningService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;
    private static final int WARNING_LIMIT = 5;

    public WarningService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
    }

    /**
     * Verwarnt einen Spieler
     */
    public boolean warnPlayer(UUID playerUuid, UUID warnedByUuid, String reason) {
        try {
            // Füge Verwarnung hinzu
            databaseManager.executeUpdate(
                "INSERT INTO player_warnings (player_uuid, warned_by, reason) VALUES (?, ?, ?)",
                playerUuid.toString(),
                warnedByUuid.toString(),
                reason
            );

            // Prüfe ob Spieler ge-bannt werden soll
            int warningCount = getWarningCount(playerUuid);
            if (warningCount >= WARNING_LIMIT) {
                banPlayer(playerUuid);
                return true; // Ban wurde aktiviert
            }

            return false; // Kein Ban
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Verwarnen des Spielers", e);
            return false;
        }
    }

    /**
     * Entfernt eine Verwarnung
     */
    public boolean removeWarning(int warningId) {
        try {
            databaseManager.executeUpdate(
                "DELETE FROM player_warnings WHERE id = ?",
                warningId
            );
            return true;
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Entfernen der Verwarnung", e);
            return false;
        }
    }

    /**
     * Entfernt alle Verwarnungen eines Spielers
     */
    public boolean removeAllWarnings(java.util.UUID playerUuid) {
        try {
            databaseManager.executeUpdate(
                "DELETE FROM player_warnings WHERE player_uuid = ?",
                playerUuid.toString()
            );
            return true;
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Entfernen aller Verwarnungen", e);
            return false;
        }
    }

    /**
     * Gibt die Anzahl der Verwarnungen zurück
     */
    public int getWarningCount(UUID playerUuid) {
        try (ResultSet result = databaseManager.executeQuery(
                "SELECT COUNT(*) as count FROM player_warnings WHERE player_uuid = ?",
                playerUuid.toString()
        )) {
            if (result.next()) {
                return result.getInt("count");
            }
        } catch (SQLException e) {
            loggerUtil.severe("Fehler beim Abrufen der Verwarnungsanzahl", e);
        }
        return 0;
    }

    /**
     * Gibt alle Verwarnungen eines Spielers zurück
     */
    public String getWarningsInfo(UUID playerUuid) {
        int count = getWarningCount(playerUuid);
        int remaining = WARNING_LIMIT - count;

        if (count == 0) {
            return "§a✓ Spieler hat keine Verwarnungen.";
        }

        String info = "§6Verwarnungen: §b" + count + "§6/§b" + WARNING_LIMIT;
        if (remaining > 0) {
            info += " §7(§b" + remaining + " §7verbleibend)";
        } else {
            info += " §c[GEBANNT]";
        }

        return info;
    }

    /**
     * Banne einen Spieler
     */
    private void banPlayer(UUID playerUuid) {
        Player player = Bukkit.getPlayer(playerUuid);
        String playerName = player != null ? player.getName() : "Unknown";
        
        // Kicke zuerst den Spieler
        if (player != null) {
            player.kickPlayer("§cDu wurdest wegen zu vieler Verwarnungen gebannt!");
        }
        
        // Führe den Ban-Command aus
        String banReason = "Zu viele Verwarnungen (5/5)";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            "ban " + playerName + " " + banReason);
        
        loggerUtil.warning("Spieler " + playerName + " wurde nach 5 Verwarnungen permanet gebannt.");
    }

    /**
     * Gibt die Anzahl der verbleibenden Verwarnungen zurück
     */
    public int getWarningsUntilBan(UUID playerUuid) {
        int count = getWarningCount(playerUuid);
        return Math.max(0, WARNING_LIMIT - count);
    }

    /**
     * Gibt alle Verwarnungen eines Spielers als formatted String zurück
     */
    public java.util.List<WarningInfo> getAllWarnings(UUID playerUuid) {
        java.util.List<WarningInfo> warnings = new java.util.ArrayList<>();
        try (ResultSet result = databaseManager.executeQuery(
                "SELECT id, warned_by, reason, created_at FROM player_warnings WHERE player_uuid = ? ORDER BY created_at DESC",
                playerUuid.toString()
        )) {
            while (result != null && result.next()) {
                warnings.add(new WarningInfo(
                    result.getInt("id"),
                    result.getString("warned_by"),
                    result.getString("reason"),
                    result.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            loggerUtil.severe("Fehler beim Abrufen aller Verwarnungen", e);
        }
        return warnings;
    }

    /**
     * Innere Klasse für Verwarnungs-Informationen
     */
    public static class WarningInfo {
        public final int id;
        public final String warnedBy;
        public final String reason;
        public final String createdAt;

        public WarningInfo(int id, String warnedBy, String reason, String createdAt) {
            this.id = id;
            this.warnedBy = warnedBy;
            this.reason = reason;
            this.createdAt = createdAt;
        }
    }
}
