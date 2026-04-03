package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.freeze.FreezeService;
import de.satsuya.ruinsCore.core.invsee.InvseeService;
import de.satsuya.ruinsCore.core.endersee.EnderseeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Listener für Freeze, Invsee und Endersee Funktionalität
 */
public final class FreezeInvseeListener implements Listener {

    private final FreezeService freezeService;
    private final InvseeService invseeService;
    private final EnderseeService enderseeService;

    public FreezeInvseeListener(RuinsCore plugin) {
        this.freezeService = plugin.getFreezeService();
        this.invseeService = plugin.getInvseeService();
        this.enderseeService = plugin.getEnderseeService();
    }

    /**
     * Verhindere Bewegung gefrorener Spieler
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (freezeService.isFrozen(event.getPlayer().getUniqueId())) {
            // Teleportiere zurück zur ursprünglichen Position
            event.setCancelled(true);
        }
    }

    /**
     * Sync Inventar-Änderungen live
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        // Prüfe ob der Spieler ein Inventar anschaut
        if (invseeService.isViewingInventory(event.getWhoClicked().getUniqueId())) {
            // Live-Sync: Das Inventar wird automatisch synchronisiert
            // Bukkit synchronisiert das Inventar automatisch beim Click
            // Keine manuellen Aktionen nötig
        }
        
        // Prüfe ob der Spieler einen Ender-Chest anschaut
        if (enderseeService.isViewingEnderChest(event.getWhoClicked().getUniqueId())) {
            // Live-Sync: Der Ender-Chest wird automatisch synchronisiert
            // Bukkit synchronisiert den Ender-Chest automatisch beim Click
            // Keine manuellen Aktionen nötig
        }
    }

    /**
     * Cleanup beim Schließen des Inventars
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        // Prüfe ob es eine Invsee-Session ist
        if (invseeService.isViewingInventory(event.getPlayer().getUniqueId())) {
            invseeService.closeInventory((org.bukkit.entity.Player) event.getPlayer());
        }
        
        // Prüfe ob es eine Endersee-Session ist
        if (enderseeService.isViewingEnderChest(event.getPlayer().getUniqueId())) {
            enderseeService.closeEnderChest((org.bukkit.entity.Player) event.getPlayer());
        }
    }
}

