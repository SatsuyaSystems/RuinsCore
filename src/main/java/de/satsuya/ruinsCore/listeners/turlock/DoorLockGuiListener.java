package de.satsuya.ruinsCore.listeners.turlock;

import de.satsuya.ruinsCore.core.turlock.DoorLockGuiHolder;
import de.satsuya.ruinsCore.core.turlock.DoorLockWhitelistGuiHolder;
import de.satsuya.ruinsCore.core.turlock.DoorLockService;
import de.satsuya.ruinsCore.core.turlock.DoorLockGuiService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import java.util.UUID;

/**
 * Listener für Türschloss-GUI Interaktionen
 */
public final class DoorLockGuiListener implements Listener {

    private final DoorLockService doorLockService;
    private final DoorLockGuiService doorLockGuiService;

    public DoorLockGuiListener(DoorLockService doorLockService, DoorLockGuiService doorLockGuiService) {
        this.doorLockService = doorLockService;
        this.doorLockGuiService = doorLockGuiService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        // Hauptmenü Türschloss
        if (holder instanceof DoorLockGuiHolder) {
            handleDoorLockGui(event, (DoorLockGuiHolder) holder);
            return;
        }

        // Whitelist Verwaltung
        if (holder instanceof DoorLockWhitelistGuiHolder) {
            handleWhitelistGui(event, (DoorLockWhitelistGuiHolder) holder);
            return;
        }
    }

    private void handleDoorLockGui(InventoryClickEvent event, DoorLockGuiHolder guiHolder) {
        event.setCancelled(true);

        String doorKey = guiHolder.getDoorKey();
        int slot = event.getRawSlot();

        // Slot 11: Toggle Öffentlich/Privat
        if (slot == 11) {
            DoorLockService.DoorLock lock = doorLockService.getLockByKey(doorKey);
            if (lock != null) {
                doorLockService.setPublicByKey(doorKey, !lock.isPublic);
                event.getWhoClicked().sendMessage("§a✓ Türschloss Status geändert!");
                event.getWhoClicked().closeInventory();
            }
        }

        // Slot 15: Whitelist verwalten
        if (slot == 15) {
            Player player = (Player) event.getWhoClicked();
            doorLockGuiService.openWhitelistGui(player, doorKey);
        }
    }

     private void handleWhitelistGui(InventoryClickEvent event, DoorLockWhitelistGuiHolder whitelistHolder) {
         event.setCancelled(true);

         String doorKey = whitelistHolder.getDoorKey();
         int slot = event.getRawSlot();
         Player player = (Player) event.getWhoClicked();
         String guiTitle = event.getView().getTitle();

         // Wenn es ein "Spieler Auswählen" GUI ist
         if (guiTitle.contains("👥")) {
             handlePlayerSelectionGui(event, doorKey, slot, player);
             return;
         }

         // Slot 48: Spieler hinzufügen
         if (slot == 48) {
             doorLockGuiService.openPlayerSelectionGui(player, doorKey);
             return;
         }

         // Slot 49: Zurück
         if (slot == 49) {
             // Öffne das Hauptmenü neu
             org.bukkit.Location location = getLocationFromKey(doorKey);
             if (location != null) {
                 doorLockGuiService.openDoorLockGui(player, location);
             } else {
                 player.closeInventory();
             }
             return;
         }

         // Klick auf Whitelist-Eintrag zum Entfernen (Slots 0-47)
         if (slot < 48) {
             if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) {
                 return;
             }

             String playerName = event.getCurrentItem().getItemMeta().getDisplayName()
                     .replace("§e", "").replace("§f", "").replace("§6", "").replace("§7", "");

             java.util.Map<UUID, String> whitelist = doorLockService.getWhitelistWithNames(doorKey);

             for (java.util.Map.Entry<UUID, String> entry : whitelist.entrySet()) {
                 if (entry.getValue().equals(playerName)) {
                     doorLockService.removeFromWhitelistByKey(doorKey, entry.getKey());
                     player.sendMessage("§a✓ Spieler " + playerName + " von Whitelist entfernt!");
                     doorLockGuiService.openWhitelistGui(player, doorKey);
                     return;
                 }
             }
         }
     }

     private void handlePlayerSelectionGui(InventoryClickEvent event, String doorKey, int slot, Player player) {
         // Slot 49: Zurück
         if (slot == 49) {
             doorLockGuiService.openWhitelistGui(player, doorKey);
             return;
         }

         // Klick auf Spieler zum Hinzufügen
         if (slot < 49) {
             if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) {
                 return;
             }

             String playerName = event.getCurrentItem().getItemMeta().getDisplayName()
                     .replace("§b", "").replace("§f", "").replace("§6", "").replace("§7", "");

             org.bukkit.entity.Player targetPlayer = org.bukkit.Bukkit.getPlayer(playerName);
             if (targetPlayer != null) {
                 doorLockService.addToWhitelistByKey(doorKey, targetPlayer.getUniqueId(), targetPlayer.getName());
                 player.sendMessage("§a✓ Spieler " + playerName + " zur Whitelist hinzugefügt!");
                 doorLockGuiService.openWhitelistGui(player, doorKey);
             }
         }
     }

    private org.bukkit.Location getLocationFromKey(String doorKey) {
        try {
            String[] parts = doorKey.split("_");
            if (parts.length != 4) return null;

            String worldName = parts[0];
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);

            org.bukkit.World world = org.bukkit.Bukkit.getWorld(worldName);
            if (world == null) return null;

            return new org.bukkit.Location(world, x, y, z);
        } catch (Exception e) {
            return null;
        }
    }
}

