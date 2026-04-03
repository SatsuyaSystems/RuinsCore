package de.satsuya.ruinsCore.core.scoreboard;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task zum periodischen Aktualisieren aller Scoreboards
 */
public final class ScoreboardUpdateTask extends BukkitRunnable {

    private final ScoreboardService scoreboardService;

    public ScoreboardUpdateTask(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    @Override
    public void run() {
        // Aktualisiere alle Scoreboards alle 0.5 Sekunden (10 Ticks)
        scoreboardService.updateAllScoreboards();
    }
}

