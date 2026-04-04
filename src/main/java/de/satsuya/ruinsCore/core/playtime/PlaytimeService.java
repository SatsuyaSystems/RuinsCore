package de.satsuya.ruinsCore.core.playtime;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service zur Verwaltung der Spielzeit
 */
public final class PlaytimeService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;
    private final Map<UUID, Long> sessionStartTime = new HashMap<>();

    public PlaytimeService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
    }

    /**
     * Starte die Spielzeit-Session für einen Spieler
     */
    public void startSession(UUID playerUuid, String playerName) {
        sessionStartTime.put(playerUuid, System.currentTimeMillis());

        // Stelle sicher, dass der Spieler in der Datenbank existiert
        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT playtime_millis FROM player_playtime WHERE player_uuid = ?",
                playerUuid.toString()
            );

            if (resultSet == null || !resultSet.next()) {
                // Spieler existiert nicht, erstelle einen Eintrag
                databaseManager.executeUpdate(
                    "INSERT INTO player_playtime (player_uuid, playtime_millis) VALUES (?, ?)",
                    playerUuid.toString(), 0L
                );
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Initialisieren der Spielzeit: " + e.getMessage());
        }
    }

    /**
     * Beende die Spielzeit-Session für einen Spieler
     */
    public void endSession(UUID playerUuid) {
        Long sessionStart = sessionStartTime.remove(playerUuid);
        if (sessionStart == null) {
            return;
        }

        long sessionDuration = System.currentTimeMillis() - sessionStart;
        addPlaytime(playerUuid, sessionDuration);
    }

    /**
     * Addiere Spielzeit hinzu
     */
    private void addPlaytime(UUID playerUuid, long milliseconds) {
        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT playtime_millis FROM player_playtime WHERE player_uuid = ?",
                playerUuid.toString()
            );

            long totalTime;
            if (resultSet != null && resultSet.next()) {
                totalTime = resultSet.getLong("playtime_millis") + milliseconds;
                databaseManager.executeUpdate(
                    "UPDATE player_playtime SET playtime_millis = ? WHERE player_uuid = ?",
                    totalTime, playerUuid.toString()
                );
            } else {
                totalTime = milliseconds;
                databaseManager.executeUpdate(
                    "INSERT INTO player_playtime (player_uuid, playtime_millis) VALUES (?, ?)",
                    playerUuid.toString(), totalTime
                );
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Speichern der Spielzeit: " + e.getMessage());
        }
    }

    /**
     * Hole die Spielzeit eines Spielers (mit live Session-Zeit)
     */
    public long getPlaytime(UUID playerUuid) {
        long totalPlaytime = 0;

        // Hole die gespeicherte Spielzeit aus DB
        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT playtime_millis FROM player_playtime WHERE player_uuid = ?",
                playerUuid.toString()
            );

            if (resultSet != null && resultSet.next()) {
                totalPlaytime = resultSet.getLong("playtime_millis");
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Abrufen der Spielzeit: " + e.getMessage());
        }

        // Addiere die aktuelle Session-Zeit
        if (sessionStartTime.containsKey(playerUuid)) {
            long currentSessionTime = System.currentTimeMillis() - sessionStartTime.get(playerUuid);
            totalPlaytime += currentSessionTime;
        }

        return totalPlaytime;
    }

    /**
     * Formatiere Spielzeit zu lesbarem Format
     */
    public String formatPlaytime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        long remainingHours = hours % 24;
        long remainingMinutes = minutes % 60;
        long remainingSeconds = seconds % 60;

        if (days > 0) {
            return String.format("%d Tage, %d Stunden, %d Minuten", days, remainingHours, remainingMinutes);
        } else if (hours > 0) {
            return String.format("%d Stunden, %d Minuten", hours, remainingMinutes);
        } else if (minutes > 0) {
            return String.format("%d Minuten, %d Sekunden", minutes, remainingSeconds);
        } else {
            return String.format("%d Sekunden", seconds);
        }
    }

    /**
     * Gebe die Spielzeit eines Spielers als String zurück
     */
    public String getPlaytimeString(UUID playerUuid) {
        long playtime = getPlaytime(playerUuid);
        return formatPlaytime(playtime);
    }

    /**
     * Setze die Spielzeit eines Spielers (Admin)
     */
    public void setPlaytime(UUID playerUuid, long milliseconds) {
        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT playtime_millis FROM player_playtime WHERE player_uuid = ?",
                playerUuid.toString()
            );

            if (resultSet != null && resultSet.next()) {
                databaseManager.executeUpdate(
                    "UPDATE player_playtime SET playtime_millis = ? WHERE player_uuid = ?",
                    milliseconds, playerUuid.toString()
                );
            } else {
                databaseManager.executeUpdate(
                    "INSERT INTO player_playtime (player_uuid, playtime_millis) VALUES (?, ?)",
                    playerUuid.toString(), milliseconds
                );
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Setzen der Spielzeit: " + e.getMessage());
        }
    }
}

