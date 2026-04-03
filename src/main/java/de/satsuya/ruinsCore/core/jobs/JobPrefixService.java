package de.satsuya.ruinsCore.core.jobs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

/**
 * Service zur Verwaltung von Job-Prefixen und Suffixen
 */
public final class JobPrefixService {

    private final JobService jobService;

    public JobPrefixService(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Aktualisiere den Prefix/Suffix eines Spielers basierend auf seinem Job
     */
    public void updatePlayerPrefix(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        }

        // Entferne Spieler aus existierenden Job-Teams
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }

        // Hole den Job des Spielers
        java.util.Optional<JobType> jobOptional = jobService.getJob(player.getUniqueId());
        
        if (jobOptional.isEmpty()) {
            // Kein Job - nutze normalen Namen
            player.setPlayerListName(null);
            player.setCustomName(null);
            return;
        }

        JobType jobType = jobOptional.get();
        JobColor jobColor = JobColor.fromJobType(jobType);

        if (jobColor == null) {
            return;
        }

        // Erstelle oder hole Team für diesen Job
        String teamName = jobType.getId();
        Team team = scoreboard.getTeam(teamName);
        
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        // Setze Prefix und Suffix
        team.setPrefix(jobColor.getDisplayName() + " §r");
        team.setSuffix("");

        // Füge Spieler zum Team hinzu
        team.addEntry(player.getName());

        // Setze Custom Name (für Nameplate über dem Kopf)
        player.setCustomName(jobColor.getDisplayName() + " §r" + player.getName());
        player.setCustomNameVisible(true);

        // Setze Tab-Namen
        player.setPlayerListName(jobColor.getDisplayName() + " §r" + player.getName());
    }

    /**
     * Entferne den Prefix eines Spielers
     */
    public void removePlayerPrefix(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        }

        // Entferne Spieler aus Job-Teams
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }

        player.setPlayerListName(null);
        player.setCustomName(null);
        player.setCustomNameVisible(false);
    }
}

