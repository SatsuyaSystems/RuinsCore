package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public final class BannerLoomRestrictionListener implements Listener {

    private final JobService jobService;
    private final String denyEditMessage;

    public BannerLoomRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.denyEditMessage = plugin.getConfig().getString(
                "jobs.bannerrist.restrictions.deny-edit-message",
                "§cNur Bannerristen dürfen Banner bearbeiten."
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoomClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getView().getTopInventory().getType() != InventoryType.LOOM) {
            return;
        }

        if (jobService.hasJob(player.getUniqueId(), JobType.BANNERRIST)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyEditMessage);
    }
}

