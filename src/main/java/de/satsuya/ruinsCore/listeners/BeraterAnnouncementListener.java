package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class BeraterAnnouncementListener implements Listener {

    private final JobService jobService;
    private final String announcementPrefix;

    public BeraterAnnouncementListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.announcementPrefix = plugin.getConfig().getString(
                "jobs.berater.announcement-prefix",
                "§e[§6ANKÜNDIGUNG§e] §r"
        );
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!jobService.hasJob(player.getUniqueId(), JobType.BERATER)) {
            return;
        }

        String message = event.getMessage();

        // Prüfe ob Nachricht mit ! anfängt für Ankündigung
        if (!message.startsWith("!")) {
            return;
        }

        event.setCancelled(true);

        String announcement = message.substring(1).trim();

        if (announcement.isBlank()) {
            player.sendMessage("§cAnkündigung darf nicht leer sein!");
            return;
        }

        String finalMessage = announcementPrefix + announcement;

        Bukkit.broadcastMessage(finalMessage);
        player.sendMessage("§aAnkündigung verteilt.");
    }
}

