package de.satsuya.ruinsCore.core.economy;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service für Geldanfragen und -transfers
 */
public final class MoneyTransactionService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;
    private final EconomyService economyService;

    public MoneyTransactionService(DatabaseManager databaseManager, LoggerUtil loggerUtil, EconomyService economyService) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
        this.economyService = economyService;
    }

    /**
     * Erstellt eine Geldanfrage
     */
    public boolean createMoneyRequest(UUID requester, UUID target, double amount) {
        if (amount <= 0) {
            return false;
        }

        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO money_requests (requester_uuid, target_uuid, amount, status, created_at) "
                + "VALUES (?, ?, ?, 'PENDING', CURRENT_TIMESTAMP)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, requester.toString());
            statement.setString(2, target.toString());
            statement.setDouble(3, amount);
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            loggerUtil.severe("Geldanfrage konnte nicht erstellt werden.", exception);
            return false;
        }
    }

    /**
     * Gibt alle ausstehenden Anfragen eines Spielers zurück
     */
    public List<MoneyRequest> getPendingRequests(UUID playerUuid) {
        Connection connection = databaseManager.getConnectionOrNull();
        List<MoneyRequest> requests = new ArrayList<>();

        if (connection == null) {
            return requests;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, requester_uuid, target_uuid, amount, created_at FROM money_requests "
                + "WHERE target_uuid = ? AND status = 'PENDING' ORDER BY created_at DESC")) {
            statement.setString(1, playerUuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(new MoneyRequest(
                            resultSet.getInt("id"),
                            UUID.fromString(resultSet.getString("requester_uuid")),
                            UUID.fromString(resultSet.getString("target_uuid")),
                            resultSet.getDouble("amount"),
                            resultSet.getString("created_at")
                    ));
                }
            }
        } catch (SQLException exception) {
            loggerUtil.severe("Geldanfragen konnten nicht gelesen werden.", exception);
        }

        return requests;
    }

    /**
     * Akzeptiert eine Geldanfrage
     */
    public boolean acceptRequest(int requestId) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        try {
            // Geldanfrage abrufen
            String selectSql = "SELECT requester_uuid, target_uuid, amount FROM money_requests WHERE id = ? AND status = 'PENDING'";
            MoneyRequest request = null;

            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setInt(1, requestId);

                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        return false;
                    }

                    request = new MoneyRequest(
                            requestId,
                            UUID.fromString(resultSet.getString("requester_uuid")),
                            UUID.fromString(resultSet.getString("target_uuid")),
                            resultSet.getDouble("amount"),
                            ""
                    );
                }
            }

            if (request == null) {
                return false;
            }

            // Guthaben abziehen
            if (!economyService.removeBalance(request.getTargetUuid(), request.getAmount())) {
                return false;
            }

            // Guthaben hinzufügen
            economyService.addBalance(request.getRequesterUuid(), request.getAmount());

            // Status aktualisieren
            String updateSql = "UPDATE money_requests SET status = 'ACCEPTED' WHERE id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                updateStatement.setInt(1, requestId);
                updateStatement.executeUpdate();
            }

            return true;
        } catch (SQLException exception) {
            loggerUtil.severe("Anfrage konnte nicht akzeptiert werden.", exception);
            return false;
        }
    }

    /**
     * Lehnt eine Geldanfrage ab
     */
    public boolean declineRequest(int requestId) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        String sql = "UPDATE money_requests SET status = 'DECLINED' WHERE id = ? AND status = 'PENDING'";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            loggerUtil.severe("Anfrage konnte nicht abgelehnt werden.", exception);
            return false;
        }
    }

    /**
     * Findet eine Geldanfrage nach ID
     */
    public Optional<MoneyRequest> findRequestById(int requestId) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return Optional.empty();
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, requester_uuid, target_uuid, amount, created_at FROM money_requests WHERE id = ?")) {
            statement.setInt(1, requestId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new MoneyRequest(
                            resultSet.getInt("id"),
                            UUID.fromString(resultSet.getString("requester_uuid")),
                            UUID.fromString(resultSet.getString("target_uuid")),
                            resultSet.getDouble("amount"),
                            resultSet.getString("created_at")
                    ));
                }
            }
        } catch (SQLException exception) {
            loggerUtil.severe("Anfrage konnte nicht gefunden werden.", exception);
        }

        return Optional.empty();
    }

    /**
     * Innere Klasse für Geldanfragen
     */
    public static class MoneyRequest {
        private final int id;
        private final UUID requesterUuid;
        private final UUID targetUuid;
        private final double amount;
        private final String createdAt;

        public MoneyRequest(int id, UUID requesterUuid, UUID targetUuid, double amount, String createdAt) {
            this.id = id;
            this.requesterUuid = requesterUuid;
            this.targetUuid = targetUuid;
            this.amount = amount;
            this.createdAt = createdAt;
        }

        public int getId() {
            return id;
        }

        public UUID getRequesterUuid() {
            return requesterUuid;
        }

        public UUID getTargetUuid() {
            return targetUuid;
        }

        public double getAmount() {
            return amount;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}

