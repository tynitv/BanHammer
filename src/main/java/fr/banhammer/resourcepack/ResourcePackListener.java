package fr.banhammer.resourcepack;

import fr.banhammer.BanHammerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackListener implements Listener {

    private final BanHammerPlugin plugin;
    private final ResourcePackServer packServer;
    private final int port;

    public ResourcePackListener(BanHammerPlugin plugin, ResourcePackServer packServer, int port) {
        this.plugin = plugin;
        this.packServer = packServer;
        this.port = port;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("resource-pack.auto-send-on-join", true)) return;

        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                String configUrl = plugin.getConfig().getString("resource-pack.url", "");
                String packUrl;
                if (configUrl != null && !configUrl.isEmpty()) {
                    packUrl = configUrl;
                } else {
                    String host = Bukkit.getIp().isEmpty() ? "127.0.0.1" : Bukkit.getIp();
                    packUrl = "http://" + host + ":" + port + "/resourcepack.zip";
                }

                byte[] hash = packServer != null ? packServer.getSha1HashBytes() : null;
                if (hash != null) {
                    player.setResourcePack(packUrl, hash);
                } else {
                    player.setResourcePack(packUrl);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not send resource pack to " + player.getName() + ": " + e.getMessage());
            }
        }, 40L);
    }
}
