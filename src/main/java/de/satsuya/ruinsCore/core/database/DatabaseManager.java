package de.satsuya.ruinsCore.core.database;

import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public final class DatabaseManager {

    private final JavaPlugin plugin;
    private final LoggerUtil loggerUtil;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin, LoggerUtil loggerUtil) {
        this.plugin = plugin;
        this.loggerUtil = loggerUtil;
    }

    public void connect() {
        if (isConnected()) {
            return;
        }

        try {
            if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
                loggerUtil.warning("Plugin-Datenordner konnte nicht erstellt werden.");
            }

            String fileName = plugin.getConfig().getString("database.file", "ruinscore.db");
            File databaseFile = new File(plugin.getDataFolder(), fileName);
            String jdbcUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            connection = DriverManager.getConnection(jdbcUrl);
            loggerUtil.info("SQLite verbunden: " + databaseFile.getAbsolutePath());
        } catch (SQLException exception) {
            loggerUtil.severe("SQLite-Verbindung konnte nicht hergestellt werden.", exception);
        }
    }

    public void initializeSchema() {
        executeUpdate("""
                CREATE TABLE IF NOT EXISTS staff_alert_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender TEXT NOT NULL,
                    message TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_jobs (
                    player_uuid TEXT PRIMARY KEY,
                    job_type TEXT NOT NULL
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS job_leaders (
                    job_type TEXT NOT NULL,
                    player_uuid TEXT NOT NULL,
                    PRIMARY KEY (job_type, player_uuid)
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_economy (
                    player_uuid TEXT PRIMARY KEY,
                    balance REAL NOT NULL DEFAULT 0.0,
                    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS money_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    requester_uuid TEXT NOT NULL,
                    target_uuid TEXT NOT NULL,
                    amount REAL NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_warnings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid TEXT NOT NULL,
                    warned_by TEXT NOT NULL,
                    reason TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_sizes (
                    player_uuid TEXT PRIMARY KEY,
                    size REAL NOT NULL DEFAULT 1.0
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS auctions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    seller_uuid TEXT NOT NULL,
                    seller_name TEXT NOT NULL,
                    item_data TEXT NOT NULL,
                    price REAL NOT NULL,
                    created_at INTEGER NOT NULL,
                    expires_at INTEGER NOT NULL
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_marriages (
                    player1_uuid TEXT NOT NULL,
                    player2_uuid TEXT NOT NULL,
                    married_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (player1_uuid, player2_uuid)
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_marriages_names (
                    player_uuid TEXT PRIMARY KEY,
                    player_name TEXT NOT NULL
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_playtime (
                    player_uuid TEXT PRIMARY KEY,
                    playtime_millis LONG NOT NULL DEFAULT 0
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_welcome (
                    player_uuid TEXT PRIMARY KEY,
                    player_name TEXT NOT NULL,
                    first_join LONG NOT NULL
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS door_locks (
                    door_key TEXT PRIMARY KEY,
                    world TEXT NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    z INTEGER NOT NULL,
                    owner_uuid TEXT NOT NULL,
                    owner_name TEXT NOT NULL,
                    is_public BOOLEAN NOT NULL DEFAULT 1,
                    created_at LONG NOT NULL
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS door_lock_whitelist (
                    door_key TEXT NOT NULL,
                    player_uuid TEXT NOT NULL,
                    player_name TEXT NOT NULL,
                    PRIMARY KEY (door_key, player_uuid)
                )
                """);

        executeUpdate("""
                CREATE TABLE IF NOT EXISTS reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    reporter_uuid TEXT NOT NULL,
                    reporter_name TEXT NOT NULL,
                    reported_uuid TEXT NOT NULL,
                    reported_name TEXT NOT NULL,
                    reason TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status TEXT NOT NULL DEFAULT 'OPEN'
                )
                """);
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException exception) {
            return false;
        }
    }

    public void close() {
        if (!isConnected()) {
            return;
        }

        try {
            connection.close();
            loggerUtil.info("SQLite-Verbindung wurde geschlossen.");
        } catch (SQLException exception) {
            loggerUtil.severe("SQLite-Verbindung konnte nicht geschlossen werden.", exception);
        }
    }

    public void executeUpdate(String sql, Object... parameters) {
        if (!isConnected()) {
            loggerUtil.warning("SQL Update übersprungen, weil keine Verbindung aktiv ist.");
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, parameters);
            statement.executeUpdate();
        } catch (SQLException exception) {
            loggerUtil.severe("SQL Update fehlgeschlagen: " + sql, exception);
        }
    }

    public void executeUpdate(String sql, StatementSetter setter) {
        if (!isConnected()) {
            loggerUtil.warning("SQL Update übersprungen, weil keine Verbindung aktiv ist.");
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setter.setValues(statement);
            statement.executeUpdate();
        } catch (SQLException exception) {
            loggerUtil.severe("SQL Update fehlgeschlagen: " + sql, exception);
        }
    }

    public ResultSet executeQuery(String sql, Object... parameters) throws SQLException {
        if (!isConnected()) {
            throw new SQLException("Keine aktive SQLite-Verbindung.");
        }

        PreparedStatement statement = connection.prepareStatement(sql);
        bindParameters(statement, parameters);
        return statement.executeQuery();
    }

    public ResultSet executeQuery(String sql, StatementSetter setter) throws SQLException {
        if (!isConnected()) {
            throw new SQLException("Keine aktive SQLite-Verbindung.");
        }

        PreparedStatement statement = connection.prepareStatement(sql);
        setter.setValues(statement);
        return statement.executeQuery();
    }

    public Connection getConnectionOrNull() {
        if (!isConnected()) {
            loggerUtil.warning("Keine aktive SQLite-Verbindung verfügbar.");
            return null;
        }

        return connection;
    }

    private void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int index = 0; index < parameters.length; index++) {
            statement.setObject(index + 1, parameters[index]);
        }
    }
}

