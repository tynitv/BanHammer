package fr.banhammer.listeners;

import fr.banhammer.BanHammerPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanHammerListener implements Listener {

    private final BanHammerPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public BanHammerListener(BanHammerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager))
            return;
        if (!(event.getEntity() instanceof Player victim))
            return;

        if (!plugin.getItemManager().isBanHammer(damager.getInventory().getItemInMainHand())) {
            return;
        }

        if (!damager.hasPermission("banhammer.use")) {
            damager.sendMessage(mm.deserialize(plugin.getMessage("no-permission")));
            event.setCancelled(true);
            return;
        }

        if (victim.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Apply configurable effects
        if (plugin.getConfig().getBoolean("effects.lightning", true)) {
            victim.getWorld().strikeLightningEffect(victim.getLocation());
        }

        if (plugin.getConfig().getBoolean("effects.explosion", true)) {
            victim.getWorld().createExplosion(victim.getLocation(), 0F, false);
        }

        if (plugin.getConfig().getBoolean("effects.sound", true)) {
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);
        }

        if (plugin.getConfig().getBoolean("effects.particles", true)) {
            victim.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, victim.getLocation(), 5);
        }

        // Apply Configurable Potion Effects
        ConfigurationSection potionSec = plugin.getConfig().getConfigurationSection("effects.potion-effects");
        if (potionSec != null) {
            for (String key : potionSec.getKeys(false)) {
                PotionEffectType type = PotionEffectType.getByName(key);
                if (type != null) {
                    int duration = potionSec.getInt(key + ".duration-seconds", 5) * 20;
                    int amp = potionSec.getInt(key + ".amplifier", 1);
                    victim.addPotionEffect(new PotionEffect(type, duration, amp));
                }
            }
        }

        // Broadcast Message
        String broadcastMsg = plugin.getMessage("broadcast").replace("<player>", victim.getName());
        Bukkit.broadcast(mm.deserialize(broadcastMsg));

        // Sanction Action Mode (BAN, TEMP_BAN, KICK, MUTE, LIGHTNING_ONLY)
        String actionMode = plugin.getConfig().getString("action-mode", "BAN").toUpperCase();
        String banReason = plugin.getMessage("ban-reason");

        switch (actionMode) {
            case "TEMP_BAN":
                int hours = plugin.getConfig().getInt("temp-ban-duration-hours", 24);
                victim.ban(banReason, Duration.ofHours(hours), damager.getName());
                victim.kick(mm.deserialize(banReason));
                break;
            case "KICK":
                victim.kick(mm.deserialize(banReason));
                break;
            case "LIGHTNING_ONLY":
                // Just lightning & effects applied above
                break;
            case "BAN":
            default:
                victim.ban(banReason, (Duration) null, damager.getName());
                victim.kick(mm.deserialize(banReason));
                break;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!plugin.getItemManager().isBanHammer(player.getInventory().getItemInMainHand())) {
            return;
        }

        if (!plugin.getConfig().getBoolean("shockwave.enabled", true)) {
            return;
        }

        if (!player.hasPermission("banhammer.use")) {
            player.sendMessage(mm.deserialize(plugin.getMessage("no-permission")));
            return;
        }

        // Cooldown check
        int cooldownSeconds = plugin.getConfig().getInt("shockwave.cooldown-seconds", 10);
        long now = System.currentTimeMillis();
        long lastUse = cooldowns.getOrDefault(player.getUniqueId(), 0L);

        if (now - lastUse < cooldownSeconds * 1000L) {
            long remaining = ((lastUse + cooldownSeconds * 1000L) - now) / 1000L + 1;
            String msg = plugin.getMessage("cooldown").replace("<seconds>", String.valueOf(remaining));
            player.sendMessage(mm.deserialize(msg));
            return;
        }

        cooldowns.put(player.getUniqueId(), now);

        // Perform Shockwave
        Location loc = player.getLocation();
        double radius = plugin.getConfig().getDouble("shockwave.radius", 6.0);
        double force = plugin.getConfig().getDouble("shockwave.knockback-force", 1.8);

        loc.getWorld().strikeLightningEffect(loc);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 3);
        loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 50, radius / 2, 0.5, radius / 2, 0.1);

        for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (entity.equals(player)) continue;
            if (entity instanceof LivingEntity living) {
                if (entity instanceof Player targetPlayer && targetPlayer.getGameMode() == GameMode.CREATIVE) {
                    continue;
                }
                Vector direction = living.getLocation().toVector().subtract(loc.toVector()).normalize().setY(0.5);
                living.setVelocity(direction.multiply(force));
                living.getWorld().spawnParticle(Particle.CRIT, living.getLocation(), 20);
            }
        }
    }
}
