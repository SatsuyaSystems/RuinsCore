package de.satsuya.ruinsCore.core.auction.gui;

import de.satsuya.ruinsCore.core.auction.AuctionItem;
import de.satsuya.ruinsCore.core.auction.AuctionService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Service zur Verwaltung von Auktions-GUIs
 */
public final class AuctionGuiService {

    private final AuctionService auctionService;

    public AuctionGuiService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * Öffne das Auktions-Übersicht-GUI
     */
    public void openAuctionOverviewGui(Player player, int page) {
        List<AuctionItem> auctions = auctionService.getAllAuctions();
        
        int itemsPerPage = 45;
        int maxPages = (auctions.size() + itemsPerPage - 1) / itemsPerPage;
        
        if (page < 1 || page > maxPages) {
            page = 1;
        }
        
        Inventory inventory = Bukkit.createInventory(
            new AuctionOverviewGuiHolder(page, maxPages),
            54,
            Component.text("§5Auktionen - Seite " + page + "/" + maxPages, NamedTextColor.LIGHT_PURPLE)
        );
        
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, auctions.size());
        
        int slot = 0;
        for (int i = startIndex; i < endIndex && slot < 45; i++) {
            AuctionItem auction = auctions.get(i);
            
            try {
                ItemStack item = auctionService.stringToItem(auction.getItemData());
                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    meta = Bukkit.getItemFactory().getItemMeta(item.getType());
                }
                
                List<String> lore = new ArrayList<>();
                lore.add("§7───────────────────");
                lore.add("§6Verkäufer: §b" + auction.getSellerName());
                lore.add("§6Preis: §b" + String.format("%.2f", auction.getPrice()) + "€");
                lore.add("§7───────────────────");
                lore.add("§eKlicke zum Kaufen!");
                meta.setLore(lore);
                item.setItemMeta(meta);
                
                inventory.setItem(slot, item);
            } catch (Exception e) {
                ItemStack placeholder = new ItemStack(Material.BARRIER);
                inventory.setItem(slot, placeholder);
            }
            
            slot++;
        }
        
        // Füge Navigation hinzu
        if (page > 1) {
            ItemStack backButton = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backButton.getItemMeta();
            backMeta.setDisplayName("§c← Zurück");
            backButton.setItemMeta(backMeta);
            inventory.setItem(45, backButton);
        }
        
        if (page < maxPages) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            nextMeta.setDisplayName("§aWeiter →");
            nextButton.setItemMeta(nextMeta);
            inventory.setItem(53, nextButton);
        }
        
        // Erstelle Auktion Button
        ItemStack createButton = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName("§a+ Neue Auktion");
        createButton.setItemMeta(createMeta);
        inventory.setItem(49, createButton);
        
        player.openInventory(inventory);
    }

    /**
     * Öffne das Auktions-Erstellungs-GUI
     */
    public void openAuctionCreateGui(Player player) {
        Inventory inventory = Bukkit.createInventory(
            new AuctionCreateGuiHolder(),
            27,
            Component.text("§5Neue Auktion erstellen", NamedTextColor.LIGHT_PURPLE)
        );
        
        // Item-Slot (Mitte)
        ItemStack itemSlot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemSlot.getItemMeta();
        itemMeta.setDisplayName("§6Item hier platzieren");
        itemSlot.setItemMeta(itemMeta);
        inventory.setItem(13, itemSlot);
        
        // Bestätigung Button
        ItemStack confirmButton = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName("§a✓ Auktion erstellen");
        confirmButton.setItemMeta(confirmMeta);
        inventory.setItem(24, confirmButton);
        
        // Abbrechen Button
        ItemStack cancelButton = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName("§c✗ Abbrechen");
        cancelButton.setItemMeta(cancelMeta);
        inventory.setItem(20, cancelButton);
        
        player.openInventory(inventory);
    }

    /**
     * Öffne das Auktions-Preis-GUI
     */
    public void openAuctionPriceGui(Player player, ItemStack item) {
        Inventory inventory = Bukkit.createInventory(
            new AuctionPriceGuiHolder(item, 0.0),
            36,
            Component.text("§5Auktions-Preis festlegen", NamedTextColor.LIGHT_PURPLE)
        );
        
        // Item-Vorschau (Slot 13)
        ItemStack preview = item.clone();
        preview.setAmount(1);
        inventory.setItem(13, preview);
        
        // Preis-Display (Slot 31)
        ItemStack priceDisplay = new ItemStack(Material.PAPER);
        ItemMeta priceMeta = priceDisplay.getItemMeta();
        priceMeta.setDisplayName("§6Aktueller Preis:");
        ArrayList<String> priceLore = new ArrayList<>();
        priceLore.add("§b0.00€");
        priceMeta.setLore(priceLore);
        priceDisplay.setItemMeta(priceMeta);
        inventory.setItem(31, priceDisplay);
        
        // Minus-Buttons (Reihe 1)
        // -10
        ItemStack minus10 = new ItemStack(Material.RED_CONCRETE);
        ItemMeta minus10Meta = minus10.getItemMeta();
        minus10Meta.setDisplayName("§c-10€");
        minus10.setItemMeta(minus10Meta);
        inventory.setItem(2, minus10);
        
        // -5
        ItemStack minus5 = new ItemStack(Material.RED_CONCRETE);
        ItemMeta minus5Meta = minus5.getItemMeta();
        minus5Meta.setDisplayName("§6-5€");
        minus5.setItemMeta(minus5Meta);
        inventory.setItem(3, minus5);
        
        // -1
        ItemStack minus1 = new ItemStack(Material.RED_CONCRETE);
        ItemMeta minus1Meta = minus1.getItemMeta();
        minus1Meta.setDisplayName("§e-1€");
        minus1.setItemMeta(minus1Meta);
        inventory.setItem(4, minus1);
        
        // Plus-Buttons (Reihe 1)
        // +1
        ItemStack plus1 = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta plus1Meta = plus1.getItemMeta();
        plus1Meta.setDisplayName("§a+1€");
        plus1.setItemMeta(plus1Meta);
        inventory.setItem(5, plus1);
        
        // +5
        ItemStack plus5 = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta plus5Meta = plus5.getItemMeta();
        plus5Meta.setDisplayName("§2+5€");
        plus5.setItemMeta(plus5Meta);
        inventory.setItem(6, plus5);
        
        // +10
        ItemStack plus10 = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta plus10Meta = plus10.getItemMeta();
        plus10Meta.setDisplayName("§a+10€");
        plus10.setItemMeta(plus10Meta);
        inventory.setItem(7, plus10);
        
        // Bestätigung Button
        ItemStack confirmButton = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName("§a✓ Auktion erstellen");
        confirmButton.setItemMeta(confirmMeta);
        inventory.setItem(33, confirmButton);
        
        // Abbrechen Button
        ItemStack cancelButton = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName("§c✗ Abbrechen");
        cancelButton.setItemMeta(cancelMeta);
        inventory.setItem(34, cancelButton);
        
        player.openInventory(inventory);
    }
}

