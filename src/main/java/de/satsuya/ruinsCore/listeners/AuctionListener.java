package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.auction.AuctionItem;
import de.satsuya.ruinsCore.core.auction.AuctionService;
import de.satsuya.ruinsCore.core.auction.gui.AuctionCreateGuiHolder;
import de.satsuya.ruinsCore.core.auction.gui.AuctionGuiService;
import de.satsuya.ruinsCore.core.auction.gui.AuctionOverviewGuiHolder;
import de.satsuya.ruinsCore.core.auction.gui.AuctionPriceGuiHolder;
import de.satsuya.ruinsCore.core.economy.EconomyService;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener für Auktions-GUI Interaktionen
 */
public final class AuctionListener implements Listener {

    private final AuctionService auctionService;
    private final AuctionGuiService auctionGuiService;
    private final EconomyService economyService;
    private final LoggerUtil loggerUtil;
    private final Map<UUID, ItemStack> selectedAuctionItems = new HashMap<>();

    public AuctionListener(RuinsCore plugin) {
        this.auctionService = plugin.getAuctionService();
        this.auctionGuiService = plugin.getAuctionGuiService();
        this.economyService = plugin.getEconomyService();
        this.loggerUtil = plugin.getLoggerUtil();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (inventory.getHolder() instanceof AuctionOverviewGuiHolder holder) {
            handleAuctionOverviewClick(player, holder, event);
            return;
        }

        if (inventory.getHolder() instanceof AuctionCreateGuiHolder) {
            handleAuctionCreateClick(player, event);
            return;
        }

        if (inventory.getHolder() instanceof AuctionPriceGuiHolder holder) {
            handleAuctionPriceClick(player, holder, event);
        }
    }

    /**
     * Bearbeite Klicks im Auktions-Übersicht-GUI
     */
    private void handleAuctionOverviewClick(Player player, AuctionOverviewGuiHolder holder, InventoryClickEvent event) {
        event.setCancelled(true);

        int slot = event.getRawSlot();

        // Navigation - Zurück
        if (slot == 45) {
            auctionGuiService.openAuctionOverviewGui(player, holder.getCurrentPage() - 1);
            return;
        }

        // Navigation - Weiter
        if (slot == 53) {
            auctionGuiService.openAuctionOverviewGui(player, holder.getCurrentPage() + 1);
            return;
        }

        // Neue Auktion erstellen
        if (slot == 49) {
            auctionGuiService.openAuctionCreateGui(player);
            return;
        }

        // Auktion kaufen (Slots 0-44)
        if (slot < 45) {
            int auctionIndex = (holder.getCurrentPage() - 1) * 45 + slot;
            java.util.List<AuctionItem> auctions = auctionService.getAllAuctions();

            if (auctionIndex >= auctions.size()) {
                player.sendMessage("§cDiese Auktion existiert nicht mehr.");
                return;
            }

            AuctionItem auction = auctions.get(auctionIndex);

            // Eigenes Auktions-Item - zurück ins Inventar
            if (auction.getSeller().equals(player.getUniqueId())) {
                try {
                    ItemStack item = auctionService.stringToItem(auction.getItemData());
                    player.getInventory().addItem(item);
                    auctionService.deleteAuction(auction.getId());
                    
                    player.sendMessage("§aAuktion beendet! §b" + item.getAmount() + "x " + 
                        item.getType().name() + " §awurde zurück ins Inventar gelegt.");
                    
                    // Überblick aktualisieren
                    auctionGuiService.openAuctionOverviewGui(player, holder.getCurrentPage());
                } catch (Exception e) {
                    loggerUtil.severe("Fehler beim Entfernen der Auktion", e);
                    player.sendMessage("§cFehler beim Beenden der Auktion!");
                }
                return;
            }

            // Geld prüfen
            if (economyService.getBalance(player.getUniqueId()) < auction.getPrice()) {
                player.sendMessage("§cDu hast nicht genug Geld! Du brauchst §b" +
                        String.format("%.2f", auction.getPrice()) + "€");
                return;
            }

            // Item konvertieren
            try {
                ItemStack item = auctionService.stringToItem(auction.getItemData());

                // Geld vom Käufer abziehen
                economyService.removeBalance(player.getUniqueId(), auction.getPrice());

                // Geld zum Verkäufer hinzufügen
                economyService.addBalance(auction.getSeller(), auction.getPrice());

                // Item zum Spieler hinzufügen
                player.getInventory().addItem(item);

                // Auktion löschen
                auctionService.deleteAuction(auction.getId());

                player.sendMessage("§aAuktion gekauft! §b" + item.getAmount() + "x " +
                        item.getType().name() + " §afür §b" + String.format("%.2f", auction.getPrice()) + "€");

                // Verkäufer benachrichtigen
                Player seller = Bukkit.getPlayer(auction.getSeller());
                if (seller != null && seller.isOnline()) {
                    seller.sendMessage("§a" + player.getName() + " hat dein Auktions-Item gekauft!");
                }

                // Überblick aktualisieren
                auctionGuiService.openAuctionOverviewGui(player, holder.getCurrentPage());
            } catch (Exception e) {
                loggerUtil.severe("Fehler beim Kaufen der Auktion", e);
                player.sendMessage("§cFehler beim Kaufen des Items.");
            }
        }
    }

    /**
     * Bearbeite Klicks im Auktions-Erstellungs-GUI
     */
    private void handleAuctionCreateClick(Player player, InventoryClickEvent event) {
        event.setCancelled(true);
        
        int slot = event.getRawSlot();
        int size = event.getInventory().getSize();
        Inventory guiInventory = event.getInventory();

        // Spieler-Inventar Klicks
        if (slot >= size) {
            ItemStack clickedItem = event.getCurrentItem();
            
            // Nur wenn ein echtes Item geklickt wurde
            if (clickedItem != null && !clickedItem.getType().isAir()) {
                // Dupliziere das Item und lege es in Slot 13
                ItemStack duplicate = clickedItem.clone();
                guiInventory.setItem(13, duplicate);
                
                // Speichere das Item für später
                selectedAuctionItems.put(player.getUniqueId(), clickedItem.clone());
                
                player.sendMessage("§6Item ausgewählt! Klicke auf §a✓ Auktion erstellen §6um fortzufahren.");
            }
            return;
        }

        // Item-Slot (13) - nicht anklickbar
        if (slot == 13) {
            event.setCancelled(true);
            return;
        }

        // Bestätigung
        if (slot == 24) {
            ItemStack item = guiInventory.getItem(13);

            if (item == null || item.getType().isAir() || item.getType().toString().contains("GLASS")) {
                player.sendMessage("§cBitte wähle ein Item aus!");
                return;
            }

            // Öffne Preis-GUI
            auctionGuiService.openAuctionPriceGui(player, item);
            return;
        }

        // Abbrechen
        if (slot == 20) {
            selectedAuctionItems.remove(player.getUniqueId());
            auctionGuiService.openAuctionOverviewGui(player, 1);
        }
    }

    /**
     * Bearbeite Klicks im Auktions-Preis-GUI
     */
    private void handleAuctionPriceClick(Player player, AuctionPriceGuiHolder holder, InventoryClickEvent event) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        double currentPrice = holder.getCurrentPrice();

        // -10
        if (slot == 2) {
            currentPrice = Math.max(0, currentPrice - 10);
            updatePriceDisplay(event.getInventory(), currentPrice);
            holder.setCurrentPrice(currentPrice);
            return;
        }

        // -5
        if (slot == 3) {
            currentPrice = Math.max(0, currentPrice - 5);
            updatePriceDisplay(event.getInventory(), currentPrice);
            holder.setCurrentPrice(currentPrice);
            return;
        }

        // -1
        if (slot == 4) {
            currentPrice = Math.max(0, currentPrice - 1);
            updatePriceDisplay(event.getInventory(), currentPrice);
            holder.setCurrentPrice(currentPrice);
            return;
        }

        // +1
        if (slot == 5) {
            currentPrice += 1;
            updatePriceDisplay(event.getInventory(), currentPrice);
            holder.setCurrentPrice(currentPrice);
            return;
        }

        // +5
        if (slot == 6) {
            currentPrice += 5;
            updatePriceDisplay(event.getInventory(), currentPrice);
            holder.setCurrentPrice(currentPrice);
            return;
        }

        // +10
        if (slot == 7) {
            currentPrice += 10;
            updatePriceDisplay(event.getInventory(), currentPrice);
            holder.setCurrentPrice(currentPrice);
            return;
        }

        // Bestätigung
        if (slot == 33) {
            if (currentPrice <= 0) {
                player.sendMessage("§cDer Preis muss größer als 0€ sein!");
                return;
            }

            ItemStack item = holder.getItemToAuction();

            // Auktion erstellen
            if (auctionService.createAuction(player.getUniqueId(), player.getName(), item, currentPrice)) {
                // Entferne das Item aus dem Spieler-Inventar
                ItemStack selectedItem = selectedAuctionItems.get(player.getUniqueId());
                if (selectedItem != null) {
                    player.getInventory().removeItem(selectedItem);
                    selectedAuctionItems.remove(player.getUniqueId());
                }
                
                player.sendMessage("§aAuktion erstellt! §b" + item.getAmount() + "x " +
                        item.getType().name() + " §afür §b" + String.format("%.2f", currentPrice) + "€");
                auctionGuiService.openAuctionOverviewGui(player, 1);
            } else {
                player.sendMessage("§cFehler beim Erstellen der Auktion!");
            }
            return;
        }

        // Abbrechen
        if (slot == 34) {
            selectedAuctionItems.remove(player.getUniqueId());
            auctionGuiService.openAuctionCreateGui(player);
        }
    }

    /**
     * Aktualisiere das Preis-Display im GUI
     */
    private void updatePriceDisplay(Inventory inventory, double price) {
        ItemStack priceDisplay = new ItemStack(Material.PAPER);
        ItemMeta priceMeta = priceDisplay.getItemMeta();
        priceMeta.setDisplayName("§6Aktueller Preis:");
        java.util.ArrayList<String> priceLore = new java.util.ArrayList<>();
        priceLore.add("§b" + String.format("%.2f", price) + "€");
        priceMeta.setLore(priceLore);
        priceDisplay.setItemMeta(priceMeta);
        inventory.setItem(31, priceDisplay);
    }

}