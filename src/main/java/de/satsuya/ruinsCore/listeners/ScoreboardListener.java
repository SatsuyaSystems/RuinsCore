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

    public ScoreboardListener(RuinsCore plugin) {
        this.scoreboardService = plugin.getScoreboardService();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Erstelle Scoreboard für neuen Spieler
        scoreboardService.createScoreboard(event.getPlayer());
        scoreboardService.updateTablist(event.getPlayer());
    }
}

