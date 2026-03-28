package de.satsuya.ruinsCore.core.jobs.gui;

import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public final class JobOverviewGuiHolder implements InventoryHolder {

    private final Map<Integer, JobType> slotJobs = new HashMap<>();

    public void setJob(int slot, JobType jobType) {
        slotJobs.put(slot, jobType);
    }

    public JobType getJob(int slot) {
        return slotJobs.get(slot);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}

