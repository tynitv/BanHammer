package fr.banhammer.commands;

import fr.banhammer.BanHammerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BanHammerCommand implements CommandExecutor {

    private final BanHammerPlugin plugin;

    public BanHammerCommand(BanHammerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("banhammer.give")) {
            sender.sendMessage(plugin.getConfig().getString("permission-message", "§cYou do not have permission."));
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /banhammer give <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cUser not found.");
            return true;
        }

        ItemStack hammer = plugin.getItemManager().getBanHammer();
        target.getInventory().addItem(hammer);
        sender.sendMessage("§aBan Hammer given to " + target.getName());
        return true;
    }
}
