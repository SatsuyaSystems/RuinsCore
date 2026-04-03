package de.satsuya.ruinsCore.core.size;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Service zur Verwaltung von Spielergröße
 */
public final class SizeService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;
    private static final float DEFAULT_SIZE = 1.0f;
    private static final float MIN_SIZE = 0.6f;
    private static final float MAX_SIZE = 1.1f;

    public SizeService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
    }

    /**
     * Setze die Größe eines Spielers
     */
    public void setPlayerSize(UUID playerUuid, float size) {
        if (size < MIN_SIZE || size > MAX_SIZE) {
            loggerUtil.warning("Ungültige Größe: " + size);
            return;
        }

        try {
            // INSERT OR REPLACE - funktioniert ob Eintrag existiert oder nicht
            databaseManager.executeUpdate(
                "INSERT OR REPLACE INTO player_sizes (player_uuid, size) VALUES (?, ?)",
                playerUuid.toString(),
                size
            );
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Speichern der Spielergröße", e);
        }
    }

    /**
     * Gibt die gespeicherte Größe eines Spielers zurück
     */
    public float getPlayerSize(UUID playerUuid) {
        try (ResultSet result = databaseManager.executeQuery(
                "SELECT size FROM player_sizes WHERE player_uuid = ?",
                playerUuid.toString()
        )) {
            if (result != null && result.next()) {
                return result.getFloat("size");
            }
        } catch (SQLException e) {
            loggerUtil.severe("Fehler beim Abrufen der Spielergröße", e);
        }
        return DEFAULT_SIZE;
    }

    /**
     * Wende die gespeicherte Größe auf einen Spieler an
     */
    public void applyPlayerSize(Player player) {
        float size = getPlayerSize(player.getUniqueId());
        applySize(player, size);
    }

    /**
     * Wende eine Größe direkt auf einen Spieler an
     */
    private void applySize(Player player, float size) {
        // Nutze den attribute Command um die Größe zu setzen
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            "attribute " + player.getName() + " minecraft:scale base set " + size);
    }

    /**
     * Gebe MIN_SIZE zurück
     */
    public float getMinSize() {
        return MIN_SIZE;
    }

    /**
     * Gebe MAX_SIZE zurück
     */
    public float getMaxSize() {
        return MAX_SIZE;
    }

    /**
     * Gebe DEFAULT_SIZE zurück
     */
    public float getDefaultSize() {
        return DEFAULT_SIZE;
    }
}

