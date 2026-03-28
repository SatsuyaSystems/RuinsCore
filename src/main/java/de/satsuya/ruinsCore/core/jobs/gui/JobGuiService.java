package de.satsuya.ruinsCore.core.jobs.gui;

import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class JobGuiService {

    private static final int GUI_SIZE = 54;
    private static final int CONTENT_MAX_SLOT = 45;

    private final JobService jobService;

    public JobGuiService(JobService jobService) {
        this.jobService = jobService;
    }

    public void openJobOverviewGui(Player viewer, Collection<JobType> allowedJobs) {
        JobOverviewGuiHolder holder = new JobOverviewGuiHolder();
        Inventory inventory = Bukkit.createInventory(holder, 27, "Jobs - Auswahl");

        int slot = 10;
        for (JobType jobType : allowedJobs) {
            inventory.setItem(slot, createJobItem(jobType));
            holder.setJob(slot, jobType);
            slot += 2;

            if (slot >= 26) {
                break;
            }
        }

        viewer.openInventory(inventory);
    }

    public void openMembersControlGui(Player viewer, JobType jobType) {
        JobMembersGuiHolder holder = new JobMembersGuiHolder(jobType);
        Inventory inventory = Bukkit.createInventory(holder, GUI_SIZE, "Job: " + jobType.getDisplayName() + " - Mitglieder");

        List<OfflinePlayer> members = new ArrayList<>(jobService.getMembers(jobType));
        members.sort(Comparator.comparing(this::safeName, String.CASE_INSENSITIVE_ORDER));

        int slot = 0;
        for (OfflinePlayer target : members) {
            if (slot >= CONTENT_MAX_SLOT) {
                break;
            }

            Optional<JobType> currentJob = jobService.getJob(target.getUniqueId());
            inventory.setItem(slot, createPlayerItem(target, currentJob, null));
            holder.setTarget(slot, target.getUniqueId());
            slot++;
        }

        inventory.setItem(JobMembersGuiHolder.SLOT_BACK, createSimpleItem(Material.ARROW, "Zurück zur Jobauswahl", List.of()));
        inventory.setItem(JobMembersGuiHolder.SLOT_ADD_USER,
                createSimpleItem(Material.LIME_WOOL, "User hinzufügen", List.of("Öffnet die Spielerauswahl")));
        inventory.setItem(JobMembersGuiHolder.SLOT_REMOVE_USER,
                createSimpleItem(Material.RED_WOOL, "User entfernen", List.of("Öffnet die Mitgliederauswahl")));

        viewer.openInventory(inventory);
    }

    public void openUserSelectGui(Player viewer, JobType jobType, JobUserSelectMode mode) {
        JobUserSelectGuiHolder holder = new JobUserSelectGuiHolder(jobType, mode);
        String title = mode == JobUserSelectMode.ADD
                ? "Job: " + jobType.getDisplayName() + " - Hinzufuegen"
                : "Job: " + jobType.getDisplayName() + " - Entfernen";
        Inventory inventory = Bukkit.createInventory(holder, GUI_SIZE, title);

        List<OfflinePlayer> targets = mode == JobUserSelectMode.ADD
                ? getAddTargets(jobType)
                : new ArrayList<>(jobService.getMembers(jobType));
        targets.sort(Comparator.comparing(this::safeName, String.CASE_INSENSITIVE_ORDER));

        int slot = 0;
        for (OfflinePlayer target : targets) {
            if (slot >= CONTENT_MAX_SLOT) {
                break;
            }

            Optional<JobType> currentJob = jobService.getJob(target.getUniqueId());
            inventory.setItem(slot, createPlayerItem(target, currentJob, mode));
            holder.setTarget(slot, target.getUniqueId());
            slot++;
        }

        inventory.setItem(JobUserSelectGuiHolder.SLOT_BACK,
                createSimpleItem(Material.ARROW, "Zurück zur Mitgliederliste", List.of()));
        viewer.openInventory(inventory);
    }

    public boolean assignMember(JobType jobType, UUID targetUuid) {
        return jobService.assignJob(targetUuid, jobType);
    }

    public boolean removeMemberJob(UUID targetUuid) {
        return jobService.removeJob(targetUuid);
    }

    private List<OfflinePlayer> getAddTargets(JobType selectedJob) {
        List<OfflinePlayer> targets = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (jobService.hasJob(online.getUniqueId(), selectedJob)) {
                continue;
            }

            targets.add(online);
        }

        return targets;
    }

    private ItemStack createJobItem(JobType jobType) {
        Material icon;
        if (jobType == JobType.HOLZFAELLER) {
            icon = Material.IRON_AXE;
        } else if (jobType == JobType.MINER) {
            icon = Material.IRON_PICKAXE;
        } else if (jobType == JobType.BUILDER) {
            icon = Material.BRICKS;
        } else if (jobType == JobType.FISCHER) {
            icon = Material.FISHING_ROD;
        } else {
            icon = Material.BOOK;
        }

        return createSimpleItem(icon, jobType.getDisplayName(), List.of("Klicken zum Verwalten"));
    }

    private ItemStack createPlayerItem(OfflinePlayer player, Optional<JobType> currentJob, JobUserSelectMode mode) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        if (meta == null) {
            return itemStack;
        }

        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName() == null ? player.getUniqueId().toString() : player.getName());

        List<String> lore = new ArrayList<>();
        String current = currentJob.map(JobType::getDisplayName).orElse("Kein Job");
        lore.add("Aktueller Job: " + current);

        if (mode == JobUserSelectMode.ADD) {
            lore.add("Klick: User zu diesem Job hinzufügen");
        } else if (mode == JobUserSelectMode.REMOVE) {
            lore.add("Klick: User aus diesem Job entfernen");
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private ItemStack createSimpleItem(Material material, String name, List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }

        meta.setDisplayName(name);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private String safeName(OfflinePlayer player) {
        String name = player.getName();
        return name == null || name.isBlank() ? player.getUniqueId().toString() : name;
    }
}


