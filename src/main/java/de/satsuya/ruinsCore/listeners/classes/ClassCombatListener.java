package de.satsuya.ruinsCore.listeners.classes;

import de.satsuya.ruinsCore.core.classes.RuinClassService;
import de.satsuya.ruinsCore.core.classes.RuinClassType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.attribute.Attribute;

import java.util.*;

public final class ClassCombatListener implements Listener {

    private final RuinClassService classService;
    private final JavaPlugin plugin;

    // Schattenläufer Sneaking
    private final Map<UUID, List<Long>> sneakLog = new HashMap<>();
    private final Map<UUID, Long> sneakCooldowns = new HashMap<>();

    // Demon Killstreak
    private final Map<UUID, Integer> demonKillstreaks = new HashMap<>();
    private final Map<UUID, BukkitTask> demonTimers = new HashMap<>();

    public ClassCombatListener(RuinClassService classService, JavaPlugin plugin) {
        this.classService = classService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // Check if victim was a Demon
        Optional<RuinClassType> victimClass = classService.getClass(victim.getUniqueId());
        if (victimClass.isPresent() && victimClass.get() == RuinClassType.DEMON) {
            resetDemonKillstreak(victim);
        }

        if (killer == null) return;

        Optional<RuinClassType> killerClass = classService.getClass(killer.getUniqueId());
        if (killerClass.isPresent()) {
            RuinClassType type = killerClass.get();

            if (type == RuinClassType.ASSASINE) {
                killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0, false, false)); // 30 sec
                killer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0, false, false)); // 10 sec
            } else if (type == RuinClassType.DEMON) {
                incrementDemonKillstreak(killer);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Optional<RuinClassType> damagerClass = classService.getClass(damager.getUniqueId());

        if (damagerClass.isPresent() && damagerClass.get() == RuinClassType.DEMON) {
            double damage = event.getFinalDamage();
            int killstreak = demonKillstreaks.getOrDefault(damager.getUniqueId(), 0);

            // Lifesteal 20%
            double healAmount = damage * 0.20;
            double maxHealth = damager.getAttribute(Attribute.MAX_HEALTH).getValue();
            damager.setHealth(Math.min(damager.getHealth() + healAmount, maxHealth));

            // Killstreak modifier (up to 10%)
            if (killstreak > 0) {
                double multiplier = 1.0 + (killstreak * 0.01);
                event.setDamage(event.getDamage() * multiplier);
            }

            // Refresh timer on damage
            if (killstreak > 0) {
                resetDemonTimer(damager);
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        Optional<RuinClassType> playerClassOpt = classService.getClass(player.getUniqueId());

        if (playerClassOpt.isPresent() && playerClassOpt.get() == RuinClassType.SCHATTENLAEUFER) {
            long now = System.currentTimeMillis();

            // Check cooldown
            if (sneakCooldowns.containsKey(player.getUniqueId())) {
                long cdExpire = sneakCooldowns.get(player.getUniqueId());
                if (now < cdExpire) {
                    long remainingMs = cdExpire - now;
                    double remainingSeconds = remainingMs / 1000.0;
                    player.sendMessage("§8[Schattenläufer] §7Cooldown: §f" + String.format("%.1f", remainingSeconds) + "s");
                    return;
                }
            }

            List<Long> sneaks = sneakLog.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
            sneaks.add(now);

            // Remove sneaks older than 2 seconds
            sneaks.removeIf(time -> now - time > 2000L);

            if (sneaks.size() >= 5) {
                // Activate Invis
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 0, false, false)); // 15 sec
                player.sendMessage("§8[Schattenläufer] §7Du verschmilzt mit den Schatten...");

                // Clear and set cooldown
                sneaks.clear();
                sneakCooldowns.put(player.getUniqueId(), now + 60000L); // 60 seconds
            }
        }
    }

    private void incrementDemonKillstreak(Player demon) {
        int current = demonKillstreaks.getOrDefault(demon.getUniqueId(), 0);
        if (current < 10) {
            current++;
            demonKillstreaks.put(demon.getUniqueId(), current);
            demon.sendMessage("§4[Bloodrush] §c+" + current + "% Schaden!");
        }

        resetDemonTimer(demon);
    }

    private void resetDemonTimer(Player demon) {
        if (demonTimers.containsKey(demon.getUniqueId())) {
            demonTimers.get(demon.getUniqueId()).cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            resetDemonKillstreak(demon);
        }, 600L); // 30 sec

        demonTimers.put(demon.getUniqueId(), task);
    }

    private void resetDemonKillstreak(Player demon) {
        if (demonKillstreaks.containsKey(demon.getUniqueId())) {
            int streaks = demonKillstreaks.remove(demon.getUniqueId());
            if (demonTimers.containsKey(demon.getUniqueId())) {
                demonTimers.remove(demon.getUniqueId()).cancel();
            }
            if (streaks > 0 && demon.isOnline()) {
                demon.sendMessage("§4[Bloodrush] §7Dein Blutrausch ist abgeklungen.");
            }
        }
    }
}
