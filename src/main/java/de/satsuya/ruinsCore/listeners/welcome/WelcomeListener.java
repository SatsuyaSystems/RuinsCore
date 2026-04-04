package de.satsuya.ruinsCore.listeners.welcome;

import de.satsuya.ruinsCore.core.welcome.WelcomeService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener für Welcome Messages
 */
public final class WelcomeListener implements Listener {

    private final WelcomeService welcomeService;

    public WelcomeListener(WelcomeService welcomeService) {
        this.welcomeService = welcomeService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (welcomeService.isNewPlayer(event.getPlayer().getUniqueId())) {
            // Neue Spieler-Welcome-Message
            String[] welcomeMessage = welcomeService.getWelcomeMessage(event.getPlayer().getName());

            // Sende die Message zum Spieler
            for (String line : welcomeMessage) {
                event.getPlayer().sendMessage(line);
            }

            // Broadcast dass ein neuer Spieler beigetreten ist
            String broadcastMessage = "§d✨ §5" + event.getPlayer().getName() + " §dhat den Server zum ersten Mal betreten! Willkommen! 🎉 §5✨";
            Bukkit.broadcast(net.kyori.adventure.text.Component.text(broadcastMessage));

            // Markiere als begrüßt
            welcomeService.markAsWelcomed(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        }
    }
}

