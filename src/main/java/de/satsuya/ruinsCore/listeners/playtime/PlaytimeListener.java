package de.satsuya.ruinsCore.listeners.playtime;

import de.satsuya.ruinsCore.core.playtime.PlaytimeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener zum Tracken der Spielzeit
 */
public final class PlaytimeListener implements Listener {

    private final PlaytimeService playtimeService;

    public PlaytimeListener(PlaytimeService playtimeService) {
        this.playtimeService = playtimeService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playtimeService.startSession(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playtimeService.endSession(event.getPlayer().getUniqueId());
    }
}

