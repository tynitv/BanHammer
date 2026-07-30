package fr.banhammer.commands;

import fr.banhammer.BanHammerPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BanHammerCommand implements CommandExecutor, TabCompleter {

    private final BanHammerPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public BanHammerCommand(BanHammerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (args.length == 0) {
            sender.sendMessage(mm.deserialize(prefix + "<gray>Utilisation : </gray><yellow>/banhammer <give|reload|pack> [joueur]</yellow>"));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload":
                if (!sender.hasPermission("banhammer.admin")) {
                    sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-permission", "").replace("<prefix>", prefix)));
                    return true;
                }
                plugin.reloadPluginConfig();
                sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.reloaded", "").replace("<prefix>", prefix)));
                return true;

            case "pack":
                Player targetPlayer = null;
                if (args.length >= 2) {
                    targetPlayer = Bukkit.getPlayer(args[1]);
                } else if (sender instanceof Player playerSender) {
                    targetPlayer = playerSender;
                }

                if (targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(prefix + "<red>Joueur introuvable.</red>"));
                    return true;
                }

                String packUrl = plugin.getConfig().getString("resource-pack.url", "https://raw.githubusercontent.com/tynitv/BanHammer/main/BanHammer_ResourcePack.zip");
                byte[] hash = plugin.getResourcePackServer() != null ? plugin.getResourcePackServer().getSha1HashBytes() : null;

                if (hash != null && hash.length == 20) {
                    targetPlayer.setResourcePack(packUrl, hash);
                } else {
                    targetPlayer.setResourcePack(packUrl);
                }

                sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.pack-sent", "").replace("<prefix>", prefix)));
                return true;

            case "give":
            default:
                if (!sender.hasPermission("banhammer.give")) {
                    sender.sendMessage(mm.deserialize(plugin.getConfig().getString("messages.no-permission", "").replace("<prefix>", prefix)));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(mm.deserialize(prefix + "<red>Utilisation : /banhammer give <joueur></red>"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(mm.deserialize(prefix + "<red>Joueur introuvable.</red>"));
                    return true;
                }

                ItemStack hammer = plugin.getItemManager().getBanHammer();
                target.getInventory().addItem(hammer);
                sender.sendMessage(mm.deserialize(prefix + "<green>Ban Hammer donné à " + target.getName() + "</green>"));
                return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("give".startsWith(input)) completions.add("give");
            if ("reload".startsWith(input)) completions.add("reload");
            if ("pack".startsWith(input)) completions.add("pack");
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
        }
        return completions;
    }
}
