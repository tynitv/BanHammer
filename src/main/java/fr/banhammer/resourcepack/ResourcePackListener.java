package fr.banhammer.resourcepack;

import fr.banhammer.BanHammerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackListener implements Listener {

    private final BanHammerPlugin plugin;
    private final int port;

    public ResourcePackListener(BanHammerPlugin plugin, int port) {
        this.plugin = plugin;
        this.port = port;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("resource-pack.auto-send-on-join", true)) return;

        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                String host = Bukkit.getIp().isEmpty() ? "localhost" : Bukkit.getIp();
                String packUrl = "http://" + host + ":" + port + "/resourcepack.zip";
                player.setResourcePack(packUrl);
            } catch (Exception ignored) {}
        }, 40L);
    }
}
