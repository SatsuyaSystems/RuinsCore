package de.satsuya.ruinsCore.listeners.classes;

import de.satsuya.ruinsCore.core.classes.RuinClassService;
import de.satsuya.ruinsCore.core.classes.RuinClassType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ClassEnvironmentListener implements Listener {

    private final RuinClassService classService;
    private final JavaPlugin plugin;
    private BukkitTask waterDamageTask;
    private final Map<UUID, Long> lastWaterDamage = new HashMap<>();

    public ClassEnvironmentListener(RuinClassService classService, JavaPlugin plugin) {
        this.classService = classService;
        this.plugin = plugin;
        startWaterDamageTask();
    }

    private void startWaterDamageTask() {
        waterDamageTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            for (Player player : Bukkit.getOnlinePlayers()) {
                Optional<RuinClassType> classOpt = classService.getClass(player.getUniqueId());
                if (classOpt.isPresent() && classOpt.get() == RuinClassType.DEMON) {
                    Material mat = player.getLocation().getBlock().getType();
                    Material eyeMat = player.getEyeLocation().getBlock().getType();

                    if (mat == Material.WATER || eyeMat == Material.WATER) {
                        Long lastDamage = lastWaterDamage.get(player.getUniqueId());
                        if (lastDamage == null || now - lastDamage >= 1000) {
                            player.damage(1.0); // 1 HP (0.5 Hearts)
                            lastWaterDamage.put(player.getUniqueId(), now);
                        }
                    }
                }
            }
        }, 20L, 10L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Optional<RuinClassType> classOpt = classService.getClass(player.getUniqueId());

        if (classOpt.isPresent() && classOpt.get() == RuinClassType.FEE) {
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR && !player.getAllowFlight()) {
                player.setAllowFlight(true);
            }

            if (player.isFlying()) {
                Location loc = player.getLocation();
                int highestBlockY = -1;

                // Raycast down to find ground
                for (int y = loc.getBlockY() - 1; y > loc.getWorld().getMinHeight(); y--) {
                    Block b = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
                    if (!b.isPassable()) {
                        highestBlockY = y;
                        break;
                    }
                }

                if (highestBlockY != -1) {
                    double heightAboveGround = loc.getY() - highestBlockY;
                    if (heightAboveGround > 7.0) {
                        // Soft cap: force down slightly
                        Location newLoc = loc.clone();
                        newLoc.setY(highestBlockY + 7.0);
                        event.setTo(newLoc);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        Optional<RuinClassType> classOpt = classService.getClass(player.getUniqueId());
        if (classOpt.isEmpty() || classOpt.get() != RuinClassType.FEE) {
            return;
        }

        if (event.isFlying()) {
            event.setCancelled(true);
            player.setFlying(true);
        }
    }

    @EventHandler
    public void onDemonLavaDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Optional<RuinClassType> classOpt = classService.getClass(player.getUniqueId());
        if (classOpt.isEmpty() || classOpt.get() != RuinClassType.DEMON) {
            return;
        }

        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.LAVA
            || cause == EntityDamageEvent.DamageCause.FIRE
            || cause == EntityDamageEvent.DamageCause.FIRE_TICK
            || cause == EntityDamageEvent.DamageCause.HOT_FLOOR) {
            event.setCancelled(true);
        }
    }
}
