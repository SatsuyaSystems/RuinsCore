package de.satsuya.ruinsCore.core.jobs.gui;

import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class JobMembersGuiHolder implements InventoryHolder {

    public static final int SLOT_BACK = 45;
    public static final int SLOT_ADD_USER = 49;
    public static final int SLOT_REMOVE_USER = 53;

    private final JobType jobType;
    private final Map<Integer, UUID> slotTargets = new HashMap<>();

    public JobMembersGuiHolder(JobType jobType) {
        this.jobType = jobType;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setTarget(int slot, UUID uuid) {
        slotTargets.put(slot, uuid);
    }

    public UUID getTarget(int slot) {
        return slotTargets.get(slot);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}

