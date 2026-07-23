package fr.banhammer.listeners;

import fr.banhammer.BanHammerPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class BanHammerListener implements Listener {

    private final BanHammerPlugin plugin;

    public BanHammerListener(BanHammerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager))
            return;
        if (!(event.getEntity() instanceof Player victim))
            return;

        // Check if using Ban Hammer
        if (!plugin.getItemManager().isBanHammer(damager.getInventory().getItemInMainHand())) {
            return;
        }

        // Check permission
        if (!damager.hasPermission("banhammer.use")) {
            damager.sendMessage("§cVous n'avez pas la permission d'utiliser cet objet légendaire.");
            event.setCancelled(true);
            return;
        }

        // Creative mode check (optional, but good practice)
        if (victim.getGameMode() == GameMode.CREATIVE || victim.getGameMode() == GameMode.SPECTATOR) {
            // Let's decide: typically admin tools bypass creative, but to be safe maybe
            // not?
            // Prompt says: "Ne fonctionne pas en mode créatif si tu veux éviter abus
            // (optionnel)"
            // Let's protect Creative players from being banned by accident
            if (victim.getGameMode() == GameMode.CREATIVE)
                return;
        }

        // Apply effects
        if (plugin.getConfig().getBoolean("lightning-effect")) {
            victim.getWorld().strikeLightningEffect(victim.getLocation());
        }

        if (plugin.getConfig().getBoolean("explosion-effect")) {
            victim.getWorld().createExplosion(victim.getLocation(), 0F, false); // 0F = visual only
        }

        if (plugin.getConfig().getBoolean("sound-effect")) {
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 0.5f);
        }

        // Broadcast
        String broadcastMsg = plugin.getConfig()
                .getString("broadcast-message", "§4<player> a été annihilé par le Ban Hammer !")
                .replace("<player>", victim.getName());
        Bukkit.broadcast(Component.text(broadcastMsg));

        // Ban Logic
        String banReason = plugin.getConfig().getString("ban-message", "§cVous avez été frappé par le Ban Hammer.");

        // Using Player#ban (Paper API)
        victim.ban(banReason, (Duration) null, damager.getName());

        victim.kick(Component.text(banReason));

        // Cancel the actual damage because they are banned/kicked anyway
        event.setCancelled(true);
    }
}
