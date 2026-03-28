package de.satsuya.ruinsCore.listeners;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.jobs.JobService;
import de.satsuya.ruinsCore.core.jobs.JobType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.EnumSet;
import java.util.Set;

public final class SmithRestrictionListener implements Listener {

    private static final Set<Material> RESTRICTED_CRAFT_ITEMS = EnumSet.of(
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS,
            Material.IRON_SWORD,
            Material.IRON_PICKAXE,
            Material.IRON_AXE,
            Material.IRON_SHOVEL,
            Material.IRON_HOE,
            Material.GOLDEN_SWORD,
            Material.GOLDEN_PICKAXE,
            Material.GOLDEN_AXE,
            Material.GOLDEN_SHOVEL,
            Material.GOLDEN_HOE,
            Material.DIAMOND_SWORD,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_AXE,
            Material.DIAMOND_SHOVEL,
            Material.DIAMOND_HOE,
            Material.NETHERITE_SWORD,
            Material.NETHERITE_PICKAXE,
            Material.NETHERITE_AXE,
            Material.NETHERITE_SHOVEL,
            Material.NETHERITE_HOE
    );

    private final JobService jobService;
    private final String denyCraftMessage;
    private final String denyUpgradeMessage;
    private final String denyRepairMessage;

    public SmithRestrictionListener(RuinsCore plugin) {
        this.jobService = plugin.getJobService();
        this.denyCraftMessage = plugin.getConfig().getString(
                "jobs.schmied.restrictions.deny-craft-message",
                "§cNur Schmiede dürfen diese Rüstung oder Werkzeuge craften."
        );
        this.denyUpgradeMessage = plugin.getConfig().getString(
                "jobs.schmied.restrictions.deny-upgrade-message",
                "§cNur Schmiede dürfen Netherite-Upgrades durchführen."
        );
        this.denyRepairMessage = plugin.getConfig().getString(
                "jobs.schmied.restrictions.deny-repair-message",
                "§cNur Schmiede dürfen diese Rüstung oder Werkzeuge reparieren."
        );
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (isSmith(player)) {
            return;
        }

        ItemStack result = event.getRecipe().getResult();
        if (!RESTRICTED_CRAFT_ITEMS.contains(result.getType())) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyCraftMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmith(SmithItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (isSmith(player)) {
            return;
        }

        ItemStack result = event.getInventory().getResult();
        if (result == null || !RESTRICTED_CRAFT_ITEMS.contains(result.getType())) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyUpgradeMessage);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnvilResultTake(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (isSmith(player)) {
            return;
        }

        if (event.getView().getTopInventory().getType() != InventoryType.ANVIL || event.getRawSlot() != 2) {
            return;
        }

        if (!(event.getView().getTopInventory() instanceof AnvilInventory anvilInventory)) {
            return;
        }

        ItemStack result = event.getCurrentItem();
        if (result == null || !RESTRICTED_CRAFT_ITEMS.contains(result.getType())) {
            return;
        }

        if (!isRepairOperation(anvilInventory, result)) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(denyRepairMessage);
    }

    private boolean isSmith(Player player) {
        return jobService.hasJob(player.getUniqueId(), JobType.SCHMIED);
    }

    private boolean isRepairOperation(AnvilInventory inventory, ItemStack result) {
        ItemStack base = inventory.getFirstItem();
        ItemStack addition = inventory.getSecondItem();
        if (base == null || addition == null) {
            return false;
        }

        if (!RESTRICTED_CRAFT_ITEMS.contains(base.getType())) {
            return false;
        }

        if (addition.getType() == base.getType()) {
            return true;
        }

        Material repairMaterial = getRepairMaterial(base.getType());
        if (repairMaterial != addition.getType()) {
            return false;
        }

        if (!(base.getItemMeta() instanceof Damageable baseMeta)
                || !(result.getItemMeta() instanceof Damageable resultMeta)) {
            return true;
        }

        return resultMeta.getDamage() < baseMeta.getDamage();
    }

    private Material getRepairMaterial(Material material) {
        String name = material.name();
        if (name.startsWith("IRON_")) {
            return Material.IRON_INGOT;
        }
        if (name.startsWith("GOLDEN_")) {
            return Material.GOLD_INGOT;
        }
        if (name.startsWith("DIAMOND_")) {
            return Material.DIAMOND;
        }
        if (name.startsWith("NETHERITE_")) {
            return Material.NETHERITE_INGOT;
        }

        return Material.AIR;
    }
}


