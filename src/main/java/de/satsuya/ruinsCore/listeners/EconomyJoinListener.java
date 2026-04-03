package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.economy.EconomyService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Verwaltet Economy beim Spieler-Login
 */
public final class EconomyJoinListener implements Listener {

    private final EconomyService economyService;

    public EconomyJoinListener(RuinsCore plugin) {
        this.economyService = plugin.getEconomyService();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Stelle sicher, dass das Konto existiert
        double balance = economyService.getBalance(event.getPlayer().getUniqueId());
        if (balance == 0) {
            economyService.setBalance(event.getPlayer().getUniqueId(), 0);
        }
    }
}

