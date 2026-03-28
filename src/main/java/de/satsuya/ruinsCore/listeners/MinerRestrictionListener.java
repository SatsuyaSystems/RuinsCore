package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumSet;
import java.util.Set;

public final class MinerRestrictionListener implements Listener {

    private static final Set<Material> ORE_BLOCKS = EnumSet.of(
            Material.COAL_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.NETHER_GOLD_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS
    );

    private final JobService jobService;

    public MinerRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Material brokenType = event.getBlock().getType();
        if (!ORE_BLOCKS.contains(brokenType)) {
            return;
        }

        Player player = event.getPlayer();
        if (jobService.hasJob(player.getUniqueId(), JobType.MINER)
                || jobService.hasJob(player.getUniqueId(), JobType.BUILDER)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage("§cNur Miner oder Builder dürfen Erze abbauen.");
    }
}

