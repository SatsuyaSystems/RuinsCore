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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class WacheSearchListener implements Listener {

    private static final int SEARCH_INVENTORY_SIZE = 54;
    private static final int RESTRAIN_SLOWNESS_DURATION_TICKS = Integer.MAX_VALUE;
    private static final int RESTRAIN_SLOWNESS_AMPLIFIER = 6;

    private final JobService jobService;
    private final WacheRestrainManager restrainManager;
    private final String searchTitle;
    private final String targetSearchedMessage;
    private final String wacheRestrainedMessage;
    private final String releasedMessage;
    private final String wacheReleasedMessage;

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
        this.releasedMessage = plugin.getConfig().getString(
                "jobs.wache.messages.released-message",
                "§aDu bist freigelassen."
        );
        this.wacheReleasedMessage = plugin.getConfig().getString(
                "jobs.wache.messages.wache-released",
                "§a{spieler} ist freigelassen."
        );

        plugin.getLogger().info("WacheSearchListener aktiv (main-hand only, toggle restrain, search-size=54)");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

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
            if (!restrainManager.shouldProcessToggle(wache.getUniqueId(), target.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
            handleRestrainToggle(wache, target);
        } else {
            handleSearch(wache, target);
        }

        event.setCancelled(true);
    }

    private void handleSearch(Player wache, Player target) {
        SearchInventoryHolder holder = new SearchInventoryHolder(target.getUniqueId(), target.getName());
        Inventory searchInventory = Bukkit.createInventory(
                holder,
                SEARCH_INVENTORY_SIZE,
                searchTitle + ": " + target.getName()
        );

        // Player-Inventar kann mehr Einträge haben (z. B. Rüstung/Offhand) als ein kleines Chest-Inventory.
        // Daher nur so viele Slots kopieren, wie das Zielinventar aufnehmen kann.
        org.bukkit.inventory.ItemStack[] contents = target.getInventory().getContents();
        int max = Math.min(contents.length, searchInventory.getSize());
        for (int i = 0; i < max; i++) {
            searchInventory.setItem(i, contents[i]);
        }
        holder.setInventory(searchInventory);

        wache.openInventory(searchInventory);

        String message = targetSearchedMessage.replace("{wache}", wache.getName());
        target.sendMessage(message);
    }

    private void handleRestrainToggle(Player wache, Player target) {
        if (restrainManager.isRestrained(target.getUniqueId())) {
            restrainManager.release(target.getUniqueId());
            target.removePotionEffect(PotionEffectType.SLOWNESS);

            target.sendMessage(releasedMessage);
            String releaseMessage = wacheReleasedMessage.replace("{spieler}", target.getName());
            wache.sendMessage(releaseMessage);
            return;
        }

        restrainManager.restrain(target.getUniqueId());
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOWNESS,
                RESTRAIN_SLOWNESS_DURATION_TICKS,
                RESTRAIN_SLOWNESS_AMPLIFIER,
                false,
                false,
                true
        ));

        String message = wacheRestrainedMessage.replace("{spieler}", target.getName());
        wache.sendMessage(message);

        String targetMessage = "§cDu bist jetzt festgehalten!";
        target.sendMessage(targetMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSearchClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof SearchInventoryHolder holder)) {
            return;
        }

        Player target = Bukkit.getPlayer(holder.getTargetUuid());
        if (target == null) {
            return;
        }

        PlayerInventory targetInventory = target.getInventory();
        org.bukkit.inventory.ItemStack[] updated = new org.bukkit.inventory.ItemStack[targetInventory.getContents().length];

        int max = Math.min(updated.length, event.getInventory().getSize());
        for (int i = 0; i < max; i++) {
            updated[i] = event.getInventory().getItem(i);
        }

        targetInventory.setContents(updated);
        target.updateInventory();
    }
}


