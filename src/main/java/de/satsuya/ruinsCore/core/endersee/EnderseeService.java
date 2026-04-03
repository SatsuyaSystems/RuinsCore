package de.satsuya.ruinsCore.core.endersee;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service zur Verwaltung von Ender-Chest Ansichten
 * Verwaltet offene Endersee-Fenster mit Live-Sync
 */
public final class EnderseeService {

    private final Map<UUID, UUID> openEndersees = new HashMap<>(); // Viewer -> Target

    /**
     * Öffne einen Ender-Chest für einen Spieler
     */
    public void openEnderChest(Player viewer, Player target) {
        // Speichere die Beziehung
        openEndersees.put(viewer.getUniqueId(), target.getUniqueId());
        
        // Öffne den Ender-Chest des Ziel-Spielers
        viewer.openInventory(target.getEnderChest());
        
        viewer.sendMessage("§a✓ Ender-Chest von §b" + target.getName() + " §aöffnet.");
    }

    /**
     * Schließe einen Ender-Chest
     */
    public void closeEnderChest(Player viewer) {
        openEndersees.remove(viewer.getUniqueId());
        viewer.sendMessage("§c✗ Ender-Chest geschlossen.");
    }

    /**
     * Prüfe ob Spieler einen Ender-Chest anschaut
     */
    public boolean isViewingEnderChest(UUID viewerUuid) {
        return openEndersees.containsKey(viewerUuid);
    }

    /**
     * Gibt den Ziel-Spieler zurück, dessen Ender-Chest angeschaut wird
     */
    public UUID getViewedPlayer(UUID viewerUuid) {
        return openEndersees.get(viewerUuid);
    }

    /**
     * Prüfe ob zwei Spieler in einer Endersee-Session sind
     */
    public boolean isInEnderseeSession(UUID viewerUuid, UUID targetUuid) {
        return openEndersees.getOrDefault(viewerUuid, null) != null && 
               openEndersees.get(viewerUuid).equals(targetUuid);
    }

    /**
     * Gibt alle aktiven Endersee-Sessions zurück
     */
    public Map<UUID, UUID> getAllSessions() {
        return new HashMap<>(openEndersees);
    }

    /**
     * Schließe alle Endersee-Sessions (beim Plugin Disable)
     */
    public void closeAllSessions() {
        for (UUID viewerUuid : new HashMap<>(openEndersees).keySet()) {
            Player viewer = Bukkit.getPlayer(viewerUuid);
            if (viewer != null) {
                viewer.closeInventory();
                closeEnderChest(viewer);
            } else {
                openEndersees.remove(viewerUuid);
            }
        }
    }
}

