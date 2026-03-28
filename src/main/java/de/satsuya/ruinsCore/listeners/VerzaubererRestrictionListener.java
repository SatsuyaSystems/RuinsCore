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
import org.bukkit.inventory.ItemStack;

public final class VerzaubererRestrictionListener implements Listener {

    private final JobService jobService;
    private final String denyEnchantingTableMessage;
    private final String denyAnvilMessage;

    public VerzaubererRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.denyEnchantingTableMessage = plugin.getConfig().getString(
                "jobs.verzauberer.restrictions.deny-enchanting-table-message",
                "§cNur Verzauberer dürfen den Zaubertisch benutzen."
        );
        this.denyAnvilMessage = plugin.getConfig().getString(
                "jobs.verzauberer.restrictions.deny-anvil-message",
                "§cNur Verzauberer dürfen den Amboss benutzen."
        );
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        Material blockType = block.getType();

        // Zaubertisch Restriktion
        if (blockType == Material.ENCHANTING_TABLE) {
            if (!jobService.hasJob(player.getUniqueId(), JobType.VERZAUBERER)) {
                event.setCancelled(true);
                player.sendMessage(denyEnchantingTableMessage);
            }
            return;
        }

        // Amboss Restriktion
        if (blockType == Material.ANVIL || blockType == Material.CHIPPED_ANVIL || blockType == Material.DAMAGED_ANVIL) {
            if (!jobService.hasJob(player.getUniqueId(), JobType.VERZAUBERER)) {
                event.setCancelled(true);
                player.sendMessage(denyAnvilMessage);
            }
        }
    }
}

