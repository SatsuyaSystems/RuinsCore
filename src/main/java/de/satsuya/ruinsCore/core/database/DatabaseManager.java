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
            loggerUtil.warning("SQL Update uebersprungen, weil keine Verbindung aktiv ist.");
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParameters(statement, parameters);
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

    public Connection getConnectionOrNull() {
        if (!isConnected()) {
            loggerUtil.warning("Keine aktive SQLite-Verbindung verfuegbar.");
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

