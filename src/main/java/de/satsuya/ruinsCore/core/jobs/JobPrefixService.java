package de.satsuya.ruinsCore.core.jobs;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Service zur Verwaltung von Job-Prefixen und Suffixen
 */
public final class JobPrefixService {

    private final JobService jobService;

    public JobPrefixService(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Aktualisiere das Nametag eines Spielers basierend auf seinem Job
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

        String teamName = jobType.getId();

        // WICHTIG: Da jeder Spieler ein eigenes Scoreboard für die Sidebar hat,
        // müssen wir sicherstellen, dass dieser Prefix-Team Eintrag auf JEDEM
        // aktiven Scoreboard der Spieler registriert wird, damit alle das Nametag sehen!
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Scoreboard pScoreboard = onlinePlayer.getScoreboard();
            if (pScoreboard == null) continue;

            // Entferne Spieler aus falschen Job-Teams
            for (Team team : pScoreboard.getTeams()) {
                if (team.hasEntry(player.getName()) && !team.getName().equals(teamName)) {
                    team.removeEntry(player.getName());
                }
            }

            // Erstelle oder hole Team für diesen Job
            Team team = pScoreboard.getTeam(teamName);
            if (team == null) {
                team = pScoreboard.registerNewTeam(teamName);
            }

            // WICHTIG: Verwende die saubere Component-API für Paper 1.21.4!
            team.prefix(jobColor.getDisplayComponent().append(Component.text(" ", net.kyori.adventure.text.format.NamedTextColor.WHITE)));
            team.color(net.kyori.adventure.text.format.NamedTextColor.WHITE);

            // Wichtige Optionen für korrektes Nametag-Verhalten
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

            // Füge Spieler zum Team hinzu falls noch nicht geschehen
            if (!team.hasEntry(player.getName())) {
                team.addEntry(player.getName());
            }
        }
            
        // Setze Tab-Namen (der Name in der Player-Liste) sauber über Component
        Component tabName = Component.empty()
            .append(jobColor.getDisplayComponent())
            .append(Component.text(" " + player.getName(), net.kyori.adventure.text.format.NamedTextColor.WHITE));
        player.playerListName(tabName);
    }

    /**
     * Entferne den Prefix eines Spielers
     */
    public void removePlayerPrefix(Player player) {
        // Muss auch auf allen Scoreboards entfernt werden!
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Scoreboard pScoreboard = onlinePlayer.getScoreboard();
            if (pScoreboard == null) continue;

            for (Team team : pScoreboard.getTeams()) {
                if (team.hasEntry(player.getName())) {
                    team.removeEntry(player.getName());
                }
            }
        }

        // Setze Tab-Namen auf Standard
        player.playerListName(null);
    }
}




