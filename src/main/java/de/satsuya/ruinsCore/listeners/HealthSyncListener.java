package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobHealthService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class HealthSyncListener implements Listener {

    private final JobHealthService jobHealthService;

    public HealthSyncListener(RuinsCore plugin) {
        this.jobHealthService = plugin.getJobHealthService();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        jobHealthService.sync(event.getPlayer());
    }
}


