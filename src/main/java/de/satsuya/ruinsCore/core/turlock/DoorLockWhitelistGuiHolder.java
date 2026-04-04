package de.satsuya.ruinsCore.core.turlock;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * InventoryHolder für Türschloss-Whitelist-GUI
 */
public final class DoorLockWhitelistGuiHolder implements InventoryHolder {

    private final String doorKey;
    private Inventory inventory;

    public DoorLockWhitelistGuiHolder(String doorKey) {
        this.doorKey = doorKey;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getDoorKey() {
        return doorKey;
    }
}


