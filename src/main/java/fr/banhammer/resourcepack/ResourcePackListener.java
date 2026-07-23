package fr.banhammer.resourcepack;

import fr.banhammer.BanHammerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

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
                if (configUrl != null && !configUrl.trim().isEmpty() && !configUrl.contains("127.0.0.1") && !configUrl.contains("0.0.0.0")) {
                    packUrl = configUrl.trim();
                } else {
                    packUrl = "https://raw.githubusercontent.com/tynitv/BanHammer/main/BanHammer_ResourcePack.zip";
                }

                byte[] hash = packServer != null ? packServer.getSha1HashBytes() : null;
                plugin.getLogger().info("[ResourcePack DEBUG] Sending pack to " + player.getName() + " | URL: " + packUrl + " | Hash: " + (packServer != null ? packServer.getSha1HashHex() : "null"));

                if (hash != null && hash.length == 20) {
                    player.setResourcePack(packUrl, hash);
                } else {
                    player.setResourcePack(packUrl);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("[ResourcePack DEBUG ERROR] Exception while sending setResourcePack to " + player.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }, 40L);
    }

    @EventHandler
    public void onPackStatus(PlayerResourcePackStatusEvent event) {
        plugin.getLogger().info("[ResourcePack DEBUG STATUS] Player " + event.getPlayer().getName() + " Pack Status: " + event.getStatus());
    }
}
