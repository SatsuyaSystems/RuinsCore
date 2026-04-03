package de.satsuya.ruinsCore.core.economy;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Service für die Verwaltung von Spieler-Geldkonten
 */
public final class EconomyService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;
    private final Server server;

    public EconomyService(DatabaseManager databaseManager, LoggerUtil loggerUtil, Server server) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
        this.server = server;
    }

    /**
     * Gibt das Guthaben eines Spielers zurück
     */
    public double getBalance(UUID playerUuid) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return 0;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT balance FROM player_economy WHERE player_uuid = ?")) {
            statement.setString(1, playerUuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                }
            }
        } catch (SQLException exception) {
            loggerUtil.severe("Guthaben konnte nicht gelesen werden.", exception);
        }

        return 0;
    }

    /**
     * Setzt das Guthaben eines Spielers
     */
    public boolean setBalance(UUID playerUuid, double amount) {
        if (amount < 0) {
            return false;
        }

        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO player_economy (player_uuid, balance) VALUES (?, ?) "
                + "ON CONFLICT(player_uuid) DO UPDATE SET balance = excluded.balance";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUuid.toString());
            statement.setDouble(2, amount);
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            loggerUtil.severe("Guthaben konnte nicht gesetzt werden.", exception);
            return false;
        }
    }

    /**
     * Fügt Guthaben hinzu
     */
    public boolean addBalance(UUID playerUuid, double amount) {
        if (amount <= 0) {
            return false;
        }

        double currentBalance = getBalance(playerUuid);
        return setBalance(playerUuid, currentBalance + amount);
    }

    /**
     * Zieht Guthaben ab
     */
    public boolean removeBalance(UUID playerUuid, double amount) {
        if (amount <= 0) {
            return false;
        }

        double currentBalance = getBalance(playerUuid);
        if (currentBalance < amount) {
            return false;
        }

        return setBalance(playerUuid, currentBalance - amount);
    }

    /**
     * Prüft ob ein Spieler genug Geld hat
     */
    public boolean hasBalance(UUID playerUuid, double amount) {
        return getBalance(playerUuid) >= amount;
    }

    /**
     * Formatiert den Betrag als Geldstring
     */
    public String formatBalance(double amount) {
        if (amount == (long) amount) {
            return String.format("%,d €", (long) amount);
        } else {
            return String.format("%,.2f €", amount);
        }
    }

    /**
     * Gibt den Spielernamen vom Spieler zurück
     */
    public String getPlayerName(UUID playerUuid) {
        OfflinePlayer player = server.getOfflinePlayer(playerUuid);
        return player.getName() != null ? player.getName() : "Unbekannt";
    }
}

