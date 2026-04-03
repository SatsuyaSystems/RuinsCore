package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobPrefixService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener zum Aktualisieren von Job-Prefixen beim Join
 */
public final class JobPrefixListener implements Listener {

    private final JobPrefixService jobPrefixService;
    private final RuinsCore plugin;

    public JobPrefixListener(RuinsCore plugin) {
        this.jobPrefixService = plugin.getJobPrefixService();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Verzögere die Aktualisierung um sicherzustellen, dass Scoreboard bereits erstellt wurde
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            jobPrefixService.updatePlayerPrefix(event.getPlayer());
        }, 1L);
    }
}

