package fr.banhammer;

import fr.banhammer.commands.BanHammerCommand;
import fr.banhammer.listeners.BanHammerListener;
import fr.banhammer.managers.ItemManager;
import fr.banhammer.resourcepack.ResourcePackListener;
import fr.banhammer.resourcepack.ResourcePackServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class BanHammerPlugin extends JavaPlugin {

    private static BanHammerPlugin instance;
    private ItemManager itemManager;
    private ResourcePackServer resourcePackServer;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.itemManager = new ItemManager(this);

        if (getCommand("banhammer") != null) {
            getCommand("banhammer").setExecutor(new BanHammerCommand(this));
        }

        getServer().getPluginManager().registerEvents(new BanHammerListener(this), this);

        // Integrated Resource Pack Server
        if (getConfig().getBoolean("resource-pack.enabled", true)) {
            int port = getConfig().getInt("resource-pack.port", 8085);
            this.resourcePackServer = new ResourcePackServer(this, port);
            this.resourcePackServer.start();
            getServer().getPluginManager().registerEvents(new ResourcePackListener(this, port), this);
        }

        getLogger().info("BanHammer Plugin v1.1.0 enabled with Integrated ResourcePack!");
    }

    @Override
    public void onDisable() {
        if (resourcePackServer != null) {
            resourcePackServer.stop();
        }
        getLogger().info("BanHammer Plugin disabled!");
    }

    public static BanHammerPlugin getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
