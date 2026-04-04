package de.satsuya.ruinsCore.core.turlock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * Service für Türschloss-GUI
 */
public final class DoorLockGuiService {

    private final DoorLockService doorLockService;

    public DoorLockGuiService(DoorLockService doorLockService) {
        this.doorLockService = doorLockService;
    }

    /**
     * Öffne das Türschloss-GUI für einen Spieler
     */
    public void openDoorLockGui(Player player, Location doorLocation) {
        String doorKey = getLocationKey(doorLocation);
        Inventory gui = Bukkit.createInventory(new DoorLockGuiHolder(doorKey), 27, "§6🔐 Türschloss");

        DoorLockService.DoorLock lock = doorLockService.getLock(doorLocation);

        // Wenn keine Lock existiert, erstelle eine
        if (lock == null) {
            doorLockService.createLock(doorLocation, player.getUniqueId(), player.getName());
            lock = doorLockService.getLock(doorLocation);
        }

        final DoorLockService.DoorLock finalLock = lock;

        // Slot 11: Öffentlich Button
        ItemStack publicButton = createButton(
            Material.EMERALD,
            finalLock.isPublic ? "§a✓ Öffentlich" : "§c✗ Öffentlich",
            finalLock.isPublic ? "§7Klick zum Privatisieren" : "§7Klick zum Öffentlich Machen"
        );
        gui.setItem(11, publicButton);

        // Slot 13: Info
        ItemStack infoItem = createButton(
            Material.PAPER,
            "§eInfo",
            "§7Besitzer: §f" + finalLock.ownerName,
            "§7Status: " + (finalLock.isPublic ? "§aÖffentlich" : "§cPrivat")
        );
        gui.setItem(13, infoItem);

        // Slot 15: Whitelist Button (nur wenn privat)
        if (!finalLock.isPublic) {
            ItemStack whitelistButton = createButton(
                Material.WRITABLE_BOOK,
                "§b📖 Whitelist",
                "§7Klick zum Verwalten"
            );
            gui.setItem(15, whitelistButton);
        }

        player.openInventory(gui);
    }

     /**
      * Erstelle einen Button Item
      */
     private ItemStack createButton(Material material, String name, String... lore) {
         ItemStack item = new ItemStack(material);
         ItemMeta meta = item.getItemMeta();

         if (meta != null) {
             meta.setDisplayName(name);
             if (lore.length > 0) {
                 meta.setLore(Arrays.asList(lore));
             }
             item.setItemMeta(meta);
         }

         return item;
     }

      /**
       * Öffne das Whitelist-GUI für einen Spieler
       */
      public void openWhitelistGui(Player player, String doorKey) {
          DoorLockWhitelistGuiHolder holder = new DoorLockWhitelistGuiHolder(doorKey);
          Inventory gui = Bukkit.createInventory(holder, 54, "§6📖 Whitelist Verwaltung");
          holder.setInventory(gui);

          // Whitelist laden und anzeigen
          Map<UUID, String> whitelist = doorLockService.getWhitelistWithNames(doorKey);

          int slot = 0;
          for (Map.Entry<UUID, String> entry : whitelist.entrySet()) {
              if (slot >= 48) {
                  break;
              }

              String playerName = entry.getValue();
              ItemStack item = createButton(
                  Material.PLAYER_HEAD,
                  "§e" + playerName,
                  "§7Klick zum Entfernen"
              );
              gui.setItem(slot, item);
              slot++;
          }

          // Slot 48: Spieler hinzufügen Button
          ItemStack addButton = createButton(
              Material.EMERALD_BLOCK,
              "§a➕ Spieler Hinzufügen",
              "§7Klick zum Hinzufügen"
          );
          gui.setItem(48, addButton);

          // Slot 49: Zurück Button
          ItemStack backButton = createButton(Material.BARRIER, "§c← Zurück");
          gui.setItem(49, backButton);

          player.openInventory(gui);
      }

      /**
       * Öffne ein GUI zum Spieler auswählen und zur Whitelist hinzufügen
       */
      public void openPlayerSelectionGui(Player player, String doorKey) {
          DoorLockWhitelistGuiHolder holder = new DoorLockWhitelistGuiHolder(doorKey);
          Inventory gui = Bukkit.createInventory(holder, 54, "§6👥 Spieler Auswählen");
          holder.setInventory(gui);

          int slot = 0;
          for (org.bukkit.entity.Player onlinePlayer : Bukkit.getOnlinePlayers()) {
              if (slot >= 49) {
                  break;
              }

              // Prüfe ob Spieler bereits auf Whitelist ist
              Map<UUID, String> whitelist = doorLockService.getWhitelistWithNames(doorKey);
              if (whitelist.containsKey(onlinePlayer.getUniqueId())) {
                  continue;
              }

              String playerName = onlinePlayer.getName();
              ItemStack item = createButton(
                  Material.PLAYER_HEAD,
                  "§b" + playerName,
                  "§7Klick zum Hinzufügen"
              );
              gui.setItem(slot, item);
              slot++;
          }

          // Slot 49: Zurück Button
          ItemStack backButton = createButton(Material.BARRIER, "§c← Zurück");
          gui.setItem(49, backButton);

          player.openInventory(gui);
      }

      /**
       * Generiere einen eindeutigen Schlüssel für eine Tür
       */
      private String getLocationKey(Location location) {
          return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
      }
  }

