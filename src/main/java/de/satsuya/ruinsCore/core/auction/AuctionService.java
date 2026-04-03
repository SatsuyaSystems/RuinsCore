package de.satsuya.ruinsCore.core.auction;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service zur Verwaltung von Auktionen
 */
public final class AuctionService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;
    private static final long AUCTION_DURATION = 7 * 24 * 60 * 60 * 1000; // 7 Tage

    public AuctionService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
    }

    /**
     * Erstelle eine neue Auktion
     */
    public boolean createAuction(UUID seller, String sellerName, ItemStack item, double price) {
        try {
            String itemData = itemToString(item);
            long now = System.currentTimeMillis();
            long expiresAt = now + AUCTION_DURATION;

            String sql = "INSERT INTO auctions (seller_uuid, seller_name, item_data, price, created_at, expires_at) VALUES (?, ?, ?, ?, ?, ?)";
            
            databaseManager.executeUpdate(sql, 
                seller.toString(),
                sellerName,
                itemData,
                price,
                now,
                expiresAt
            );
            
            return true;
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Erstellen einer Auktion", e);
            return false;
        }
    }

    /**
     * Gebe alle aktiven Auktionen zurück
     */
    public List<AuctionItem> getAllAuctions() {
        List<AuctionItem> auctions = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM auctions WHERE expires_at > ? ORDER BY created_at DESC";
            ResultSet result = databaseManager.executeQuery(sql, System.currentTimeMillis());
            
            while (result != null && result.next()) {
                auctions.add(new AuctionItem(
                    result.getInt("id"),
                    UUID.fromString(result.getString("seller_uuid")),
                    result.getString("seller_name"),
                    result.getString("item_data"),
                    result.getDouble("price"),
                    result.getLong("created_at"),
                    result.getLong("expires_at")
                ));
            }
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Abrufen der Auktionen", e);
        }
        
        return auctions;
    }

    /**
     * Gebe eine spezifische Auktion zurück
     */
    public AuctionItem getAuction(int id) {
        try {
            String sql = "SELECT * FROM auctions WHERE id = ?";
            ResultSet result = databaseManager.executeQuery(sql, id);
            
            if (result != null && result.next()) {
                return new AuctionItem(
                    result.getInt("id"),
                    UUID.fromString(result.getString("seller_uuid")),
                    result.getString("seller_name"),
                    result.getString("item_data"),
                    result.getDouble("price"),
                    result.getLong("created_at"),
                    result.getLong("expires_at")
                );
            }
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Abrufen der Auktion", e);
        }
        
        return null;
    }

    /**
     * Lösche eine Auktion
     */
    public boolean deleteAuction(int id) {
        try {
            String sql = "DELETE FROM auctions WHERE id = ?";
            databaseManager.executeUpdate(sql, id);
            return true;
        } catch (Exception e) {
            loggerUtil.severe("Fehler beim Löschen der Auktion", e);
            return false;
        }
    }

    /**
     * Konvertiere ItemStack zu String
     */
    private String itemToString(ItemStack item) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeObject(item);
        dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    /**
     * Konvertiere String zu ItemStack
     */
    public ItemStack stringToItem(String data) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        ItemStack item = (ItemStack) dataInput.readObject();
        dataInput.close();
        return item;
    }
}

