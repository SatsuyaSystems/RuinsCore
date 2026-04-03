package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.chat.ChatRadiusService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Listener für Chat-Radius Funktionalität
 */
public final class ChatRadiusListener implements Listener {

    private final ChatRadiusService chatRadiusService;

    public ChatRadiusListener(RuinsCore plugin) {
        this.chatRadiusService = plugin.getChatRadiusService();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();

        // Wenn Radius deaktiviert: normale Nachricht für alle
        if (!chatRadiusService.isEnabled()) {
            return;
        }

        // Wenn Sender hat Bypass: normale Nachricht für alle
        if (sender.hasPermission("ruinscore.chat.bypass")) {
            return;
        }

        // Filtere die Empfänger basierend auf Radius
        Set<Player> recipients = chatRadiusService.getReceipients(sender);

        // Sender sieht seine eigene Nachricht immer
        recipients.add(sender);

        // Setze die neuen Empfänger
        event.getRecipients().clear();
        event.getRecipients().addAll(recipients);
    }
}

