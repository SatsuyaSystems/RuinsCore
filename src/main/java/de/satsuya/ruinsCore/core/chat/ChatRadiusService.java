package de.satsuya.ruinsCore.core.chat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Service für Chat-Radius Management
 */
public final class ChatRadiusService {

    private final Plugin plugin;
    private boolean enabled;
    private double radius;

    public ChatRadiusService(Plugin plugin) {
        this.plugin = plugin;
        loadConfiguration();
    }

    /**
     * Lade Konfiguration
     */
    public void loadConfiguration() {
        this.enabled = plugin.getConfig().getBoolean("chat.enabled", true);
        this.radius = plugin.getConfig().getDouble("chat.radius", 30.0);
    }

    /**
     * Prüfe ob Chat-Radius aktiviert ist
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gibt den Chat-Radius zurück
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Gibt alle Spieler zurück, die eine Nachricht empfangen sollen
     */
    public Set<Player> getReceipients(Player sender) {
        Set<Player> recipients = new HashSet<>();

        // Wenn deaktiviert: alle Spieler bekommen die Nachricht
        if (!enabled) {
            recipients.addAll(Bukkit.getOnlinePlayers());
            return recipients;
        }

        Location senderLocation = sender.getLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            // Admin mit Bypass-Permission sieht alles
            if (player.hasPermission("ruinscore.chat.bypass")) {
                recipients.add(player);
                continue;
            }

            // Prüfe ob Spieler in Reichweite ist
            Location playerLocation = player.getLocation();

            // Gleiche Welt prüfen
            if (!senderLocation.getWorld().equals(playerLocation.getWorld())) {
                continue;
            }

            // Distanz berechnen
            double distance = senderLocation.distance(playerLocation);

            if (distance <= radius) {
                recipients.add(player);
            }
        }

        return recipients;
    }

    /**
     * Berechne die Distanz zwischen zwei Spielern
     */
    public double getDistance(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();

        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return Double.MAX_VALUE;
        }

        return loc1.distance(loc2);
    }
}

