package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.WacheHealthService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class WacheHealthSyncListener implements Listener {

    private final WacheHealthService wacheHealthService;

    public WacheHealthSyncListener(RuinsCore plugin) {
        this.wacheHealthService = plugin.getWacheHealthService();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        wacheHealthService.sync(event.getPlayer());
    }
}

