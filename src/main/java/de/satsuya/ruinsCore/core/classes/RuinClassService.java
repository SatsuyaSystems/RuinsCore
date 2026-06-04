package de.satsuya.ruinsCore.core.classes;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class RuinClassService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;

    // Cache
    private final Map<UUID, RuinClassType> playerClasses = new HashMap<>();
    private final Map<UUID, Boolean> baptizedPlayers = new HashMap<>();

    public RuinClassService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
        loadAllData();
    }

    private void loadAllData() {
        // Load classes
        try (ResultSet result = databaseManager.executeQuery("SELECT player_uuid, class_type FROM player_classes")) {
            if (result != null) {
                while (result.next()) {
                    try {
                        UUID uuid = UUID.fromString(result.getString("player_uuid"));
                        RuinClassType classType = RuinClassType.valueOf(result.getString("class_type"));
                        playerClasses.put(uuid, classType);
                    } catch (IllegalArgumentException e) {
                        loggerUtil.warning("Fehler beim Laden einer Klasse für " + result.getString("player_uuid"));
                    }
                }
            }
        } catch (SQLException e) {
            loggerUtil.severe("Fehler beim Laden der Klassen-Daten", e);
        }

        // Load baptized players
        try (ResultSet result = databaseManager.executeQuery("SELECT player_uuid FROM baptized_players")) {
            if (result != null) {
                while (result.next()) {
                    try {
                        UUID uuid = UUID.fromString(result.getString("player_uuid"));
                        baptizedPlayers.put(uuid, true);
                    } catch (IllegalArgumentException e) {
                        loggerUtil.warning("Fehler beim Laden eins getauften Spielers: " + result.getString("player_uuid"));
                    }
                }
            }
        } catch (SQLException e) {
            loggerUtil.severe("Fehler beim Laden der Taufen-Daten", e);
        }
    }

    public Optional<RuinClassType> getClass(UUID playerUuid) {
        return Optional.ofNullable(playerClasses.get(playerUuid));
    }

    public void setClass(UUID playerUuid, RuinClassType classType) {
        if (classType == null) {
            removeClass(playerUuid);
            return;
        }

        playerClasses.put(playerUuid, classType);
        try {
            databaseManager.executeUpdate(
                "INSERT OR REPLACE INTO player_classes (player_uuid, class_type) VALUES (?, ?)",
                playerUuid.toString(),
                classType.name()
            );
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Aktualisieren der Klasse für " + playerUuid, e);
        }
    }

    public void removeClass(UUID playerUuid) {
        playerClasses.remove(playerUuid);
        try {
            databaseManager.executeUpdate(
                "DELETE FROM player_classes WHERE player_uuid = ?",
                playerUuid.toString()
            );
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Entfernen der Klasse für " + playerUuid, e);
        }
    }

    public boolean isBaptized(UUID playerUuid) {
        return baptizedPlayers.containsKey(playerUuid);
    }

    public void setBaptized(UUID playerUuid, boolean baptized) {
        if (baptized) {
            baptizedPlayers.put(playerUuid, true);
            try {
                databaseManager.executeUpdate(
                    "INSERT OR IGNORE INTO baptized_players (player_uuid) VALUES (?)",
                    playerUuid.toString()
                );
            } catch (Exception e) {
                loggerUtil.severe("Fehler beim Hinzufügen der Taufe für " + playerUuid, e);
            }
        } else {
            baptizedPlayers.remove(playerUuid);
            try {
                databaseManager.executeUpdate(
                    "DELETE FROM baptized_players WHERE player_uuid = ?",
                    playerUuid.toString()
                );
            } catch (Exception e) {
                loggerUtil.severe("Fehler beim Entfernen der Taufe für " + playerUuid, e);
            }
        }
    }
}

