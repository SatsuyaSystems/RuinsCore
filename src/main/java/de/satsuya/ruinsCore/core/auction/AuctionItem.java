package de.satsuya.ruinsCore.core.auction;

import java.util.UUID;

/**
 * Repräsentiert ein Item in einer Auktion
 */
public final class AuctionItem {
    
    private final int id;
    private final UUID seller;
    private final String sellerName;
    private final String itemData;
    private final double price;
    private final long createdAt;
    private final long expiresAt;
    
    public AuctionItem(int id, UUID seller, String sellerName, String itemData, double price, long createdAt, long expiresAt) {
        this.id = id;
        this.seller = seller;
        this.sellerName = sellerName;
        this.itemData = itemData;
        this.price = price;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
    
    public int getId() {
        return id;
    }
    
    public UUID getSeller() {
        return seller;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public String getItemData() {
        return itemData;
    }
    
    public double getPrice() {
        return price;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public long getExpiresAt() {
        return expiresAt;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}

