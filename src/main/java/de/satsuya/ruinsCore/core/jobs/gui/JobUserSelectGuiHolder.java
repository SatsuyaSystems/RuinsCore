package de.satsuya.ruinsCore.core.jobs.gui;

import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class JobUserSelectGuiHolder implements InventoryHolder {

    public static final int SLOT_BACK = 45;

    private final JobType jobType;
    private final JobUserSelectMode mode;
    private final Map<Integer, UUID> slotTargets = new HashMap<>();

    public JobUserSelectGuiHolder(JobType jobType, JobUserSelectMode mode) {
        this.jobType = jobType;
        this.mode = mode;
    }

    public JobType getJobType() {
        return jobType;
    }

    public JobUserSelectMode getMode() {
        return mode;
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

