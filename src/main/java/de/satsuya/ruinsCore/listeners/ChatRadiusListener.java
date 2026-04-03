package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.chat.ChatRadiusService;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import de.satsuya.ruinsCore.core.jobs.JobColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

/**
 * Listener für Chat-Radius Funktionalität mit Job-Prefix
 */
public final class ChatRadiusListener implements Listener {

    private final ChatRadiusService chatRadiusService;
    private final JobService jobService;

    public ChatRadiusListener(RuinsCore plugin) {
        this.chatRadiusService = plugin.getChatRadiusService();
        this.jobService = plugin.getJobService();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String jobPrefix = getJobPrefix(sender.getUniqueId());
        String message = event.getMessage();

        // Wenn Radius deaktiviert: normale Nachricht für alle
        if (!chatRadiusService.isEnabled()) {
            event.setFormat(jobPrefix + " §7%1$s§7: §f%2$s");
            return;
        }

        // Wenn Sender hat Bypass: normale Nachricht für alle
        if (sender.hasPermission("ruinscore.chat.bypass")) {
            event.setFormat(jobPrefix + " §7%1$s§7: §f%2$s");
            return;
        }

        // Cancele das Original-Event
        event.setCancelled(true);

        // Filtere die Empfänger basierend auf Radius
        Set<Player> recipients = chatRadiusService.getReceipients(sender);

        // Sender sieht seine eigene Nachricht immer
        recipients.add(sender);

        // Erstelle formatierte Nachricht
        Component formattedMessage = Component.text(
            jobPrefix + " §7" + sender.getName() + "§7: §f" + message,
            NamedTextColor.WHITE
        );

        // Sende Nachricht nur an zugelassene Spieler
        for (Player recipient : recipients) {
            recipient.sendMessage(formattedMessage);
        }
    }

    /**
     * Gibt den Job-Prefix eines Spielers zurück
     */
    private String getJobPrefix(java.util.UUID playerUuid) {
        Optional<JobType> job = jobService.getJob(playerUuid);
        if (job.isPresent()) {
            JobColor jobColor = JobColor.fromJobType(job.get());
            if (jobColor != null) {
                return jobColor.getDisplayName();
            }
        }
        return "§7[?]";
    }
}

