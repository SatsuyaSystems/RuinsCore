package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import de.satsuya.ruinsCore.core.wache.WacheRestrainManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public final class WacheReleaseListener implements Listener {

    private final JobService jobService;
    private final WacheRestrainManager restrainManager;
    private final String releasedMessage;
    private final String wacheReleasedMessage;

    public WacheReleaseListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.restrainManager = plugin.getWacheRestrainManager();
        this.releasedMessage = plugin.getConfig().getString(
                "jobs.wache.messages.released-message",
                "§aDu bist freigelassen."
        );
        this.wacheReleasedMessage = plugin.getConfig().getString(
                "jobs.wache.messages.wache-released",
                "§a{spieler} ist freigelassen."
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target)) {
            return;
        }

        Player wache = event.getPlayer();

        if (!jobService.hasJob(wache.getUniqueId(), JobType.WACHE)) {
            return;
        }

        if (wache.getInventory().getItemInMainHand().getType() != Material.STICK) {
            return;
        }

        if (!wache.isSneaking()) {
            return;
        }

        if (!restrainManager.isRestrained(target.getUniqueId())) {
            return;
        }

        releasePlayer(wache, target);
        event.setCancelled(true);
    }

    private void releasePlayer(Player wache, Player target) {
        restrainManager.release(target.getUniqueId());
        target.setFreezeTicks(0);

        target.sendMessage(releasedMessage);

        String message = wacheReleasedMessage.replace("{spieler}", target.getName());
        wache.sendMessage(message);
    }
}


