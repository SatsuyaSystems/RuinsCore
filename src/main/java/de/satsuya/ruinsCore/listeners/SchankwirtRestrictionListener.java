package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class SchankwirtRestrictionListener implements Listener {

    private final JobService jobService;
    private final String denyMessage;

    public SchankwirtRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.denyMessage = plugin.getConfig().getString(
                "jobs.schankwirt.restrictions.deny-brewing-message",
                "§cNur Schankwirte dürfen den Braustand benutzen."
        );
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.BREWING_STAND) {
            return;
        }

        // Erlauben wenn Schankwirt
        if (jobService.hasJob(player.getUniqueId(), JobType.SCHANKWIRT)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyMessage);
    }
}

