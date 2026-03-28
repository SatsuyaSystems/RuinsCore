package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.RitterHealthService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class RitterHealthSyncListener implements Listener {

    private final RitterHealthService ritterHealthService;

    public RitterHealthSyncListener(RuinsCore plugin) {
        this.ritterHealthService = plugin.getRitterHealthService();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ritterHealthService.sync(event.getPlayer());
    }
}

