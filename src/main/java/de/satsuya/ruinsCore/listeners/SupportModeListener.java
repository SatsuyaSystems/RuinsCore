package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.support.SupportModeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener zum Cleanup von Support Mode beim Disconnect
 */
public final class SupportModeListener implements Listener {

    private final SupportModeService supportModeService;

    public SupportModeListener(RuinsCore plugin) {
        this.supportModeService = plugin.getSupportModeService();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Entferne Support Mode Daten wenn Spieler disconnectet
        supportModeService.disableSupportMode(event.getPlayer());
    }
}

