package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public final class BannerCraftRestrictionListener implements Listener {

    private static final Set<Material> BANNER_ITEMS = EnumSet.of(
            Material.WHITE_BANNER,
            Material.ORANGE_BANNER,
            Material.MAGENTA_BANNER,
            Material.LIGHT_BLUE_BANNER,
            Material.YELLOW_BANNER,
            Material.LIME_BANNER,
            Material.PINK_BANNER,
            Material.GRAY_BANNER,
            Material.LIGHT_GRAY_BANNER,
            Material.CYAN_BANNER,
            Material.PURPLE_BANNER,
            Material.BLUE_BANNER,
            Material.BROWN_BANNER,
            Material.GREEN_BANNER,
            Material.RED_BANNER,
            Material.BLACK_BANNER
    );

    private final JobService jobService;
    private final String denyCraftMessage;

    public BannerCraftRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.denyCraftMessage = plugin.getConfig().getString(
                "jobs.bannerrist.restrictions.deny-craft-message",
                "§cNur Bannerristen dürfen Banner craften."
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (jobService.hasJob(player.getUniqueId(), JobType.BANNERRIST) || player.isOp()) {
            return;
        }

        ItemStack result = event.getRecipe().getResult();
        if (!BANNER_ITEMS.contains(result.getType())) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyCraftMessage);
    }
}

