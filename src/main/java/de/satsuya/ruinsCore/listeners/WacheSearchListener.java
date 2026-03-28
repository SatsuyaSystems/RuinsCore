package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import de.satsuya.ruinsCore.core.wache.SearchInventoryHolder;
import de.satsuya.ruinsCore.core.wache.WacheRestrainManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;

public final class WacheSearchListener implements Listener {

    private final JobService jobService;
    private final WacheRestrainManager restrainManager;
    private final String searchTitle;
    private final String targetSearchedMessage;
    private final String wacheRestrainedMessage;

    public WacheSearchListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.restrainManager = plugin.getWacheRestrainManager();
        this.searchTitle = plugin.getConfig().getString(
                "jobs.wache.messages.search-title",
                "Durchsuchung"
        );
        this.targetSearchedMessage = plugin.getConfig().getString(
                "jobs.wache.messages.target-searched",
                "§7Du wirst durchsucht von {wache}."
        );
        this.wacheRestrainedMessage = plugin.getConfig().getString(
                "jobs.wache.messages.wache-restrained",
                "§a{spieler} ist jetzt festgehalten."
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

        if (wache.isSneaking()) {
            handleRestrain(wache, target);
        } else {
            handleSearch(wache, target);
        }

        event.setCancelled(true);
    }

    private void handleSearch(Player wache, Player target) {
        SearchInventoryHolder holder = new SearchInventoryHolder(target.getUniqueId(), target.getName());
        Inventory searchInventory = Bukkit.createInventory(
                holder,
                27,
                searchTitle + ": " + target.getName()
        );

        searchInventory.setContents(target.getInventory().getContents());
        holder.setInventory(searchInventory);

        wache.openInventory(searchInventory);

        String message = targetSearchedMessage.replace("{wache}", wache.getName());
        target.sendMessage(message);
    }

    private void handleRestrain(Player wache, Player target) {
        if (restrainManager.isRestrained(target.getUniqueId())) {
            return;
        }

        restrainManager.restrain(target.getUniqueId());
        target.setFreezeTicks(Integer.MAX_VALUE);

        String message = wacheRestrainedMessage.replace("{spieler}", target.getName());
        wache.sendMessage(message);

        String targetMessage = "§cDu bist jetzt festgehalten!";
        target.sendMessage(targetMessage);
    }
}


