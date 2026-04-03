package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.vanish.VanishService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener zum Verstecken von vanished Spielern bei neuen Joins
 */
public final class VanishJoinListener implements Listener {

    private final VanishService vanishService;

    public VanishJoinListener(RuinsCore plugin) {
        this.vanishService = plugin.getVanishService();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Verstecke alle vanished Spieler für den neu joinenden Spieler
        vanishService.hideVanishedPlayersFor(event.getPlayer());
    }
}

