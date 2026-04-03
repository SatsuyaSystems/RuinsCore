package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.size.SizeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener zur Wiederherstellung der Spielergröße beim Join
 */
public final class SizeRestoreListener implements Listener {

    private final SizeService sizeService;

    public SizeRestoreListener(RuinsCore plugin) {
        this.sizeService = plugin.getSizeService();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Wende gespeicherte Größe an
        sizeService.applyPlayerSize(event.getPlayer());
    }
}

