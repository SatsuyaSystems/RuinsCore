package de.satsuya.ruinsCore.core.welcome;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Service zur Verwaltung von Welcome Messages für neue Spieler
 */
public final class WelcomeService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;

    public WelcomeService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
    }

    /**
     * Prüfe, ob ein Spieler neu ist (erstes Mal online)
     */
    public boolean isNewPlayer(UUID playerUuid) {
        try {
            ResultSet resultSet = databaseManager.executeQuery(
                "SELECT player_uuid FROM player_welcome WHERE player_uuid = ?",
                playerUuid.toString()
            );

            return resultSet == null || !resultSet.next();
        } catch (SQLException e) {
            loggerUtil.warning("Fehler beim Prüfen des neuen Spielers: " + e.getMessage());
            return false;
        }
    }

    /**
     * Markiere einen Spieler als begrüßt
     */
    public void markAsWelcomed(UUID playerUuid, String playerName) {
        try {
            databaseManager.executeUpdate(
                "INSERT OR IGNORE INTO player_welcome (player_uuid, player_name, first_join) VALUES (?, ?, ?)",
                playerUuid.toString(), playerName, System.currentTimeMillis()
            );
        } catch (Exception e) {
            loggerUtil.warning("Fehler beim Speichern der Welcome Info: " + e.getMessage());
        }
    }

    /**
     * Gebe die Welcome Message zurück
     */
    public String[] getWelcomeMessage(String playerName) {
        return new String[]{
            "§5§l═══════════════════════════════════════════════════════════════",
            "§d",
            "§5                    §d✨ WILLKOMMEN §5✨",
            "§d",
            "§d             Du bist neu auf dem §5RuinsCore §dServer!",
            "§d",
            "§5                    Hallo, §d" + playerName + "§5!",
            "§d",
            "§d Wir freuen uns, dich hier zu haben. Hier sind ein paar wichtige",
            "§d Informationen zum Einstieg:",
            "§d",
            "§5 📋 Commands:",
            "§d   • §5/pay§d - Zahle jemandem Geld",
            "§d   • §5/money§d - Schaue dein Guthaben an",
            "§d   • §5/playtime§d - Sehe deine Spielzeit",
            "§d   • §5/help§d - Alle Commands anschauen",
            "§d",
            "§5 💼 Berufe:",
            "§d   Es gibt verschiedene Berufe die unterschiedliche Fähigkeiten",
            "§d   bieten. Wähle einen der zu dir passt!",
            "§d",
            "§5 💰 Wirtschaft:",
            "§d   Verdiene Geld durch deinen Beruf und handle mit anderen",
            "§d   Spielern. Nutze §5/auction§d um Items zu verkaufen!",
            "§d",
            "§5 📞 Hilfe:",
            "§d   Wenn du Fragen hast, schreib in den Chat oder frag einen",
            "§d   Supporter um Hilfe.",
            "§d",
            "§d",
            "§5§l═══════════════════════════════════════════════════════════════"
        };
    }
}

