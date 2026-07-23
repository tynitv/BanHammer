package fr.banhammer.listeners;

import fr.banhammer.BanHammerPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;

public class BanHammerListener implements Listener {

    private final BanHammerPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

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

        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (!damager.hasPermission("banhammer.use")) {
            damager.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-permission", "").replace("<prefix>", prefix)));
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
        String broadcastMsg = plugin.getConfig().getString("messages.broadcast", "")
                .replace("<prefix>", prefix)
                .replace("<player>", victim.getName());
        Bukkit.broadcast(mm.deserialize(broadcastMsg));

        // Ban & Kick
        String banReason = plugin.getConfig().getString("messages.ban-reason", "Vous avez été ban par le BanHammer.");
        victim.ban(banReason, (Duration) null, damager.getName());
        victim.kick(mm.deserialize(banReason));

        event.setCancelled(true);
    }
}
