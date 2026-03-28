package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.LeutnantHealthService;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class LeutnantHealthSyncListener implements Listener {

    private final LeutnantHealthService leutnantHealthService;

    public LeutnantHealthSyncListener(RuinsCore plugin) {
        this.leutnantHealthService = plugin.getLeutnantHealthService();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        leutnantHealthService.sync(event.getPlayer());
    }
}


