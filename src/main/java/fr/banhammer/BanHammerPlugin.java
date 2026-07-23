package fr.banhammer;

import fr.banhammer.commands.BanHammerCommand;
import fr.banhammer.listeners.BanHammerListener;
import fr.banhammer.managers.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BanHammerPlugin extends JavaPlugin {

    private static BanHammerPlugin instance;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Initialize Managers
        this.itemManager = new ItemManager(this);

        // Register Commands
        getCommand("banhammer").setExecutor(new BanHammerCommand(this));

        // Register Listeners
        getServer().getPluginManager().registerEvents(new BanHammerListener(this), this);

        getLogger().info("BanHammer Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BanHammer Plugin has been disabled!");
    }

    public static BanHammerPlugin getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
