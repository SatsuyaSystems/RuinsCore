package de.satsuya.ruinsCore.listeners.classes;

import de.satsuya.ruinsCore.core.classes.RuinClassService;
import de.satsuya.ruinsCore.core.classes.RuinClassType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class ClassBuffListener implements Listener {

    private final RuinClassService classService;
    private final JavaPlugin plugin;
    private BukkitTask particleTask;

    private static final List<PotionEffectType> BAD_EFFECTS = Arrays.asList(
        PotionEffectType.BLINDNESS, PotionEffectType.NAUSEA, PotionEffectType.HUNGER,
        PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.MINING_FATIGUE,
        PotionEffectType.WEAKNESS, PotionEffectType.WITHER, PotionEffectType.UNLUCK
    );

    private static final List<Material> VEGETARIAN_FOOD = Arrays.asList(
        Material.APPLE, Material.BREAD, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE,
        Material.MUSHROOM_STEW, Material.CARROT, Material.POTATO, Material.BAKED_POTATO,
        Material.BEETROOT, Material.MELON_SLICE, Material.PUMPKIN_PIE, Material.SWEET_BERRIES,
        Material.GLOW_BERRIES, Material.COOKIE, Material.HONEY_BOTTLE
    );

    // Guard, damit der von uns selbst neu gesetzte Effekt nicht erneut halbiert wird.
    private final Set<UUID> hexeReapplyGuard = new HashSet<>();

    public ClassBuffListener(RuinClassService classService, JavaPlugin plugin) {
        this.classService = classService;
        this.plugin = plugin;
        startParticleTask();
    }

    private void startParticleTask() {
        particleTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Optional<RuinClassType> type = classService.getClass(p.getUniqueId());
                if (type.isPresent() && type.get() == RuinClassType.WALDGEIST) {
                    if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        p.getWorld().spawnParticle(Particle.SOUL, p.getLocation().add(0, 1, 0), 2, 0.3, 0.5, 0.3, 0.01);
                    }
                }
            }
        }, 10L, 10L); // Every 0.5 seconds
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Optional<RuinClassType> type = classService.getClass(player.getUniqueId());

        if (type.isPresent() && type.get() == RuinClassType.WALDGEIST) {
            if (VEGETARIAN_FOOD.contains(event.getItem().getType())) {
                float currentSaturation = player.getSaturation();
                player.setSaturation(Math.min(currentSaturation + 4.0f, player.getFoodLevel()));
            }
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Optional<RuinClassType> type = classService.getClass(player.getUniqueId());
        if (type.isEmpty() || type.get() != RuinClassType.HEXE) {
            return;
        }

        if (event.getAction() != EntityPotionEffectEvent.Action.ADDED || event.getNewEffect() == null) {
            return;
        }

        if (!BAD_EFFECTS.contains(event.getNewEffect().getType())) {
            return;
        }

        UUID playerId = player.getUniqueId();
        if (hexeReapplyGuard.remove(playerId)) {
            return;
        }

        int originalDuration = event.getNewEffect().getDuration();
        if (originalDuration <= 1) {
            return;
        }

        event.setCancelled(true);

        PotionEffect newEffect = event.getNewEffect();
        int halfDuration = Math.max(1, originalDuration / 2);

        Bukkit.getScheduler().runTask(plugin, () -> {
            hexeReapplyGuard.add(playerId);
            player.addPotionEffect(new PotionEffect(
                newEffect.getType(),
                halfDuration,
                newEffect.getAmplifier(),
                newEffect.isAmbient(),
                newEffect.hasParticles(),
                newEffect.hasIcon()
            ), true);
        });
    }
}
