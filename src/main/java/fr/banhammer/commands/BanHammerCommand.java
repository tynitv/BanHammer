package fr.banhammer.commands;

import fr.banhammer.BanHammerPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        if (args.length == 0) {
            sender.sendMessage(mm.deserialize(plugin.getMessage("usage")));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload":
                if (!sender.hasPermission("banhammer.admin")) {
                    sender.sendMessage(mm.deserialize(plugin.getMessage("no-permission")));
                    return true;
                }
                plugin.reloadPluginConfig();
                sender.sendMessage(mm.deserialize(plugin.getMessage("reloaded")));
                return true;

            case "pack":
                Player targetPlayer = null;
                if (args.length >= 2) {
                    targetPlayer = Bukkit.getPlayer(args[1]);
                } else if (sender instanceof Player playerSender) {
                    targetPlayer = playerSender;
                }

                if (targetPlayer == null) {
                    sender.sendMessage(mm.deserialize(plugin.getMessage("player-not-found")));
                    return true;
                }

                String packUrl = plugin.getConfig().getString("resource-pack.url", "http://127.0.0.1:8765/resourcepack.zip");
                byte[] hash = plugin.getResourcePackServer() != null ? plugin.getResourcePackServer().getSha1HashBytes() : null;

                if (hash != null && hash.length == 20) {
                    targetPlayer.setResourcePack(packUrl, hash);
                } else {
                    targetPlayer.setResourcePack(packUrl);
                }

                sender.sendMessage(mm.deserialize(plugin.getMessage("pack-sent")));
                return true;

            case "enchant":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cOnly players can execute this command.");
                    return true;
                }

                if (!player.hasPermission("banhammer.give") && !player.hasPermission("banhammer.admin")) {
                    player.sendMessage(mm.deserialize(plugin.getMessage("no-permission")));
                    return true;
                }

                ItemStack heldItem = player.getInventory().getItemInMainHand();
                if (!plugin.getItemManager().isBanHammer(heldItem)) {
                    player.sendMessage(mm.deserialize(plugin.getMessage("must-hold-banhammer")));
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(mm.deserialize(plugin.getMessage("usage")));
                    return true;
                }

                String enchantName = args[1];
                int level = 1;
                if (args.length >= 3) {
                    try {
                        level = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ignored) {}
                }

                Enchantment ench = plugin.getItemManager().parseEnchantment(enchantName);
                if (ench == null) {
                    player.sendMessage(mm.deserialize(plugin.getMessage("invalid-enchantment")));
                    return true;
                }

                ItemMeta meta = heldItem.getItemMeta();
                if (meta != null) {
                    meta.addEnchant(ench, level, true);
                    heldItem.setItemMeta(meta);
                    String msg = plugin.getMessage("enchanted")
                            .replace("<enchant>", ench.getKey().getKey())
                            .replace("<level>", String.valueOf(level));
                    player.sendMessage(mm.deserialize(msg));
                }
                return true;

            case "give":
            default:
                if (!sender.hasPermission("banhammer.give")) {
                    sender.sendMessage(mm.deserialize(plugin.getMessage("no-permission")));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(mm.deserialize(plugin.getMessage("usage")));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(mm.deserialize(plugin.getMessage("player-not-found")));
                    return true;
                }

                ItemStack hammer = plugin.getItemManager().getBanHammer();
                target.getInventory().addItem(hammer);
                sender.sendMessage(mm.deserialize(plugin.getMessage("given").replace("<player>", target.getName())));
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
            if ("enchant".startsWith(input)) completions.add("enchant");
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            String input = args[1].toLowerCase();

            if ("enchant".equals(sub)) {
                for (Enchantment e : Enchantment.values()) {
                    String name = e.getKey().getKey();
                    if (name.startsWith(input)) {
                        completions.add(name);
                    }
                }
            } else if ("give".equals(sub) || "pack".equals(sub)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 3 && "enchant".equalsIgnoreCase(args[0])) {
            completions.add("1");
            completions.add("3");
            completions.add("5");
        }
        return completions;
    }
}
