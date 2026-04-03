package de.satsuya.ruinsCore.core.scoreboard;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.economy.EconomyService;
import de.satsuya.ruinsCore.core.jobs.JobService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Service zur Verwaltung des Scoreboards und der Tablist
 */
public final class ScoreboardService {

    private final RuinsCore plugin;
    private final JobService jobService;
    private final EconomyService economyService;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public ScoreboardService(RuinsCore plugin, JobService jobService, EconomyService economyService) {
        this.plugin = plugin;
        this.jobService = jobService;
        this.economyService = economyService;
    }

    /**
     * Erstelle ein Scoreboard für einen Spieler
     */
    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        
        // Erstelle Objective mit Lila-Pink Gradient
        Objective objective = scoreboard.registerNewObjective(
            "ruinscore_main",
            "dummy",
            Component.text("§5§l◆ §dRuinsCore §5§l◆", NamedTextColor.LIGHT_PURPLE)
        );
        
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        // Setze initiale Scores
        updateScoreboard(player, objective);
        
        player.setScoreboard(scoreboard);
    }

    /**
     * Aktualisiere das Scoreboard für einen Spieler
     */
    public void updateScoreboard(Player player, Objective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        
        // Lösche alte Einträge
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        
        // Hole Spieler-Daten
        String jobName = getPlayerJob(player.getUniqueId());
        double money = economyService.getBalance(player.getUniqueId());
        String timeString = timeFormat.format(new Date());
        int ping = player.getPing();
        
        // Setze neue Scores (von unten nach oben)
        int score = 10;
        
        // Leerzeile
        objective.getScore("§5").setScore(score--);
        
        // Ping
        objective.getScore("§dPing: §5" + ping + "ms").setScore(score--);
        
        // Uhrzeit
        objective.getScore("§dUhrzeit: §5" + timeString).setScore(score--);
        
        // Leerzeile
        objective.getScore("§d").setScore(score--);
        
        // Geld
        String moneyFormatted = String.format("%.2f", money);
        objective.getScore("§dGeld: §5" + moneyFormatted + "€").setScore(score--);
        
        // Job
        objective.getScore("§dJob: §5" + jobName).setScore(score--);
    }

    /**
     * Aktualisiere die Tablist für einen Spieler
     */
    public void updateTablist(Player player) {
        String jobName = getPlayerJob(player.getUniqueId());
        double money = economyService.getBalance(player.getUniqueId());
        String moneyFormatted = String.format("%.2f", money);
        
        // Header mit Lila-Pink Gradient
        Component header = Component.text(
            "§5§l═══════════════════════════════════\n" +
            "§dWillkommen auf §5RuinsCore §dServer\n" +
            "§5§l═══════════════════════════════════",
            NamedTextColor.LIGHT_PURPLE
        );
        
        // Footer mit Lila-Pink Gradient
        Component footer = Component.text(
            "§5───────────────────────────────────\n" +
            "§dJob: §5" + jobName + " §d│ §dGeld: §5" + moneyFormatted + "€\n" +
            "§5───────────────────────────────────",
            NamedTextColor.LIGHT_PURPLE
        );
        
        player.sendPlayerListHeaderAndFooter(header, footer);
    }

    /**
     * Gibt den Job-Namen eines Spielers zurück
     */
    private String getPlayerJob(UUID playerUuid) {
        try {
            String job = jobService.getPlayerJob(playerUuid);
            return job != null && !job.isEmpty() ? job : "§cNone";
        } catch (Exception e) {
            return "§cError";
        }
    }

    /**
     * Aktualisiere alle Online-Spieler-Scoreboards
     */
    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard != null) {
                Objective objective = scoreboard.getObjective("ruinscore_main");
                if (objective != null) {
                    updateScoreboard(player, objective);
                    updateTablist(player);
                }
            }
        }
    }
}

