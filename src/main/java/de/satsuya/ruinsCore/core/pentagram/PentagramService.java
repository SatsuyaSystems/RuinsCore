package de.satsuya.ruinsCore.core.pentagram;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service zum Erstellen von Pentagrammen aus Feuer-Partikeln (pro Spieler)
 */
public final class PentagramService {

    private final Plugin plugin;
    private final Map<UUID, BukkitTask> playerPentagrams = new HashMap<>();
    private final Map<UUID, PentagramData> playerPentagramData = new HashMap<>();

    public PentagramService(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Erstelle ein Pentagramm aus Feuer-Partikeln um die Position eines Spielers
     */
    public void createPentagram(Player player, double radius) {
        Location center = player.getLocation();
        center = center.clone();
        center.setY(center.getY() + 0.5); // Etwas über dem Boden

        UUID playerUuid = player.getUniqueId();

        // Lösche alten Pentagramm-Task für diesen Spieler
        if (playerPentagrams.containsKey(playerUuid)) {
            playerPentagrams.get(playerUuid).cancel();
        }

        // Speichere die Pentagramm-Daten für diesen Spieler
        playerPentagramData.put(playerUuid, new PentagramData(center, radius));

        // Starte einen repeating Task für diesen Spieler (alle 2 Ticks)
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(
            plugin,
            () -> drawPentagramLines(playerUuid),
            0L,   // Sofort starten
            2L    // Wiederholen alle 2 Ticks (alle 0.1 Sekunde)
        );
        playerPentagrams.put(playerUuid, task);

        // Stoppe den Task nach 10 Sekunden (200 Ticks) nur für diesen Spieler
        plugin.getServer().getScheduler().runTaskLater(
            plugin,
            () -> removePentagram(playerUuid),
            200L
        );
    }

    /**
     * Entferne das Pentagramm eines Spielers
     */
    private void removePentagram(UUID playerUuid) {
        if (playerPentagrams.containsKey(playerUuid)) {
            playerPentagrams.get(playerUuid).cancel();
            playerPentagrams.remove(playerUuid);
        }
        playerPentagramData.remove(playerUuid);
    }

    /**
     * Zeichne alle Linien des Pentagramms für einen Spieler
     */
    private void drawPentagramLines(UUID playerUuid) {
        PentagramData data = playerPentagramData.get(playerUuid);
        if (data == null) {
            return;
        }

        Location center = data.center;
        double radius = data.radius;

        // Erstelle 5 Spitzen des Pentagramms
        for (int i = 0; i < 5; i++) {
            // Winkel für jede Spitze (72 Grad = 360/5)
            double angle = (i * 72) * Math.PI / 180.0;

            // Berechne Position für diese Spitze
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);

            Location spitze = new Location(center.getWorld(), x, center.getY(), z);

            // Verbinde diese Spitze mit den nächsten zwei Spitzen
            // (um einen 5-zackigen Stern zu zeichnen)
            for (int j = 1; j <= 2; j++) {
                double nextAngle = ((i + j * 2) % 5 * 72) * Math.PI / 180.0;
                double nextX = center.getX() + radius * Math.cos(nextAngle);
                double nextZ = center.getZ() + radius * Math.sin(nextAngle);
                Location nextSpitze = new Location(center.getWorld(), nextX, center.getY(), nextZ);

                // Zeichne eine Linie zwischen den Spitzen mit Partikeln
                drawLine(spitze, nextSpitze);
            }
        }
    }

    /**
     * Zeichne eine Linie zwischen zwei Punkten mit Feuer-Partikeln
     */
    private void drawLine(Location start, Location end) {
        double distance = start.distance(end);
        double step = 0.4; // Alle 0.4 Blöcke ein Partikel
        int steps = (int) (distance / step) + 1;

        for (int i = 0; i <= steps; i++) {
            double progress = (double) i / steps;

            double x = start.getX() + (end.getX() - start.getX()) * progress;
            double y = start.getY();
            double z = start.getZ() + (end.getZ() - start.getZ()) * progress;

            Location particleLocation = new Location(start.getWorld(), x, y, z);

            // Spawne Feuer-Partikel mit Geschwindigkeit 0 (damit sie nicht wegfliegen)
            start.getWorld().spawnParticle(
                Particle.FLAME,
                particleLocation,
                1,           // Anzahl der Partikel pro Spawn (1 = stabil)
                0,           // Kein Offset
                0,
                0,
                0            // Geschwindigkeit 0 (nicht wegfliegen)
            );
        }
    }

    /**
     * Interne Klasse zum Speichern der Pentagramm-Daten
     */
    private static class PentagramData {
        final Location center;
        final double radius;

        PentagramData(Location center, double radius) {
            this.center = center;
            this.radius = radius;
        }
    }
}

