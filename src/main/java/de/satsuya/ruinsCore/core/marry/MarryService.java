package de.satsuya.ruinsCore.core.marry;

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
 * Service zur Verwaltung von Verheiratungen
 */
public final class MarryService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;
    private final Map<UUID, UUID> marriageCache = new HashMap<>();

    public MarryService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
        loadAllMarriages();
    }

    /**
     * Verheirate zwei Spieler
     */
    public void marryPlayers(UUID player1, UUID player2) {
        if (isMarried(player1) || isMarried(player2)) {
            loggerUtil.warning("Einer der Spieler ist bereits verheiratet!");
            return;
        }

        databaseManager.executeUpdate(
            "INSERT INTO player_marriages (player1_uuid, player2_uuid) VALUES (?, ?)",
            player1.toString(), player2.toString()
        );

        marriageCache.put(player1, player2);
        marriageCache.put(player2, player1);

        loggerUtil.info("Spieler " + player1 + " und " + player2 + " sind jetzt verheiratet!");
    }

    /**
     * Trenne ein Ehepaar
     */
    public void divorcePlayer(UUID player) {
        UUID partner = getPartner(player);
        if (partner == null) {
            return;
        }

        databaseManager.executeUpdate(
            "DELETE FROM player_marriages WHERE (player1_uuid = ? AND player2_uuid = ?) OR (player1_uuid = ? AND player2_uuid = ?)",
            player.toString(), partner.toString(), partner.toString(), player.toString()
        );

        marriageCache.remove(player);
        marriageCache.remove(partner);

        loggerUtil.info("Spieler " + player + " und " + partner + " sind jetzt geschieden!");
    }

    /**
     * Gebe den Partner eines Spielers zurück
     */
    public UUID getPartner(UUID playerUuid) {
        return marriageCache.getOrDefault(playerUuid, null);
    }

    /**
     * Prüfe, ob ein Spieler verheiratet ist
     */
    public boolean isMarried(UUID playerUuid) {
        return marriageCache.containsKey(playerUuid);
    }

    /**
     * Gebe den Namen des Partners
     */
    public String getPartnerName(UUID playerUuid) {
        UUID partner = getPartner(playerUuid);
        if (partner == null) {
            return null;
        }

        Player partnerPlayer = Bukkit.getPlayer(partner);
        if (partnerPlayer != null) {
            return partnerPlayer.getName();
        }

        // Versuche Namen aus der Datenbank zu laden (falls Offline)
        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT player_name FROM player_marriages_names WHERE player_uuid = ?",
                partner.toString()
            );
            if (resultSet != null && resultSet.next()) {
                return resultSet.getString("player_name");
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Laden des Partner-Namens: " + e.getMessage());
        }

        return "Unknown";
    }

    /**
     * Lade alle Verheiratungen beim Start
     */
    private void loadAllMarriages() {
        try {
            ResultSet resultSet = databaseManager.executeQuery("SELECT player1_uuid, player2_uuid FROM player_marriages");
            if (resultSet != null) {
                while (resultSet.next()) {
                    UUID player1 = UUID.fromString(resultSet.getString("player1_uuid"));
                    UUID player2 = UUID.fromString(resultSet.getString("player2_uuid"));

                    marriageCache.put(player1, player2);
                    marriageCache.put(player2, player1);
                }
            }
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Laden der Verheiratungen: " + e.getMessage());
        }
    }

    /**
     * Speichere den Partner-Namen offline
     */
    public void savePartnerName(UUID playerUuid, String playerName) {
        databaseManager.executeUpdate(
            "INSERT OR REPLACE INTO player_marriages_names (player_uuid, player_name) VALUES (?, ?)",
            playerUuid.toString(), playerName
        );
    }
}

