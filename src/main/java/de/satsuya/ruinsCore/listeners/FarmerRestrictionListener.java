package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public final class FarmerRestrictionListener implements Listener {

    private static final Set<Material> PLANTABLE_FOOD_BLOCKS = EnumSet.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.PUMPKIN_STEM,
            Material.ATTACHED_PUMPKIN_STEM,
            Material.MELON_STEM,
            Material.ATTACHED_MELON_STEM,
            Material.COCOA,
            Material.SWEET_BERRY_BUSH
    );

    private static final Set<Material> HARVESTABLE_FOOD_BLOCKS = EnumSet.of(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.PUMPKIN,
            Material.MELON,
            Material.COCOA,
            Material.SWEET_BERRY_BUSH,
            Material.SUGAR_CANE,
            Material.KELP,
            Material.KELP_PLANT
    );

    private final JobService jobService;
    private final String denyPlantMessage;
    private final String denyHarvestMessage;
    private final String denyCraftMessage;

    public FarmerRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.denyPlantMessage = plugin.getConfig().getString(
                "jobs.bauer.restrictions.deny-plant-message",
                "§cNur Bauer dürfen Nahrungspflanzen anbauen."
        );
        this.denyHarvestMessage = plugin.getConfig().getString(
                "jobs.bauer.restrictions.deny-harvest-message",
                "§cNur Bauer dürfen Nahrungspflanzen abbauen."
        );
        this.denyCraftMessage = plugin.getConfig().getString(
                "jobs.bauer.restrictions.deny-craft-message",
                "§cNur Bauer dürfen Essen craften."
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!PLANTABLE_FOOD_BLOCKS.contains(event.getBlockPlaced().getType())) {
            return;
        }

        Player player = event.getPlayer();
        if (isFarmer(player)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyPlantMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!HARVESTABLE_FOOD_BLOCKS.contains(event.getBlock().getType())) {
            return;
        }

        Player player = event.getPlayer();
        if (isFarmer(player)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyHarvestMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (isFarmer(player)) {
            return;
        }

        ItemStack result = event.getInventory().getResult();
        if (result == null || !result.getType().isEdible()) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyCraftMessage);
    }

    private boolean isFarmer(Player player) {
        return jobService.hasJob(player.getUniqueId(), JobType.BAUER) || player.isOp();
    }
}



