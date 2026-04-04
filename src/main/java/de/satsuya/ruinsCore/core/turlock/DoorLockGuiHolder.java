package de.satsuya.ruinsCore.core.turlock;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * InventoryHolder für Türschloss-GUI
 */
public final class DoorLockGuiHolder implements InventoryHolder {

    private final String doorKey;

    public DoorLockGuiHolder(String doorKey) {
        this.doorKey = doorKey;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public String getDoorKey() {
        return doorKey;
    }
}

