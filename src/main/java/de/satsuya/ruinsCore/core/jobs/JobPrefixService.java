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
        // Hole den Job des Spielers
        java.util.Optional<JobType> jobOptional = jobService.getJob(player.getUniqueId());
        
        if (jobOptional.isEmpty()) {
            // Kein Job - entferne Prefix
            removePlayerPrefix(player);
            return;
        }

        JobType jobType = jobOptional.get();
        JobColor jobColor = JobColor.fromJobType(jobType);

        if (jobColor == null) {
            return;
        }

        // Nutze Main Scoreboard für globale Teams
        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // Entferne Spieler aus existierenden Job-Teams
        for (Team team : mainScoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }

        // Erstelle oder hole Team für diesen Job
        String teamName = jobType.getId();
        Team team = mainScoreboard.getTeam(teamName);

        if (team == null) {
            team = mainScoreboard.registerNewTeam(teamName);
            // Setze Prefix und Suffix für das Team
            team.setPrefix(jobColor.getDisplayName() + " §r");
            team.setSuffix("");
            // Wichtig: Setze Option für Nametag-Sichtbarkeit
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }

        // Füge Spieler zum Team hinzu
        team.addEntry(player.getName());

        // Setze Tab-Namen (der Name in der Player-Liste)
        player.setPlayerListName(jobColor.getDisplayName() + " §r" + player.getName());
    }

    /**
     * Entferne den Prefix eines Spielers
     */
    public void removePlayerPrefix(Player player) {
        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // Entferne Spieler aus Job-Teams
        for (Team team : mainScoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }

        // Setze Tab-Namen auf Standard
        player.setPlayerListName(null);
    }
}

