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

        BanHammerCommand cmd = new BanHammerCommand(this);
        if (getCommand("banhammer") != null) {
            getCommand("banhammer").setExecutor(cmd);
            getCommand("banhammer").setTabCompleter(cmd);
        }

        getServer().getPluginManager().registerEvents(new BanHammerListener(this), this);

        // Integrated Resource Pack Server
        if (getConfig().getBoolean("resource-pack.enabled", true)) {
            int port = getConfig().getInt("resource-pack.port", 8765);
            this.resourcePackServer = new ResourcePackServer(this, port);
            this.resourcePackServer.start();
            getServer().getPluginManager().registerEvents(new ResourcePackListener(this, resourcePackServer, port), this);
        }

        getLogger().info("BanHammer Plugin v1.2.0 enabled with SHA-1 ResourcePack!");
    }

    @Override
    public void onDisable() {
        if (resourcePackServer != null) {
            resourcePackServer.stop();
        }
        getLogger().info("BanHammer Plugin v1.2.0 disabled!");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        if (resourcePackServer != null) {
            resourcePackServer.stop();
            int port = getConfig().getInt("resource-pack.port", 8765);
            this.resourcePackServer = new ResourcePackServer(this, port);
            this.resourcePackServer.start();
        }
    }

    public String getMessage(String key) {
        String lang = getConfig().getString("language", "EN").toUpperCase();
        String prefix = getConfig().getString("messages.prefix", "");
        String msg = getConfig().getString("messages." + lang + "." + key);
        if (msg == null) {
            msg = getConfig().getString("messages.EN." + key);
        }
        if (msg == null) {
            msg = getConfig().getString("messages." + key, "");
        }
        return msg.replace("<prefix>", prefix);
    }

    public static BanHammerPlugin getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public ResourcePackServer getResourcePackServer() {
        return resourcePackServer;
    }
}
