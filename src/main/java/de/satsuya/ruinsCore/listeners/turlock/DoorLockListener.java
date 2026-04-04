package de.satsuya.ruinsCore.listeners.turlock;

import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import de.satsuya.ruinsCore.core.turlock.DoorLockService;
import de.satsuya.ruinsCore.core.turlock.DoorLockGuiService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Listener für Türschloss-System
 */
public final class DoorLockListener implements Listener {

    private final DoorLockService doorLockService;
    private final DoorLockGuiService doorLockGuiService;
    private final JobService jobService;

    public DoorLockListener(DoorLockService doorLockService, DoorLockGuiService doorLockGuiService, JobService jobService) {
        this.doorLockService = doorLockService;
        this.doorLockGuiService = doorLockGuiService;
        this.jobService = jobService;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Prüfe ob es eine Tür ist und SHIFT Rechtsklick
        if (block == null || !(block.getBlockData() instanceof Door)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Prüfe ob SHIFT gedrückt
        if (!player.isSneaking()) {
            return;
        }

        // Prüfe ob Haupthand leer ist
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() != Material.AIR) {
            return;
        }

        // Prüfe ob Spieler diese Tür verwalten darf
        DoorLockService.DoorLock lock = doorLockService.getLock(block.getLocation());

        // Wenn keine Lock = Spieler kann sie erstellen
        if (lock == null) {
            event.setCancelled(true);
            doorLockGuiService.openDoorLockGui(player, block.getLocation());
            return;
        }

        // Nur Besitzer oder Admins dürfen das GUI öffnen
        boolean isAdmin = player.hasPermission("ruinscore.command.turlock.admin");
        if (!lock.ownerUuid.equals(player.getUniqueId().toString()) && !isAdmin) {
            player.sendMessage("§c✗ Du darfst diese Tür nicht verwalten!");
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        doorLockGuiService.openDoorLockGui(player, block.getLocation());
    }

    @EventHandler
    public void onPlayerOpenDoor(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Prüfe ob es eine Tür ist (aber KEIN SHIFT Rechtsklick)
        if (block == null || !(block.getBlockData() instanceof Door)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // SHIFT Rechtsklick ist bereits oben behandelt
        if (player.isSneaking()) {
            return;
        }

        // Prüfe ob Spieler die Tür öffnen darf
        Optional<JobType> jobOptional = jobService.getJob(player.getUniqueId());
        boolean isWache = jobOptional.isPresent() && jobOptional.get() == JobType.WACHE;

        if (!doorLockService.canOpenDoor(block.getLocation(), player.getUniqueId(), isWache)) {
            event.setCancelled(true);
            player.sendMessage("§c✗ Diese Tür ist verschlossen!");
        }
    }
}

