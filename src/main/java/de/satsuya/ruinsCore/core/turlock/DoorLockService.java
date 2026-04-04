package de.satsuya.ruinsCore.core.turlock;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service zur Verwaltung von Türschlössern
 */
public final class DoorLockService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;

    public DoorLockService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
    }

    /**
     * Erstelle ein neues Türschloss
     */
    public void createLock(Location location, UUID ownerUuid, String ownerName) {
        String key = getLocationKey(location);

        try {
            databaseManager.executeUpdate(
                "INSERT OR REPLACE INTO door_locks (door_key, world, x, y, z, owner_uuid, owner_name, is_public, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                key, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                ownerUuid.toString(), ownerName, true, System.currentTimeMillis()
            );
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Erstellen des Türschlosses: " + e.getMessage());
        }
    }

    /**
     * Hole ein Türschloss per doorKey (String)
     */
    public DoorLock getLockByKey(String doorKey) {
        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT * FROM door_locks WHERE door_key = ?",
                doorKey
            );

            if (resultSet != null && resultSet.next()) {
                return new DoorLock(
                    resultSet.getString("door_key"),
                    resultSet.getString("owner_uuid"),
                    resultSet.getString("owner_name"),
                    resultSet.getBoolean("is_public")
                );
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Abrufen des Türschlosses: " + e.getMessage());
        }

        return null;
    }

    /**
     * Setze öffentlich/privat per doorKey (String)
     */
    public void setPublicByKey(String doorKey, boolean isPublic) {
        try {
            databaseManager.executeUpdate(
                "UPDATE door_locks SET is_public = ? WHERE door_key = ?",
                isPublic, doorKey
            );
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Setzen von Öffentlich: " + e.getMessage());
        }
    }

    // ...existing code...

    /**
     * Hole ein Türschloss
     */
    public DoorLock getLock(Location location) {
        String key = getLocationKey(location);

        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT * FROM door_locks WHERE door_key = ?",
                key
            );

            if (resultSet != null && resultSet.next()) {
                return new DoorLock(
                    resultSet.getString("door_key"),
                    resultSet.getString("owner_uuid"),
                    resultSet.getString("owner_name"),
                    resultSet.getBoolean("is_public")
                );
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Abrufen des Türschlosses: " + e.getMessage());
        }

        return null;
    }

    /**
     * Prüfe ob ein Spieler diese Tür öffnen darf
     */
    public boolean canOpenDoor(Location location, UUID playerUuid, boolean isWache) {
        // Wache kann alle Türen öffnen
        if (isWache) {
            return true;
        }

        DoorLock lock = getLock(location);

        // Keine Lock = öffentlich
        if (lock == null) {
            return true;
        }

        // Öffentlich = jeder kann öffnen
        if (lock.isPublic) {
            return true;
        }

        // Besitzer = kann immer öffnen
        if (lock.ownerUuid.equals(playerUuid.toString())) {
            return true;
        }

        // Prüfe Whitelist
        return isWhitelisted(location, playerUuid);
    }

    /**
     * Prüfe ob ein Spieler auf der Whitelist ist
     */
    private boolean isWhitelisted(Location location, UUID playerUuid) {
        String key = getLocationKey(location);

        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT * FROM door_lock_whitelist WHERE door_key = ? AND player_uuid = ?",
                key, playerUuid.toString()
            );

            return resultSet != null && resultSet.next();
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Prüfen der Whitelist: " + e.getMessage());
            return false;
        }
    }

    /**
     * Setze öffentlich/privat
     */
    public void setPublic(Location location, boolean isPublic) {
        String key = getLocationKey(location);

        try {
            databaseManager.executeUpdate(
                "UPDATE door_locks SET is_public = ? WHERE door_key = ?",
                isPublic, key
            );
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Setzen von Öffentlich: " + e.getMessage());
        }
    }

    /**
     * Füge Spieler zur Whitelist hinzu
     */
    public void addToWhitelist(Location location, UUID playerUuid, String playerName) {
        String key = getLocationKey(location);

        try {
            databaseManager.executeUpdate(
                "INSERT OR IGNORE INTO door_lock_whitelist (door_key, player_uuid, player_name) VALUES (?, ?, ?)",
                key, playerUuid.toString(), playerName
            );
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Hinzufügen zur Whitelist: " + e.getMessage());
        }
    }

    /**
     * Entferne Spieler von Whitelist
     */
    public void removeFromWhitelist(Location location, UUID playerUuid) {
        String key = getLocationKey(location);

        try {
            databaseManager.executeUpdate(
                "DELETE FROM door_lock_whitelist WHERE door_key = ? AND player_uuid = ?",
                key, playerUuid.toString()
            );
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Entfernen von Whitelist: " + e.getMessage());
        }
    }

    /**
     * Hole alle Spieler auf Whitelist
     */
    public Set<UUID> getWhitelist(Location location) {
        Set<UUID> whitelist = new HashSet<>();
        String key = getLocationKey(location);

        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT player_uuid FROM door_lock_whitelist WHERE door_key = ?",
                key
            );

            if (resultSet != null) {
                while (resultSet.next()) {
                    whitelist.add(UUID.fromString(resultSet.getString("player_uuid")));
                }
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Abrufen der Whitelist: " + e.getMessage());
        }

        return whitelist;
    }

    /**
     * Hole alle Spieler auf Whitelist mit Namen (per doorKey)
     */
    public Map<UUID, String> getWhitelistWithNames(String doorKey) {
        Map<UUID, String> whitelist = new HashMap<>();

        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT player_uuid, player_name FROM door_lock_whitelist WHERE door_key = ?",
                doorKey
            );

            if (resultSet != null) {
                while (resultSet.next()) {
                    UUID playerUuid = UUID.fromString(resultSet.getString("player_uuid"));
                    String playerName = resultSet.getString("player_name");
                    whitelist.put(playerUuid, playerName);
                }
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Abrufen der Whitelist: " + e.getMessage());
        }

        return whitelist;
    }

    /**
     * Entferne einen Spieler von Whitelist per UUID (doorKey)
     */
    public void removeFromWhitelistByKey(String doorKey, UUID playerUuid) {
        try {
            databaseManager.executeUpdate(
                "DELETE FROM door_lock_whitelist WHERE door_key = ? AND player_uuid = ?",
                doorKey, playerUuid.toString()
            );
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Entfernen von Whitelist: " + e.getMessage());
        }
    }

    /**
     * Füge Spieler zur Whitelist hinzu per doorKey (String)
     */
    public void addToWhitelistByKey(String doorKey, UUID playerUuid, String playerName) {
        try {
            databaseManager.executeUpdate(
                "INSERT OR IGNORE INTO door_lock_whitelist (door_key, player_uuid, player_name) VALUES (?, ?, ?)",
                doorKey, playerUuid.toString(), playerName
            );
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Hinzufügen zur Whitelist: " + e.getMessage());
        }
    }

    /**
     * Lösche ein Türschloss
     */
    public void deleteLock(Location location) {
        String key = getLocationKey(location);

        try {
            databaseManager.executeUpdate("DELETE FROM door_locks WHERE door_key = ?", key);
            databaseManager.executeUpdate("DELETE FROM door_lock_whitelist WHERE door_key = ?", key);
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Löschen des Türschlosses: " + e.getMessage());
        }
    }

    /**
     * Generiere einen eindeutigen Schlüssel für eine Tür
     */
    private String getLocationKey(Location location) {
        return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
    }

    /**
     * Interne Klasse für Türschloss-Daten
     */
    public static class DoorLock {
        public final String key;
        public final String ownerUuid;
        public final String ownerName;
        public final boolean isPublic;

        public DoorLock(String key, String ownerUuid, String ownerName, boolean isPublic) {
            this.key = key;
            this.ownerUuid = ownerUuid;
            this.ownerName = ownerName;
            this.isPublic = isPublic;
        }
    }
}

