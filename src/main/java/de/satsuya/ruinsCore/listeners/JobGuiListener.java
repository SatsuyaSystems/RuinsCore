package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobHealthService;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import de.satsuya.ruinsCore.core.jobs.gui.JobGuiService;
import de.satsuya.ruinsCore.core.jobs.gui.JobMembersGuiHolder;
import de.satsuya.ruinsCore.core.jobs.gui.JobUserSelectGuiHolder;
import de.satsuya.ruinsCore.core.jobs.gui.JobUserSelectMode;
import de.satsuya.ruinsCore.core.permission.PermissionManager;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class JobGuiListener implements Listener {

    private final PermissionManager permissionManager;
    private final JobService jobService;
    private final JobGuiService jobGuiService;
    private final JobHealthService jobHealthService;

    public JobGuiListener(RuinsCore plugin) {
        this.permissionManager = plugin.getPermissionManager();
        this.jobService = plugin.getJobService();
        this.jobGuiService = new JobGuiService(plugin.getJobService());
        this.jobHealthService = plugin.getJobHealthService();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        boolean isOverview = event.getView().getTitle().equals("Jobs - Auswahl");
        if (!isOverview
                && !(inventory.getHolder() instanceof JobMembersGuiHolder)
                && !(inventory.getHolder() instanceof JobUserSelectGuiHolder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!isGlobalJobAdmin(player) && jobService.getLeaderJobs(player.getUniqueId()).isEmpty()) {
            player.sendMessage("Dir fehlen Rechte für das Job-GUI.");
            return;
        }

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }

        if (isOverview) {
            handleOverviewClick(player, inventory, slot);
            return;
        }

        if (inventory.getHolder() instanceof JobMembersGuiHolder membersHolder) {
            handleMembersClick(player, membersHolder, slot);
            return;
        }

        if (inventory.getHolder() instanceof JobUserSelectGuiHolder selectHolder) {
            handleUserSelectClick(player, selectHolder, slot);
        }
    }

    private void handleOverviewClick(Player player, Inventory inventory, int slot) {
        ItemStack clicked = inventory.getItem(slot);
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) {
            return;
        }

        String displayName = clicked.getItemMeta().getDisplayName();
        JobType selectedJob = null;
        for (JobType jobType : JobType.values()) {
            if (jobType.getDisplayName().equals(displayName)) {
                selectedJob = jobType;
                break;
            }
        }

        if (selectedJob == null) {
            return;
        }

        if (!canManageJob(player, selectedJob)) {
            player.sendMessage("Du darfst diesen Job nicht verwalten.");
            return;
        }

        jobGuiService.openMembersControlGui(player, selectedJob);
    }

    private void handleMembersClick(Player player, JobMembersGuiHolder holder, int slot) {
        if (!canManageJob(player, holder.getJobType())) {
            player.sendMessage("Du darfst diesen Job nicht verwalten.");
            return;
        }

        if (slot == JobMembersGuiHolder.SLOT_BACK) {
            jobGuiService.openJobOverviewGui(player, getAllowedJobs(player));
            return;
        }

        if (slot == JobMembersGuiHolder.SLOT_ADD_USER) {
            jobGuiService.openUserSelectGui(player, holder.getJobType(), JobUserSelectMode.ADD);
            return;
        }

        if (slot == JobMembersGuiHolder.SLOT_REMOVE_USER) {
            jobGuiService.openUserSelectGui(player, holder.getJobType(), JobUserSelectMode.REMOVE);
        }
    }

    private void handleUserSelectClick(Player player, JobUserSelectGuiHolder holder, int slot) {
        if (!canManageJob(player, holder.getJobType())) {
            player.sendMessage("Du darfst diesen Job nicht verwalten.");
            return;
        }

        if (slot == JobUserSelectGuiHolder.SLOT_BACK) {
            jobGuiService.openMembersControlGui(player, holder.getJobType());
            return;
        }

        UUID targetUuid = holder.getTarget(slot);
        if (targetUuid == null) {
            return;
        }

        boolean success;
        if (holder.getMode() == JobUserSelectMode.ADD) {
            success = jobGuiService.assignMember(holder.getJobType(), targetUuid);
        } else {
            success = jobGuiService.removeMemberJob(targetUuid);
        }

        if (!success) {
            player.sendMessage("Aktion fehlgeschlagen.");
            return;
        }

        jobHealthService.syncOnline(targetUuid);

        jobGuiService.openMembersControlGui(player, holder.getJobType());

        Player target = Bukkit.getPlayer(targetUuid);
        if (target != null) {
            player.sendMessage("Mitglied aktualisiert: " + target.getName());
        } else {
            player.sendMessage("Mitglied aktualisiert.");
        }
    }

    private boolean canManageJob(Player player, JobType jobType) {
        return isGlobalJobAdmin(player) || jobService.isLeader(player.getUniqueId(), jobType);
    }

    private boolean isGlobalJobAdmin(Player player) {
        return permissionManager.has(player, PermissionNode.JOB_GUI)
                || permissionManager.has(player, PermissionNode.JOB_MANAGE);
    }

    private java.util.Set<JobType> getAllowedJobs(Player player) {
        if (isGlobalJobAdmin(player)) {
            return java.util.EnumSet.allOf(JobType.class);
        }

        return jobService.getLeaderJobs(player.getUniqueId());
    }
}

