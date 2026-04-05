package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.scoreboard.ScoreboardService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener zum Erstellen von Scoreboards beim Join
 */
public final class ScoreboardListener implements Listener {

    private final ScoreboardService scoreboardService;
    private final RuinsCore plugin;

    public ScoreboardListener(RuinsCore plugin) {
        this.scoreboardService = plugin.getScoreboardService();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Erstelle Scoreboard für neuen Spieler
        scoreboardService.createScoreboard(event.getPlayer());
        scoreboardService.updateTablist(event.getPlayer());

        // Da der Spieler jetzt ein nagelneues privates Scoreboard hat,
        // kennt SEIN Scoreboard die Teams der ANDEREN Spieler nicht mehr!
        // Wir müssen hier einmalig für alle Online Spieler updatePlayerPrefix aufrufen,
        // damit sie in die Teams dieses neuen Scoreboards eingetragen werden.
        org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(
            plugin,
            () -> {
                de.satsuya.ruinsCore.core.jobs.JobPrefixService jobPrefixService = plugin.getJobPrefixService();
                for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                    jobPrefixService.updatePlayerPrefix(p);
                }
            },
            10L
        );
    }
}

