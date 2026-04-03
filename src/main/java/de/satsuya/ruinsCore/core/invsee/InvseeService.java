package de.satsuya.ruinsCore.core.invsee;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service zur Verwaltung von Inventar-Ansichten
 * Verwaltet offene Invsee-Fenster mit Live-Sync
 */
public final class InvseeService {

    private final Map<UUID, UUID> openInvsees = new HashMap<>(); // Viewer -> Target

    /**
     * Öffne ein Inventar-Fenster für einen Spieler
     */
    public void openInventory(Player viewer, Player target) {
        // Speichere die Beziehung
        openInvsees.put(viewer.getUniqueId(), target.getUniqueId());
        
        // Öffne das Inventar des Ziel-Spielers
        viewer.openInventory(target.getInventory());
        
        viewer.sendMessage("§a✓ Inventar von §b" + target.getName() + " §aöffnet.");
    }

    /**
     * Schließe ein Inventar-Fenster
     */
    public void closeInventory(Player viewer) {
        openInvsees.remove(viewer.getUniqueId());
        viewer.sendMessage("§c✗ Inventar geschlossen.");
    }

    /**
     * Prüfe ob Spieler ein Inventar anschaut
     */
    public boolean isViewingInventory(UUID viewerUuid) {
        return openInvsees.containsKey(viewerUuid);
    }

    /**
     * Gibt den Ziel-Spieler zurück, dessen Inventar angeschaut wird
     */
    public UUID getViewedPlayer(UUID viewerUuid) {
        return openInvsees.get(viewerUuid);
    }

    /**
     * Prüfe ob zwei Spieler in einer Invsee-Session sind
     */
    public boolean isInInvseeSession(UUID viewerUuid, UUID targetUuid) {
        return openInvsees.getOrDefault(viewerUuid, null) != null && 
               openInvsees.get(viewerUuid).equals(targetUuid);
    }

    /**
     * Gibt alle aktiven Invsee-Sessions zurück
     */
    public Map<UUID, UUID> getAllSessions() {
        return new HashMap<>(openInvsees);
    }

    /**
     * Schließe alle Invsee-Sessions (beim Plugin Disable)
     */
    public void closeAllSessions() {
        for (UUID viewerUuid : new HashMap<>(openInvsees).keySet()) {
            Player viewer = Bukkit.getPlayer(viewerUuid);
            if (viewer != null) {
                viewer.closeInventory();
                closeInventory(viewer);
            } else {
                openInvsees.remove(viewerUuid);
            }
        }
    }
}

